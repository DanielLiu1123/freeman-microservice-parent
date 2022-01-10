package cn.liumouren.boot.circuitbreaker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 22:25
 */
@SpringBootTest(webEnvironment = DEFINED_PORT, classes = FeignIntegrationTest.App.class, properties = {
        "server.port=10101", "feign.circuitbreaker.enabled=true",
        "feign.sentinel.default-rule=default",
        "feign.sentinel.rules.default[0].grade=2",
        "feign.sentinel.rules.default[0].count=2",
        "feign.sentinel.rules.default[0].timeWindow=1",
        "feign.sentinel.rules.default[0].statIntervalMs=1000",
        "feign.sentinel.rules.default[0].minRequestAmount=5",
        "feign.sentinel.rules.user[0].grade=2",
        "feign.sentinel.rules.user[0].count=2",
        "feign.sentinel.rules.user[0].timeWindow=1",
        "feign.sentinel.rules.user[0].statIntervalMs=1000",
        "feign.sentinel.rules.user[0].minRequestAmount=5",
        "feign.sentinel.rules.[user#specificFeignMethod(boolean)][0].grade=2",
        "feign.sentinel.rules.[user#specificFeignMethod(boolean)][0].count=1",
        "feign.sentinel.rules.[user#specificFeignMethod(boolean)][0].timeWindow=1",
        "feign.sentinel.rules.[user#specificFeignMethod(boolean)][0].statIntervalMs=1000",
        "feign.sentinel.rules.[user#specificFeignMethod(boolean)][0].minRequestAmount=5"})
public class FeignIntegrationTest {

    @Autowired
    private App.UserClient userClient;
    @Autowired
    private App.OrderClient orderClient;

    @Test
    public void testConfigDefaultRule() throws Exception {
        // test default configuration is working

        // ok
        assertThat(orderClient.defaultConfig(true)).isEqualTo("ok");
        assertThat(orderClient.defaultConfig(true)).isEqualTo("ok");

        // occur exception, circuit breaker open
        assertThat(orderClient.defaultConfig(false)).isEqualTo("fallback");
        assertThat(orderClient.defaultConfig(false)).isEqualTo("fallback");
        assertThat(orderClient.defaultConfig(false)).isEqualTo("fallback");

        // test circuit breaker open
        assertThat(orderClient.defaultConfig(true)).isEqualTo("fallback");
        assertThat(orderClient.defaultConfig(true)).isEqualTo("fallback");

        // longer than timeWindow, circuit breaker half open
        Thread.sleep(1100L);

        // test circuit breaker close
        assertThat(orderClient.defaultConfig(true)).isEqualTo("ok");
    }

    @Test
    public void testConfigSpecificFeignRule() throws Exception {
        // test specific Feign client configuration is working

        // ok
        assertThat(userClient.specificFeign(true)).isEqualTo("ok");
        assertThat(userClient.specificFeign(true)).isEqualTo("ok");

        // occur exception, circuit breaker open
        assertThat(userClient.specificFeign(false)).isEqualTo("fallback");
        assertThat(userClient.specificFeign(false)).isEqualTo("fallback");
        assertThat(userClient.specificFeign(false)).isEqualTo("fallback");

        // test circuit breaker open
        assertThat(userClient.specificFeign(true)).isEqualTo("fallback");
        assertThat(userClient.specificFeign(true)).isEqualTo("fallback");

        // longer than timeWindow, circuit breaker half open
        Thread.sleep(1100L);

        // test circuit breaker close
        assertThat(userClient.specificFeign(true)).isEqualTo("ok");
    }

    @Test
    public void testConfigSpecificFeignMethodRule() throws Exception {
        // test specific Feign client method configuration is working

        // ok
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");

        // occur exceptions
        assertThat(userClient.specificFeignMethod(false)).isEqualTo("fallback");

        // 1 time exceptions, circuit breaker is closed(configuration is 1, but we need 2 to make it closed)
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");

        // occur the 2nd exception, circuit breaker close
        assertThat(userClient.specificFeignMethod(false)).isEqualTo("fallback");

        // test circuit breaker is closed
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("fallback");
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("fallback");

        // longer than timeWindow, circuit breaker half open
        Thread.sleep(1100L);

        // let circuit breaker open
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");
        assertThat(userClient.specificFeignMethod(true)).isEqualTo("ok");
    }


    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableFeignClients("cn.liumouren.boot.circuitbreaker")
    static class App {

        @FeignClient(value = "user", url = "http://localhost:${server.port}", fallback = UserClientFallback.class)
        interface UserClient {

            @GetMapping("/specificFeign/{success}")
            String specificFeign(@PathVariable boolean success);

            @GetMapping("/specificFeignMethod/{success}")
            String specificFeignMethod(@PathVariable boolean success);

        }

        @FeignClient(value = "order", url = "http://localhost:${server.port}", fallback = OrderClientFallback.class)
        interface OrderClient {

            @GetMapping("/defaultConfig/{success}")
            String defaultConfig(@PathVariable boolean success);

        }

        @Component
        static class UserClientFallback implements UserClient {

            @Override
            public String specificFeign(boolean success) {
                return "fallback";
            }

            @Override
            public String specificFeignMethod(boolean success) {
                return "fallback";
            }

        }

        @Component
        static class OrderClientFallback implements OrderClient {

            @Override
            public String defaultConfig(boolean success) {
                return "fallback";
            }

        }

        @RestController
        static class Controller {

            @GetMapping("/specificFeign/{success}")
            public String specificFeign(@PathVariable boolean success) {
                if (success) {
                    return "ok";
                }
                throw new RuntimeException("failed");
            }

            @GetMapping("/defaultConfig/{success}")
            String defaultConfig(@PathVariable boolean success) {
                if (success) {
                    return "ok";
                }
                throw new RuntimeException("failed");
            }

            @GetMapping("/specificFeignMethod/{success}")
            String specificFeignMethod(@PathVariable boolean success) {
                if (success) {
                    return "ok";
                }
                throw new RuntimeException("failed");
            }
        }

    }

}
