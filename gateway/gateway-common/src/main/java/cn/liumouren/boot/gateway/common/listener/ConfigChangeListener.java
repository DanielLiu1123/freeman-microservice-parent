package cn.liumouren.boot.gateway.common.listener;

import cn.liumouren.boot.gateway.common.GatewayRuleProperties;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/10/13 14:46
 */
public class ConfigChangeListener implements ApplicationListener<RefreshScopeRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigChangeListener.class);

    private GatewayRuleProperties properties;

    public ConfigChangeListener(GatewayRuleProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // TODO 保留数据源中的规则?
        GatewayRuleManager.loadRules(new HashSet<>(properties.getFlows()));

        LOGGER.info("更新流控配置: {}", JSON.toJSONString(properties.getFlows(), SerializerFeature.PrettyFormat));
    }
}
