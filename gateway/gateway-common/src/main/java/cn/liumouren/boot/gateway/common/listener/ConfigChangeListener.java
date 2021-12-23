package cn.liumouren.boot.gateway.common.listener;

import cn.liumouren.boot.gateway.common.listener.event.ConfigChangeEvent;
import cn.liumouren.boot.gateway.common.GatewayRuleProperties;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;

/**
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/10/13 14:46
 */
public class ConfigChangeListener implements ApplicationListener<ConfigChangeEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigChangeListener.class);

    @Override
    public void onApplicationEvent(ConfigChangeEvent event) {
        GatewayRuleProperties properties = (GatewayRuleProperties) event.getSource();

        // TODO 保留数据源中的规则?
        GatewayRuleManager.loadRules(new HashSet<>(properties.getFlows()));

        LOGGER.info("更新配置: {}", JSON.toJSONString(properties.getFlows(), SerializerFeature.PrettyFormat));
    }

}
