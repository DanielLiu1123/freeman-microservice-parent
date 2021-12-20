package cn.liumouren.boot.lb.clientside.component;

import cn.liumouren.boot.lb.clientside.model.K8sServiceInstance;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * k8s 服务发现客户端
 *
 * @author freeman
 * @date 2021/12/20 15:08
 */
public class K8sDiscoveryClient implements DiscoveryClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(K8sDiscoveryClient.class);

    private final CoreV1Api api = new CoreV1Api();

    static {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            throw new RuntimeException("can't create ApiClient");
        }
    }

    @Override
    public String description() {
        return "K8s Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        try {
            V1ServiceList serviceList = api.listServiceForAllNamespaces(null, null, "metadata.name=" + serviceId, null, null, null, null, null, null, null);
            if (serviceList.getItems() == null || serviceList.getItems().size() == 0) {
                LOGGER.warn("k8s doesn't contain serviceId: " + serviceId);
                return Collections.emptyList();
            }
            if (serviceList.getItems().size() > 1) {
                LOGGER.warn("serviceId: " + serviceId + " count is more than 1, please change another service name, using first ...");
            }
            V1Service service = serviceList.getItems().get(0);
            final String labelSelectors = getLabelSelectors(service);
            V1PodList podList = api.listPodForAllNamespaces(null, null, null, labelSelectors, null, null, null, null, null, null);
            return podList.getItems().stream()
                    .map(pod -> toServiceInstance(serviceId, pod))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("get instances from k8s fail,", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getServices() {
        try {
            V1ServiceList serviceList = api.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            return serviceList.getItems().stream()
                    .map(svc -> svc.getMetadata().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("get service name from k8s fail,", e);
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
            instance.setPort(pod.getSpec().getContainers().get(0).getPorts().get(0).getHostPort());
        } catch (Exception e) {
            instance.setPort(-1);
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

}
