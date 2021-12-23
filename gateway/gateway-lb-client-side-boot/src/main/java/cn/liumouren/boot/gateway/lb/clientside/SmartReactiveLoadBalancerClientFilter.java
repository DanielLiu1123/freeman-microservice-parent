package cn.liumouren.boot.gateway.lb.clientside;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 20:27
 */
@Slf4j
public class SmartReactiveLoadBalancerClientFilter extends ReactiveLoadBalancerClientFilter implements BeanFactoryAware, InitializingBean {
    private BeanFactory beanFactory;
    private ReactiveDiscoveryClient reactiveDiscoveryClient;
    private LoadBalancerCacheManager cacheManager;

    public SmartReactiveLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
                                                 GatewayLoadBalancerProperties properties,
                                                 LoadBalancerProperties loadBalancerProperties) {
        super(clientFactory, properties, loadBalancerProperties);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 缓存 serviceId, 在执行 super.filter(exchange, chain) 之后, 会将 serviceId 换为真正的 ip
        URI requestUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String serviceId = requestUri.getHost();
        return super.filter(exchange, chain)
                .doOnError(IOException.class, e -> updateCacheFor(serviceId))
                .retryWhen(Retry
                        .backoff(1, Duration.ofMillis(0L)).jitter(0L)
                        .onRetryExhaustedThrow((rbs, rs) -> {
                            log.warn("请求服务[{}]失败 (重试{}次): {}", serviceId, rs.totalRetries(), requestUri);
                            return rs.failure();
                        })
                );
    }

    private void updateCacheFor(String serviceId) {
        Cache cache = cacheManager.getCache(SERVICE_INSTANCE_CACHE_NAME);
        if (cache != null) {
            // 重新获取实例列表, 更新缓存
            List<ServiceInstance> instances = reactiveDiscoveryClient.getInstances(serviceId)
                    .toStream()
                    .collect(Collectors.toList());
            cache.put(serviceId, instances);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.reactiveDiscoveryClient = beanFactory.getBean(ReactiveDiscoveryClient.class);
        this.cacheManager = beanFactory.getBean(LoadBalancerCacheManager.class);
    }
}
