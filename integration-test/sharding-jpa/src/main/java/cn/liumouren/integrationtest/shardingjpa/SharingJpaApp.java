package cn.liumouren.integrationtest.shardingjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;

/**
 *
 * @author freeman
 * @date 2021/12/7 2:17 PM
 */
@SpringBootApplication
public class SharingJpaApp {
    public static void main(String[] args) {
        SpringApplication.run(SharingJpaApp.class, args);
    }
}
