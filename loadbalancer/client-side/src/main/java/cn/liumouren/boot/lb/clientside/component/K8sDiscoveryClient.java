package cn.liumouren.boot.lb.clientside.component;

import cn.liumouren.boot.lb.clientside.model.K8sServiceInstance;
import cn.liumouren.boot.lb.common.FreemanK8sDiscoveryProperties;
import cn.liumouren.boot.lb.common.Namespace;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.liumouren.boot.lb.common.FreemanK8sDiscoveryProperties.DEFAULT_NAMESPACE;

/**
 * k8s 服务发现客户端
 *
 * @author freeman
 * @date 2021/12/20 15:08
 */
public class K8sDiscoveryClient implements DiscoveryClient, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(K8sDiscoveryClient.class);

    private CoreV1Api api = new CoreV1Api();
    private FreemanK8sDiscoveryProperties properties;
    private ApplicationContext applicationContext;
    private Set<String> namespaces;

    public K8sDiscoveryClient(FreemanK8sDiscoveryProperties properties) {
        this.properties = properties;
    }

    public K8sDiscoveryClient() {
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
        return "K8s Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        try {
            String namespace = getNamespace(serviceId);
            V1ServiceList serviceList = api.listNamespacedService(namespace, null, null, null, "metadata.name=" + serviceId, null, null, null, null, null, null);
            if (serviceList.getItems() == null || serviceList.getItems().size() == 0) {
                LOGGER.warn("k8s doesn't contain serviceId: " + serviceId);
                return Collections.emptyList();
            }
            if (serviceList.getItems().size() > 1) {
                LOGGER.warn("serviceId: " + serviceId + " count is more than 1, please change another service name, using first ...");
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

//    @Override
//    public List<String> getServices() {
//        try {
//            // 根据配置获取所有已配置的命名空间的 svc
//            Set<String> namespaces = getConfigNamespaces();
//            Set<String> services = new HashSet<>();
//            for (String namespace : namespaces) {
//                V1ServiceList serviceList = api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, null, null);
//                List<String> servicesForNamespace = serviceList.getItems().stream()
//                        .map(svc -> svc.getMetadata().getName())
//                        .collect(Collectors.toList());
//                services.addAll(servicesForNamespace);
//            }
//            return new ArrayList<>(services);
//        } catch (Exception e) {
//            LOGGER.error("get service name from k8s fail,", e);
//            return Collections.emptyList();
//        }
//    }

    @Override
    public List<String> getServices() {
        try {
            return new ArrayList<>(getAllServiceId());
        } catch (Exception e) {
            LOGGER.error("get service name from k8s fail,", e);
            return Collections.emptyList();
        }
    }

    private Set<String> getAllServiceId() {
        // 根据 @FeignClient 注解获取所有的 svc
        Set<String> result = new LinkedHashSet<>();
        List<String> feigns = Arrays.stream(applicationContext.getBeanNamesForAnnotation(FeignClient.class))
                .filter(s -> s.contains(".")) // 过滤掉 controller
                .collect(Collectors.toList());
        for (String feign : feigns) {
            try {
                FeignClient annotation = Class.forName(feign).getAnnotation(FeignClient.class);
                Optional.ofNullable(AnnotationUtils.getValue(annotation))
                        .ifPresent(val -> result.add(val.toString()));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return result;
    }

    private Set<String> getConfigNamespaces() {
        if (namespaces == null) {
            // 扫描 @Namespace 注解
            namespaces = new LinkedHashSet<>();
            List<String> feigns = Arrays.stream(applicationContext.getBeanNamesForAnnotation(FeignClient.class))
                    .filter(s -> s.contains(".")) // 过滤掉 controller
                    .collect(Collectors.toList());
            for (String feign : feigns) {
                try {
                    Namespace namespace = Class.forName(feign).getAnnotation(Namespace.class);
                    if (namespace != null) {
                        namespaces.add(namespace.value());
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        return namespaces;
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

    private String getNamespace(String serviceId) {
        Map<String, List<String>> map = properties.getMappings();
        if (map != null) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                for (String svc : entry.getValue()) {
                    if (Objects.equals(serviceId, svc)) {
                        return entry.getKey();
                    }
                }
            }
        }
        return DEFAULT_NAMESPACE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
