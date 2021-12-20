package cn.liumouren.boot.lb.clientside;

import cn.hutool.core.util.ReflectUtil;
import cn.liumouren.boot.lb.clientside.component.K8sDiscoveryClient;
import cn.liumouren.boot.lb.clientside.component.SmartFeignBlockingLoadBalancerClient;
import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignLoadBalancerAutoConfiguration;
import org.springframework.cloud.openfeign.loadbalancer.OnRetryNotEnabledCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 14:17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LoadbalancerClientSideProperties.class)
public class LbClientSideAutoConfiguration {

    @Bean
    public K8sDiscoveryClient k8sDiscoveryClient() {
        return new K8sDiscoveryClient();
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
