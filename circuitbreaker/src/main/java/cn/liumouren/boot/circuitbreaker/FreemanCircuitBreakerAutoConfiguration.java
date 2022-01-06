package cn.liumouren.boot.circuitbreaker;

import com.alibaba.cloud.circuitbreaker.sentinel.ReactiveSentinelCircuitBreakerFactory;
import com.alibaba.cloud.circuitbreaker.sentinel.SentinelCircuitBreakerFactory;
import com.alibaba.cloud.circuitbreaker.sentinel.SentinelConfigBuilder;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import feign.Feign;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.AbstractCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/24 17:58
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ Feign.class, FeignClientFactoryBean.class })
@ConditionalOnProperty(name = "spring.cloud.circuitbreaker.sentinel.enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SentinelFeignClientProperties.class)
public class FreemanCircuitBreakerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerNameResolver.class)
    public CircuitBreakerNameResolver feignClientCircuitNameResolver(CircuitBreakerFactory factory) {
        return new FeignClientCircuitNameResolver(factory);
    }

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerFactory.class)
    public CircuitBreakerFactory sentinelCircuitBreakerFactory(ObjectProvider<List<Customizer<SentinelCircuitBreakerFactory>>> provider) {
        SentinelCircuitBreakerFactory factory = new SentinelCircuitBreakerFactory();
        provider.ifAvailable(customizers -> customizers.forEach(customizer -> customizer.customize(factory)));
        return factory;
    }

    @Configuration(proxyBeanMethods = false)
    public static class SentinelCustomizerConfiguration {

        @Bean
        public Customizer<SentinelCircuitBreakerFactory> configureRulesCustomizer(
                SentinelFeignClientProperties properties) {
            return factory -> {
                configureDefault(properties, factory);
                configureCustom(properties, factory);
            };
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = { "reactor.core.publisher.Mono",
            "reactor.core.publisher.Flux" })
    public static class ReactiveSentinelCustomizerConfiguration {

        @Bean
        public Customizer<ReactiveSentinelCircuitBreakerFactory> reactiveConfigureRulesCustomizer(
                SentinelFeignClientProperties properties) {
            return factory -> {
                configureDefault(properties, factory);
                configureCustom(properties, factory);
            };
        }

    }

    private static void configureCustom(SentinelFeignClientProperties properties,
                                        AbstractCircuitBreakerFactory factory) {
        properties.getRules().forEach((resourceName, degradeRules) -> {
            if (!Objects.equals(properties.getDefaultRule(), resourceName)) {
                factory.configure(builder -> ((SentinelConfigBuilder) builder)
                                .rules(properties.getRules().getOrDefault(resourceName,
                                        new ArrayList<>())),
                        resourceName);
            }
        });
    }

    private static void configureDefault(SentinelFeignClientProperties properties,
                                         AbstractCircuitBreakerFactory factory) {
        List<DegradeRule> defaultConfigurations = properties.getRules()
                .getOrDefault(properties.getDefaultRule(), new ArrayList<>());
        factory.configureDefault(
                resourceName -> new SentinelConfigBuilder(resourceName.toString())
                        .entryType(EntryType.OUT).rules(defaultConfigurations).build());
    }

}
