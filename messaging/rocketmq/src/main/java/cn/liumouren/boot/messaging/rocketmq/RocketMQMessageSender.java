package cn.liumouren.boot.messaging.rocketmq;

import cn.liumouren.boot.messaging.common.Callback;
import cn.liumouren.boot.messaging.common.Event;
import cn.liumouren.boot.messaging.common.MessageSender;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;

/**
 *
 *
 * @author freeman
 * @date 2021/12/28 20:57
 */
public class RocketMQMessageSender implements MessageSender {
    public static final String TX_TRANSACTION_GROUP = "tx_transaction_group";

    private RocketMQTemplate rocketMQTemplate;

    public RocketMQMessageSender(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public void send(Object payload) {
        Event event = getEvent(payload);
        send(event.topic(), event.tag(), payload);
    }

    public void send(String topic, String tag, Object payload) {
        rocketMQTemplate.syncSend(topic + ":" + tag, payload);
    }

    @Override
    public void sendTransactionalMessage(Object payload) {
        Event event = getEvent(payload);
        sendTransactionalMessage(event.topic(), event.tag(), payload);
    }

    public void sendTransactionalMessage(String topic, String tag, Object payload) {
        rocketMQTemplate.sendMessageInTransaction(TX_TRANSACTION_GROUP, topic + ":" + tag, MessageBuilder.withPayload(payload).build(), null);
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
        rocketMQTemplate.syncSend(topic + ":" + tag, MessageBuilder.withPayload(payload).build(), rocketMQTemplate.getProducer().getSendMsgTimeout(), (int) delayLevel);
    }

    @Override
    public void asyncSend(Object payload, Callback callback) {
        Event event = getEvent(payload);
        asyncSend(event.topic(), event.tag(), payload, callback);
    }

    public void asyncSend(String topic, String tag, Object payload, Callback callback) {
        rocketMQTemplate.asyncSend(topic + ":" + tag, payload, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if (callback != null) {
                    callback.onSuccess(sendResult);
                }
            }

            @Override
            public void onException(Throwable e) {
                if (callback != null) {
                    callback.onException(e);
                }
            }
        });
    }

}
