package cn.liumouren.boot.lb.serverside;

import cn.liumouren.boot.lb.common.FreemanK8sDiscoveryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 14:17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FreemanK8sDiscoveryProperties.class)
public class LbServerSideAutoConfiguration {

}
