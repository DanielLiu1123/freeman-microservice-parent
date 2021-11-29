package cn.liumouren.integrationtest.openfeign;

import cn.liumouren.integrationtest.openfeign.api.SomeApi;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @author freeman
 * @date 2021/11/29 14:12
 */
@SpringBootApplication
@EnableFeignClients
public class TestOpenfeignApp implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(TestOpenfeignApp.class, args);
    }

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Autowired
    @Qualifier("cn.liumouren.integrationtest.openfeign.api.SomeApi")
    SomeApi someApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(someApi.getClass());
        String freeman = someApi.some("freeman");
        System.out.println(freeman);
    }
}
