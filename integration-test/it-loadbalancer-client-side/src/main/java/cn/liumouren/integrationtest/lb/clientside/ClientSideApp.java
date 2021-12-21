package cn.liumouren.integrationtest.lb.clientside;

import cn.hutool.core.thread.ThreadUtil;
import cn.liumouren.integrationtest.lb.clientside.api.ClientSideApi;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author freeman
 * @date 2021/12/20 14:25
 */
@SpringBootApplication
@EnableFeignClients("cn.liumouren.**.api")
@RestController
public class ClientSideApp implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(ClientSideApp.class, args);
    }

    @GetMapping("/get")
    public String get(@Value("${server.port}") int port) {
        return "get -> " + port;
    }

    @GetMapping("/lb")
    public String get() {
        return api.get();
    }

    @Autowired
    private ClientSideApi api;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 等待服务注册完成
        ThreadUtil.sleep(2_000);

        ThreadUtil.execAsync(() -> System.out.println(api.get()));
    }

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }
}
