package cn.liumouren.boot.gateway.lb.clientside;

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 19:31
 */
public class ReactorK8sDiscoveryClient implements ReactiveDiscoveryClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorK8sDiscoveryClient.class);

    private CoreV1Api api = new CoreV1Api();
    private GatewayProperties gatewayProperties;

    public ReactorK8sDiscoveryClient(GatewayProperties properties) {
        this.gatewayProperties = properties;
    }

    static {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            throw new RuntimeException("can't create ApiClient");
        }
    }

    @Override
    public String description() {
        return "K8s Reactive Discovery Client";
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return Mono.justOrEmpty(serviceId)
                .flatMapMany(svc -> Flux.fromIterable(getPods(svc)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public List<ServiceInstance> getPods(String serviceId) {
        try {
            String namespace = getNamespace(serviceId);
            V1ServiceList serviceList = api.listNamespacedService(namespace, null, null, null, "metadata.name=" + serviceId, null, null, null, null, null, null);
            if (serviceList.getItems() == null || serviceList.getItems().size() == 0) {
                LOGGER.warn("k8s doesn't contain serviceId [" + serviceId + "] in namespace [" + namespace + "]");
                return Collections.emptyList();
            }
            if (serviceList.getItems().size() > 1) {
                LOGGER.warn("serviceId [" + serviceId + "] in namespace [" + namespace + "] count is more than 1, please change another service name, using first ...");
            }

            V1Service service = serviceList.getItems().get(0);
            String labelSelectors = getLabelSelectors(service);
            V1PodList podList = api.listPodForAllNamespaces(null, null, null, labelSelectors, null, null, null, null, null, null);
            return podList.getItems().stream()
                    .map(pod -> toServiceInstance(serviceId, pod))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("get instances from k8s fail,", e);
            return Collections.emptyList();
        }
    }

    private String getLabelSelectors(V1Service service) {
        Map<String, String> selectors = service.getSpec().getSelector();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : selectors.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private ServiceInstance toServiceInstance(String serviceId, V1Pod pod) {
        K8sServiceInstance instance = new K8sServiceInstance();
        instance.setHost(pod.getStatus().getPodIP());
        try {
            instance.setPort(pod.getSpec().getContainers().get(0).getPorts().get(0).getContainerPort());
        } catch (Exception e) {
            instance.setPort(80);
        }
        instance.setSecure(false);
        instance.setServiceId(serviceId);
        Map<String, String> metadata = new HashMap<>();
        V1ObjectMeta md = pod.getMetadata();
        if (md != null) {
            metadata.putAll(Optional.ofNullable(md.getLabels()).orElse(Collections.emptyMap()));
            metadata.putAll(Optional.ofNullable(md.getAnnotations()).orElse(Collections.emptyMap()));
        }
        instance.setMetadata(metadata);
        return instance;
    }

    private String getNamespace(String serviceId) {
        List<RouteDefinition> routes = gatewayProperties.getRoutes();
        if (routes != null) {
            for (RouteDefinition route : routes) {
                String url = route.getUri().toString();
                if (url.startsWith("lb://")) {
                    String svc = route.getUri().getHost();
                    if (Objects.equals(serviceId, svc)) {
                        Object namespace = route.getMetadata().get("namespace");
                        if (namespace != null) {
                            return namespace.toString();
                        }
                    }
                }
            }
        }
        return "default";
    }

    @Override
    public Flux<String> getServices() {
        return Mono.justOrEmpty(gatewayProperties.getRoutes())
                .flatMapMany(routes -> Flux.fromIterable(getSvcFromProperties(routes)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Set<String> getSvcFromProperties(List<RouteDefinition> routes) {
        return routes.stream()
                .map(def -> {
                    String url = def.getUri().toString();
                    if (url.startsWith("lb://")) {
                        return def.getUri().getHost();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
