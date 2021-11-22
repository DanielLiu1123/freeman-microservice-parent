package cn.liumouren.boot.openfeign.fromapp;

import cn.liumouren.boot.common.WebConst;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:36
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = OpenfeignFromAppRequestInterceptorBootTest.WebBootConfiguration.class,
        properties = {
                "server.port=81",
                "spring.application.name=openfeign-boot-test"
        }
)
public class OpenfeignFromAppRequestInterceptorBootTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FromAppApi fromAppApi;

    @Test
    public void testOpenfeign_fromAppHeader() {
        String header = fromAppApi.testFromAppHeader();
        String nullHeader = restTemplate.getForObject("http://localhost:81/testPrimerBean", String.class);
        assertEquals("openfeign-boot-test", header);
        assertNull(nullHeader);
    }


    @FeignClient(value = "from-app", url = "http://localhost:81")
    interface FromAppApi {
        @GetMapping("/testPrimerBean")
        String testFromAppHeader();
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients
    static class WebBootConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @RestController
        static class FromAppController {
            @Autowired
            private Environment environment;

            @GetMapping
            public String getAppName() {
                return environment.getProperty("spring.application.name");
            }

            @GetMapping("/testPrimerBean")
            public String testFromAppHeader(HttpServletRequest request) {
                return request.getHeader(WebConst.Header.FROM_APP);
            }
        }

    }

}

