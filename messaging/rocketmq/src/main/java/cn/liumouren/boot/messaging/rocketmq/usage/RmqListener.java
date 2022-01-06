//package cn.liumouren.boot.messaging.rocketmq.usage;
//
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.springframework.stereotype.Component;
//
///**
// *
// *
// * @author freeman
// * @date 2021/12/28 21:31
// */
//@Component
//@RocketMQMessageListener(
//        consumerGroup = "order_group",
//        topic = "order",
//        selectorExpression = "tag01 || tag02",
//        consumeMode = ConsumeMode.CONCURRENTLY,
//        messageModel = MessageModel.CLUSTERING
//)
//public class RmqListener implements RocketMQListener<SomeEvent> {
//    @Override
//    public void onMessage(SomeEvent message) {
//
//    }
//}
