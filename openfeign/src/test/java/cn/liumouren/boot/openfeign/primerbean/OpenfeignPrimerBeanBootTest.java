package cn.liumouren.boot.openfeign.primerbean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:36
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = OpenfeignPrimerBeanBootTest.WebBootConfiguration.class,
        properties = {
                "server.port=10001",
                "spring.application.name=openfeign-boot-test",
                // FeignClient value 相同会出现重名 bean, 开启不会影响 feignClient 的使用
                "spring.main.allow-bean-definition-overriding=true"
        }
)
public class OpenfeignPrimerBeanBootTest {

    @Autowired
    // is $, NOT .
    @Qualifier("cn.liumouren.boot.openfeign.primerbean.OpenfeignPrimerBeanBootTest$PrimerBeanApi")
    private PrimerBeanApi primerBeanApi;
    @Autowired
    private PrimerBeanApi PrimerBeanController;

    @Test
    public void test_primerBean() {
        // 判断我们是否给 controller primer 设置为 false
        assertTrue(PrimerBeanApi.class.isAssignableFrom(primerBeanApi.getClass()));
        assertEquals(WebBootConfiguration.PrimerBeanController.class, PrimerBeanController.getClass());

        String header = PrimerBeanController.testPrimerBean();

        assertEquals("openfeign-boot-test", header);
    }


    @FeignClient(value = "primer-bean", url = "http://localhost:10001")
    interface PrimerBeanApi {
        @GetMapping("/testPrimerBean")
        String testPrimerBean();
    }

    @FeignClient(value = "primer-bean", url = "http://localhost:10001")
    interface PrimerBean2Api {
        @GetMapping("/testPrimerBean2")
        String testPrimerBean();
    }

    @FeignClient(value = "three", url = "http://localhost:10001")
    interface ThreeApi {
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan
    @EnableFeignClients
    static class WebBootConfiguration {

        @RestController
        static class PrimerBeanController implements PrimerBeanApi {
            @Autowired
            private Environment environment;

            @GetMapping("/testPrimerBean")
            @Override
            public String testPrimerBean() {
                return environment.getProperty("spring.application.name");
            }
        }

        @RestController
        static class PrimerBean2Controller implements PrimerBean2Api, ThreeApi {
            @Autowired
            private Environment environment;

            @GetMapping("/testPrimerBean2")
            @Override
            public String testPrimerBean() {
                return environment.getProperty("spring.application.name");
            }
        }

        @RestController
        static class CommonController {

        }

    }

}

