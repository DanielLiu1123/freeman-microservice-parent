package cn.liumouren.integrationtest.circuitbreaker;

import cn.hutool.core.thread.ThreadUtil;
import cn.liumouren.integrationtest.circuitbreaker.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 23:49
 */
@Component
public class Runner implements ApplicationRunner {

    @Autowired
    @Qualifier("cn.liumouren.integrationtest.circuitbreaker.api.UserApi")
    private UserApi api;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execAsync(() -> {
            ThreadUtil.sleep(1000);

            System.out.println("========== start ==========");

            System.out.println(api.ok(true));
            // configuration count is 1, but we need 2 exceptions to open the circuit breaker
            System.out.println(api.ok(false));
            System.out.println(api.ok(false));

            System.out.println("========== circuit breaker opened ==========");

            System.out.println(api.ok(true));
            System.out.println(api.ok(true));
            System.out.println(api.ok(true));
            System.out.println(api.ok(true));

            System.out.println("========== sleep 1100ms, circuit breaker half open ==========");

            ThreadUtil.sleep(1100);
            System.out.println(api.ok(true));
            System.out.println(api.ok(true));
            System.out.println(api.ok(true));
            System.out.println(api.ok(true));

            System.out.println("========== end ==========");
        });
    }

}
