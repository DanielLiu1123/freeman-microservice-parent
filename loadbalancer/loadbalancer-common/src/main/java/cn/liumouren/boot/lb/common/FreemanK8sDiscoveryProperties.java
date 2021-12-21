package cn.liumouren.boot.lb.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 15:13
 */
@Data
@ConfigurationProperties(FreemanK8sDiscoveryProperties.PREFIX)
public class FreemanK8sDiscoveryProperties {
    public static final String PREFIX = "freeman.discovery.k8s";

    public static final String DEFAULT_NAMESPACE = "default";

    /**
     * 当前服务所在 k8s 命名空间, 默认 default
     */
    private String namespace = DEFAULT_NAMESPACE;

    /**
     * namespace -> [svc-01, svc-02]
     */
    private Map<String, List<String>> mappings;

}
