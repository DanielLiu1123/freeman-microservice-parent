package cn.liumouren.integrationtest.lb.serverside;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *
 *
 * @author freeman
 * @date 2021/12/21 3:03 AM
 */
@SpringBootApplication
@EnableFeignClients("cn.liumouren.**.api")
public class ServerSideApp {
    public static void main(String[] args) {
        SpringApplication.run(ServerSideApp.class, args);
    }
}
