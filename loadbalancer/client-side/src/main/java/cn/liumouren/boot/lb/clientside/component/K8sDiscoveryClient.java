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
    /**
     * namespace -> [service01,service-02]
     */
    private Map<String, List<String>> mappings = new HashMap<>();

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

    @Override
    public List<String> getServices() {
        try {
            return mappings.values().stream()
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("get service name from k8s fail,", e);
            return Collections.emptyList();
        }
    }

    private List<String> getFeignClientBeanNames(ApplicationContext applicationContext) {
        return Arrays.stream(applicationContext.getBeanNamesForAnnotation(FeignClient.class))
                .filter(s -> s.contains(".")) // 过滤掉 controller
                .collect(Collectors.toList());
    }

    private void populateMappings() {
        Map<String, String> serviceNamespaceMapping = new HashMap<>();

        // 先找配置文件
        if (properties.getMappings() != null) {
            for (Map.Entry<String, List<String>> namespaceServices : properties.getMappings().entrySet()) {
                if (namespaceServices != null) {
                    for (String svc : namespaceServices.getValue()) {
                        serviceNamespaceMapping.put(svc, namespaceServices.getKey());
                    }
                }
            }
        }

        // 再找 @Namespace 注解, 会覆盖配置文件
        List<String> feigns = getFeignClientBeanNames(applicationContext);
        for (String feign : feigns) {
            try {
                Class<?> clz = Class.forName(feign);
                FeignClient fcAnno = clz.getAnnotation(FeignClient.class);
                Namespace nsAnno = clz.getAnnotation(Namespace.class);
                Object svc = AnnotationUtils.getValue(fcAnno);
                Object ns = AnnotationUtils.getValue(nsAnno);
                if (svc != null && ns != null) {
                    serviceNamespaceMapping.put(svc.toString(), ns.toString());
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        Set<String> nss = new HashSet<>(serviceNamespaceMapping.values());
        for (String ns : nss) {
            for (Map.Entry<String, String> svcNamespace : serviceNamespaceMapping.entrySet()) {
                if (Objects.equals(ns, svcNamespace.getValue())) {
                    List<String> svcs = new ArrayList<>();
                    svcs.add(svcNamespace.getKey());
                    mappings.merge(ns, svcs, (oldVal, newVal) -> {
                        oldVal.addAll(newVal);
                        return oldVal;
                    });
                }
            }
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

    private String getNamespace(String serviceId) {
        for (Map.Entry<String, List<String>> namespaceServices : mappings.entrySet()) {
            for (String svc : namespaceServices.getValue()) {
                if (Objects.equals(svc, serviceId)) {
                    return namespaceServices.getKey();
                }
            }
        }
        return DEFAULT_NAMESPACE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        populateMappings();
    }

}
