package cn.liumouren.boot.gateway.common;

import cn.liumouren.boot.gateway.common.listener.ConfigChangeListener;
import cn.liumouren.boot.gateway.common.listener.event.ConfigChangeEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
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
    public ApplicationListener<ConfigChangeEvent> configChangeListener() {
        return new ConfigChangeListener();
    }

}
