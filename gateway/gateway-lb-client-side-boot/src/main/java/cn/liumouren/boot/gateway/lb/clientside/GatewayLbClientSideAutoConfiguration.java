package cn.liumouren.boot.gateway.lb.clientside;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 20:23
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(GatewayReactiveLoadBalancerClientAutoConfiguration.class)
public class GatewayLbClientSideAutoConfiguration {

    @Bean
    public ReactiveDiscoveryClient reactorK8sDiscoveryClient(GatewayProperties properties) {
        return new ReactorK8sDiscoveryClient(properties);
    }

    @Bean
    public ReactiveLoadBalancerClientFilter smartReactiveLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
                                                                                  GatewayLoadBalancerProperties properties,
                                                                                  LoadBalancerProperties loadBalancerProperties) {
        return new SmartReactiveLoadBalancerClientFilter(clientFactory, properties, loadBalancerProperties);
    }


}
