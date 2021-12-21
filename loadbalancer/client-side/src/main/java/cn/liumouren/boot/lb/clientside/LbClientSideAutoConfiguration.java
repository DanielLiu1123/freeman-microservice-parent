package cn.liumouren.boot.lb.clientside;

import cn.hutool.core.util.ReflectUtil;
import cn.liumouren.boot.lb.clientside.component.K8sDiscoveryClient;
import cn.liumouren.boot.lb.clientside.component.SmartFeignBlockingLoadBalancerClient;
import cn.liumouren.boot.lb.common.FreemanK8sDiscoveryProperties;
import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 14:17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FreemanK8sDiscoveryProperties.class)
public class LbClientSideAutoConfiguration {

    @Bean
    public K8sDiscoveryClient k8sDiscoveryClient(FreemanK8sDiscoveryProperties properties) {
        return new K8sDiscoveryClient(properties);
    }

    @Bean
    public Client smartFeignBlockingLoadBalancerClient(okhttp3.OkHttpClient okHttpClient,
                                                       LoadBalancerClient loadBalancerClient,
                                                       LoadBalancerProperties properties,
                                                       LoadBalancerClientFactory loadBalancerClientFactory) {
        OkHttpClient delegate = ReflectUtil.newInstance(OkHttpClient.class, okHttpClient);
        return new SmartFeignBlockingLoadBalancerClient(delegate, loadBalancerClient, properties, loadBalancerClientFactory);
    }


}
