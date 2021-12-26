package cn.liumouren.boot.gateway.common;

import cn.liumouren.boot.gateway.common.filter.GatewayLogWebFilter;
import cn.liumouren.boot.gateway.common.listener.ConfigChangeListener;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.server.WebFilter;

import java.util.HashSet;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 18:19
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GatewayRuleProperties.class)
public class GatewayCommonAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(GatewayCommonAutoConfiguration.class);

    private GatewayRuleProperties properties;

    public GatewayCommonAutoConfiguration(GatewayRuleProperties properties) {
        this.properties = properties;
        initRules();
    }

    private void initRules() {
        GatewayRuleManager.loadRules(new HashSet<>(properties.getFlows()));
        log.info("加载流控配置: {}", JSON.toJSONString(properties.getFlows(), SerializerFeature.PrettyFormat));
    }

    @Bean
    public ConfigChangeListener configChangeListener() {
        return new ConfigChangeListener(properties);
    }

    @Bean
    public HttpClientCustomizer fromAppHeaderCustomizer(Environment environment) {
        return httpClient -> httpClient.headers(header -> {
            if (!header.contains("From-App")) {
                header.add("From-App", environment.getProperty("spring.application.name"));
            }
        });
    }

    @Bean
    public WebFilter gatewayLogWebFilter() {
        return new GatewayLogWebFilter();
    }

}
