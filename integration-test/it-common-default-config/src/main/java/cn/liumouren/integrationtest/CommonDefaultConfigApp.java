package cn.liumouren.integrationtest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

@SpringBootApplication
public class CommonDefaultConfigApp implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(CommonDefaultConfigApp.class, args);
    }

    @Value("${spring.main.allow-bean-definition-overriding}")
    private boolean allowOverriding;
    @Value("${feign.okhttp.enabled}")
    private boolean enableOkhttp;

    @Override
    public void run(String... args) throws Exception {
        // 检查默认配置是否生效
        Assert.isTrue(enableOkhttp, "默认配置没有生效");

        // 检查默认配置优先级是否正确
        Assert.isTrue(!allowOverriding, "默认配置优先级不正确");
    }
}

