package cn.liumouren.boot.web;

import cn.hutool.core.thread.ThreadUtil;
import cn.liumouren.boot.common.FreemanWebException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:36
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = WebBootTest.WebBootConfiguration.class,
        properties = {
                "server.port=80",
                "spring.application.name=web-boot-test"
        }
)
public class WebBootTest {

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void testFilter() throws InterruptedException {
        String appName = restTemplate.getForObject("http://localhost", String.class);
        assertEquals("web-boot-test", appName);
    }

    @Test
    public void testConsumeTimeIfCorrect() {
        // 第一次请求
        try {
            String res = restTemplate.getForObject("http://localhost/false", String.class);
            System.out.println(res);
        } catch (RestClientException ignore) {
        }

        ThreadUtil.sleep(300);

        // 第二次请求
        restTemplate.getForObject("http://localhost/true", String.class);
    }


    @Configuration
    @EnableAutoConfiguration
    static class WebBootConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @RestController
        static class WebBootController {
            @Autowired
            private Environment environment;

            @GetMapping
            public String getAppName() {
                return environment.getProperty("spring.application.name");
            }

            @GetMapping("/{success}")
            public String success(@PathVariable boolean success) {
                if (success) {
                    return environment.getProperty("spring.application.name");
                }
                throw new FreemanWebException("抛出异常");
            }
        }

    }

    @Test
    public void testTomcatThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    restTemplate.getForObject("http://localhost", String.class);
                } catch (RestClientException e) {
                    System.out.println(e.getCause().toString());
                }
                latch.countDown();
            }, String.valueOf(i)).start();
        }
        latch.await();
    }

}

