package cn.liumouren.boot.messaging.rabbitmq;

import cn.liumouren.boot.messaging.common.Callback;
import cn.liumouren.boot.messaging.common.Event;
import cn.liumouren.boot.messaging.common.MessageSender;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 *
 *
 * @author freeman
 * @date 2021/12/28 20:57
 */
public class RabbitMQMessageSender implements MessageSender {

    private RabbitTemplate rabbitTemplate;

    public RabbitMQMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(Object payload) {
        Event event = getEvent(payload);
        send(event.topic(), event.tag(), payload);
    }

    public void send(String topic, String tag, Object payload) {
        rabbitTemplate.convertAndSend(topic, tag, payload);
    }

    @Override
    public void sendTransactionalMessage(Object payload) {
        Event event = getEvent(payload);
        sendTransactionalMessage(event.topic(), event.tag(), payload);
    }

    public void sendTransactionalMessage(String topic, String tag, Object payload) {
        // rabbitmq 貌似不支持事务消息?
        throw new UnsupportedOperationException("RabbitMQ 暂不支持事务消息");
    }

    /**
     * RocketMQ 不支持自定义延迟时间, 这里的 delay 参数意义为 delay level
     * @see <a href="https://github.com/apache/rocketmq/blob/master/docs/cn/RocketMQ_Example.md#32-%E5%8F%91%E9%80%81%E5%BB%B6%E6%97%B6%E6%B6%88%E6%81%AFc">delay level</a>
     */
    @Override
    public void sendDelayMessage(Object payload, long delay) {
        Event event = getEvent(payload);
        sendDelayMessage(event.topic(), event.tag(), payload, delay);
    }

    public void sendDelayMessage(String topic, String tag, Object payload, long delayLevel) {
        // TODO 延迟消息可以使用死信队列完成

    }

    @Override
    public void asyncSend(Object payload, Callback callback) {
        Event event = getEvent(payload);
        asyncSend(event.topic(), event.tag(), payload, callback);
    }

    public void asyncSend(String topic, String tag, Object payload, Callback callback) {
        rabbitTemplate.convertAndSend(topic + ":" + tag, payload, new CorrelationData());
    }

}
