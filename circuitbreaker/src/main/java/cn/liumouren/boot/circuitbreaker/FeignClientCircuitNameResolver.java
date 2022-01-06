package cn.liumouren.boot.circuitbreaker;

import feign.Feign;
import feign.Target;
import org.springframework.cloud.client.circuitbreaker.AbstractCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 17:45
 */
public class FeignClientCircuitNameResolver implements CircuitBreakerNameResolver {

    private Map configurations;

    public FeignClientCircuitNameResolver(CircuitBreakerFactory factory) {
        configurations = getConfigurations(factory);
    }

    @Override
    public String resolveCircuitBreakerName(String feignClientName,
                                            Target<?> target, Method method) {
        String key = Feign.configKey(target.type(), method);

        if (configurations != null && configurations.containsKey(key)) {
            return key;
        }

        return feignClientName;
    }

    private Map getConfigurations(CircuitBreakerFactory factory) {
        try {
            Method getConfigurations = ReflectionUtils.findMethod(AbstractCircuitBreakerFactory.class, "getConfigurations");
            getConfigurations.setAccessible(true);
            return (Map) ReflectionUtils.invokeMethod(getConfigurations, factory);
        } catch (Exception ignored) {
        }
        return Collections.emptyMap();
    }

}
