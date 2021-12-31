package cn.liumouren.boot.messaging.rocketmq;

import cn.liumouren.boot.messaging.common.MessageSender;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author freeman
 * @date 2021/12/28 02:33
 */
@Configuration(proxyBeanMethods = false)
public class RocketmqMessagingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessageSender rocketMQMessageSender(RocketMQTemplate rocketMQTemplate) {
        return new RocketMQMessageSender(rocketMQTemplate);
    }

}
