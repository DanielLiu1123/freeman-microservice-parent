package cn.liumouren.boot.gateway.common;

import cn.liumouren.boot.gateway.common.listener.ConfigChangeListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 18:19
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GatewayRuleProperties.class)
public class GatewayCommonAutoConfiguration {

    @Bean
    public ConfigChangeListener configChangeListener(GatewayRuleProperties properties) {
        return new ConfigChangeListener(properties);
    }

}
