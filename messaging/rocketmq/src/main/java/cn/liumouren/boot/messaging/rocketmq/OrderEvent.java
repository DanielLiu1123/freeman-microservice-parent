package cn.liumouren.boot.messaging.rocketmq;

import cn.liumouren.boot.messaging.common.Event;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 *
 * @author freeman
 * @date 2021/12/28 16:31
 */
@Data
@Accessors(chain = true)
@Event(topic = "order", tag = "order")
public class OrderEvent {
    private String name;
}


