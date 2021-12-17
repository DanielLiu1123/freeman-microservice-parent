package cn.liumouren.boot.web.WebBoot;

import cn.hutool.core.thread.ThreadUtil;
import cn.liumouren.boot.common.exception.BizException;
import cn.liumouren.boot.common.exception.WebException;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:36
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = WebBootTest.WebBootConfiguration.class,
        properties = {
                "server.port=8081",
                "spring.application.name=web-boot-test"
        }
)
public class WebBootTest {

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void testFilter() {
        restTemplate.getForObject("http://localhost:8081", String.class);
    }

    @Test
    public void testConsumeTimeIfCorrect() {
        // 第一次请求
        try {
            String res = restTemplate.getForObject("http://localhost:8081/false", String.class);
            System.out.println(res);
        } catch (RestClientException ignore) {
        }

        ThreadUtil.sleep(300);

        // 第二次请求
        restTemplate.getForObject("http://localhost:8081/true", String.class);
    }

    @Test
    public void testExceptionHandler() {
        assertThrows(HttpServerErrorException.InternalServerError.class,
                () -> restTemplate.getForObject("http://localhost:8081/biz_exception", String.class));
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
                throw new WebException("抛出异常");
            }

            @GetMapping("/biz_exception")
            public String bizException() throws BizException {
                throw new BizException()
                        .setCode(23212);
            }
        }

    }

    @Test
    public void testTomcatThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    restTemplate.getForObject("http://localhost:8081", String.class);
                } catch (RestClientException e) {
                    System.out.println(e.getCause().toString());
                }
                latch.countDown();
            }, String.valueOf(i)).start();
        }
        latch.await();
    }

}

