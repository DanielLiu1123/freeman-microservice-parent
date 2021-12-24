package cn.liumouren.boot.gateway.common;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
public class GatewayRuleProperties {
    public static final String PREFIX = "freeman.gateway.rules";

    private List<GatewayFlowRule> flows = new ArrayList<>();

}
