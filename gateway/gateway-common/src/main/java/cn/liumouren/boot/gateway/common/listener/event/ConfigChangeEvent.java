package cn.liumouren.boot.gateway.common.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 *
 *
 * @author freeman
 * @date 2021/12/23 16:37
 */
public class ConfigChangeEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     * which the event is associated (never {@code null})
     */
    public ConfigChangeEvent(Object source) {
        super(source);
    }
}
