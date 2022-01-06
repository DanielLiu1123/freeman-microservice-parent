package cn.liumouren.integrationtest.circuitbreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CircuitBreakerApp {
    public static void main(String[] args) {
        SpringApplication.run(CircuitBreakerApp.class, args);
    }
}
