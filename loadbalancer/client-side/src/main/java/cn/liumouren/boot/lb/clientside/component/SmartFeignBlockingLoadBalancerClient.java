package cn.liumouren.boot.lb.clientside.component;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.Cache;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;

/**
 * 扩展 {@link FeignBlockingLoadBalancerClient}
 *
 * <p> 在请求失败时更新缓存实例
 *
 * @author freeman
 * @date 2021/12/20 20:57
 */
public class SmartFeignBlockingLoadBalancerClient extends FeignBlockingLoadBalancerClient implements BeanFactoryAware, SmartInitializingSingleton {

    private BeanFactory beanFactory;
    private DiscoveryClient discoveryClient;
    private LoadBalancerCacheManager cacheManager;

    public SmartFeignBlockingLoadBalancerClient(Client delegate,
                                                LoadBalancerClient loadBalancerClient,
                                                LoadBalancerProperties properties,
                                                LoadBalancerClientFactory loadBalancerClientFactory) {
        super(delegate, loadBalancerClient, properties, loadBalancerClientFactory);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        try {
            return super.execute(request, options);
        } catch (ConnectException e) {
            // 连接失败, 说明实例下线, 我们更新缓存
            Cache cache = cacheManager.getCache(CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME);
            if (cache == null) {
                throw e;
            }

            // 重新获取实例列表, 更新缓存
            String serviceId = URI.create(request.url()).getHost();
            cache.put(serviceId, discoveryClient.getInstances(serviceId));

            // 再次发送请求
            return super.execute(request, options);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        cacheManager = beanFactory.getBean(LoadBalancerCacheManager.class);
        discoveryClient = beanFactory.getBean(DiscoveryClient.class);
    }
}
