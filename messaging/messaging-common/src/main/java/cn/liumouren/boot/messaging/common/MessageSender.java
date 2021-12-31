package cn.liumouren.boot.messaging.common;


import org.springframework.lang.Nullable;

/**
 *
 *
 * @author freeman
 * @date 2021/12/28 20:56
 */
public interface MessageSender {

    /**
     * 同步发送消息
     * @param payload payload
     */
    void send(Object payload);

    /**
     * 发送事务消息
     * @param payload payload
     */
    void sendTransactionalMessage(Object payload);

    /**
     * 发送延时消失
     * @param payload payload
     * @param delay 延迟时间
     */
    void sendDelayMessage(Object payload, long delay);

    /**
     * 异步发送消息
     * @param payload payload
     * @param callback 回调函数
     */
    void asyncSend(Object payload, @Nullable Callback callback);

    /**
     * 获取 payload 上的 @Event 注解
     * @param payload payload
     * @return {@link Event}
     * @throws IllegalArgumentException @Event not exist
     */
    default Event getEvent(Object payload) {
        Event event = payload.getClass().getAnnotation(Event.class);
        if (event == null) {
            throw new IllegalArgumentException(payload.getClass() + " 需要包含 @Event 注解");
        }
        return event;
    }

}
