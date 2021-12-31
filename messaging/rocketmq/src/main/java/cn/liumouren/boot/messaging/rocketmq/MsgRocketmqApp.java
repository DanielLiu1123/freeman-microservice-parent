package cn.liumouren.boot.messaging.rocketmq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsgRocketmqApp {
    public static void main(String[] args) {
        SpringApplication.run(MsgRocketmqApp.class, args);
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void dodo() {

    }
}
