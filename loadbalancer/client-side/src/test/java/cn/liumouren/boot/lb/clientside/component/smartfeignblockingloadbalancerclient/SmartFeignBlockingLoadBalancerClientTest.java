package cn.liumouren.boot.lb.clientside.component.smartfeignblockingloadbalancerclient;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.liumouren.boot.lb.clientside.component.SmartFeignBlockingLoadBalancerClient;
import cn.liumouren.boot.lb.clientside.model.K8sServiceInstance;
import feign.okhttp.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.loadbalancer.cache.CaffeineBasedLoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME;

/**
 *
 *
 * @author freeman
 * @date 2021/12/22 23:39
 */
public class SmartFeignBlockingLoadBalancerClientTest {

    //    @BeforeAll
    static void init() throws IOException {
        var loadBalancerProperties = new LoadBalancerProperties();

        var properties = new LoadBalancerCacheProperties();
        properties.setTtl(Duration.ofSeconds(10L));
        properties.setCapacity(32);

        var client = new OkHttpClient();

        var lbClient = mock(LoadBalancerClient.class);
        when(lbClient.execute(any(), any()))
                .thenThrow(ConnectException.class);

        var lbFactory = mock(LoadBalancerClientFactory.class);
        when(lbFactory.getInstance(any(), any(Class.class)))
                .thenReturn(Collections.emptyMap());

        var discoveryClient = mock(DiscoveryClient.class);
        when(discoveryClient.getInstances(any()))
                .thenReturn(Arrays.asList(mock(ServiceInstance.class), mock(ServiceInstance.class)));

        var cacheManager = new CaffeineBasedLoadBalancerCacheManager(properties);

        var sfblbClient = new SmartFeignBlockingLoadBalancerClient(client, lbClient, loadBalancerProperties, lbFactory);
        ReflectionTestUtils.setField(sfblbClient, "discoveryClient", discoveryClient);
        ReflectionTestUtils.setField(sfblbClient, "cacheManager", cacheManager);
    }

    @Test
    public void test_caffeineCache() {
        var properties = new LoadBalancerCacheProperties();
        properties.setTtl(Duration.ofMillis(500L));
        properties.setCapacity(32);
        var cacheManager = new CaffeineBasedLoadBalancerCacheManager(properties);

        Cache cache = cacheManager.getCache(SERVICE_INSTANCE_CACHE_NAME);
        cache.put("svc-01", createInstances(2));

        Assertions.assertEquals(2, cache.get("svc-01", List.class).size());

        // 验证缓存过期
        ThreadUtil.sleep(1000L);
        Assertions.assertTrue(CollUtil.isEmpty(cache.get("svc-01", List.class)));
    }

    private List<ServiceInstance> createInstances(int n) {
        List<ServiceInstance> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            K8sServiceInstance instance = new K8sServiceInstance();
            instance.setPort(80);
            instance.setHost("127.0.0.1");
            instance.setServiceId("svc-01");
            instance.setSecure(false);
            instance.setMetadata(Collections.emptyMap());
            result.add(instance);
        }
        return result;
    }

}
