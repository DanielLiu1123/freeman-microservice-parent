package cn.liumouren.boot.messaging.rabbitmq;

import cn.liumouren.boot.messaging.common.MessageSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
public class RabbitmqMessagingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessageSender rabbitMQMessageSender(RabbitTemplate rabbitTemplate) {
        return new RabbitMQMessageSender(rabbitTemplate);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
