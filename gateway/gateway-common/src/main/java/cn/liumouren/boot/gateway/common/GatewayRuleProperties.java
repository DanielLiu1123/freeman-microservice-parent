package cn.liumouren.boot.gateway.common;

import cn.liumouren.boot.gateway.common.listener.event.ConfigChangeEvent;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * Sentinel 网关规则配置
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/10/13 14:36
 * @see com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule
 */
@Data
@ConfigurationProperties(GatewayRuleProperties.PREFIX)
public class GatewayRuleProperties implements ApplicationContextAware {
    public static final String PREFIX = "freeman.gateway.rules";
    private static int changeCount = 0;

    private transient ApplicationContext applicationContext;

    private List<GatewayFlowRule> flows = new ArrayList<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        if (changeCount > 0) {
            // 防止在容器第一次加载时, 触发该事件
            if (applicationContext != null) {
                applicationContext.publishEvent(new ConfigChangeEvent(this));
            }
        }
        changeCount++;
    }

}
