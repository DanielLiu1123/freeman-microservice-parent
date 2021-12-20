package cn.liumouren.boot.lb.clientside;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static cn.liumouren.boot.lb.clientside.LoadbalancerClientSideProperties.PREFIX;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 15:13
 */
@Data
@ConfigurationProperties(PREFIX)
public class LoadbalancerClientSideProperties {

    public static final String PREFIX = "freeman.loadbalancer.client-side";

}
