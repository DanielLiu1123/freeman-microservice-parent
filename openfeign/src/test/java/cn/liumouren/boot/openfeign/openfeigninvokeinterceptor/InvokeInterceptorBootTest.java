package cn.liumouren.boot.openfeign.openfeigninvokeinterceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.liumouren.boot.common.exception.BizException;
import cn.liumouren.boot.openfeign.Admin;
import cn.liumouren.boot.common.anno.UserId;
import feign.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/23 10:50
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = InvokeInterceptorBootTest.OpenfeignInvokeInterceptorConfiguration.class,
        properties = {
                "server.port=8282",
                "spring.application.name=invoke-interceptor-boot-test",
                "logging.level.cn.liumouren.boot=debug"
        }
)
public class InvokeInterceptorBootTest {
    @Autowired
    // is $, NOT .
    @Qualifier("cn.liumouren.boot.openfeign.openfeigninvokeinterceptor.InvokeInterceptorBootTest$InvokeInterceptorApi")
    private InvokeInterceptorApi invokeInterceptorApi;

    @Autowired
    @Qualifier("cn.liumouren.boot.openfeign.openfeigninvokeinterceptor.InvokeInterceptorBootTest$RequestMappingApi")
    private RequestMappingApi requestMappingApi;

    @Test
    public void test_fromAppHeader() {
        assertThrows(BizException.class, () -> invokeInterceptorApi.throwExpectedException());
        assertThrows(FeignException.class, () -> invokeInterceptorApi.invokeErr());
        assertThrows(FeignException.class, () -> invokeInterceptorApi.throwUnexpectedException());
    }

    @Test
    public void test_pathVariable() {
        assertEquals("freeman", invokeInterceptorApi.testPathVariable("freeman"));
    }

    @Test
    public void test_userId() {
        assertEquals("ok", invokeInterceptorApi.testUserId("userId"));
        assertEquals("freeman", invokeInterceptorApi.testUserIdAndOthers("freeman", "id"));
    }

    @Test
    public void test_requestLine() {
        Map<String, String> map = new HashMap<>();
        map.put("freeman-02", "0202");
        map.put("freeman-03", "0303");
        map.put("freeman-04", "0404");
        assertEquals("freeman:4 headers", invokeInterceptorApi.testRequestLine(map, "freeman", "001"));
    }

    @Test
    public void test_requestMapping() {
        assertEquals("freeman", requestMappingApi.testRequestMapping("freeman"));
    }

    @Test
    public void test_requestBody() {
        Admin admin = new Admin()
                .setAge(21)
                .setName("daniel")
                .setDeleted(false)
                .setRoles(Arrays.asList("admin", "user"));
        assertEquals(4, requestMappingApi.testRequestBody(admin));
    }


    @FeignClient(value = "invoke-interceptor", url = "http://localhost:8282")
    interface InvokeInterceptorApi {
        @GetMapping
        String throwExpectedException() throws BizException;

        @GetMapping("/this/is/a/404/api")
        String invokeErr();

        @GetMapping("/throwUnexpectedException")
        String throwUnexpectedException() throws RuntimeException;

        @GetMapping("/{id}")
        String testPathVariable(@PathVariable("id") String id);

        @GetMapping("/testUserId")
        String testUserId(@UserId String id);

        @GetMapping("/testUserIdAndOthers/{name}")
        @Headers({
                "some-header: hhhh",
                "some-header-1: hhhhhhh",
        })
        String testUserIdAndOthers(@Param("name") String name, @UserId String id);

        @RequestLine("GET /testRequestLine/{name}")
        @Headers({
                "freeman-01: 010101"
        })
        String testRequestLine(@RequestHeader Map<String, String> header, @PathVariable("name") String name, @UserId String id);
    }

    @FeignClient(value = "invoke-interceptor", path = "/requestMapping", url = "http://localhost:8282")
    interface RequestMappingApi {
        @RequestLine("GET /{name}")
        String testRequestMapping(@PathVariable("name") String name);

        @PostMapping
        int testRequestBody(Admin admin);
    }

    @FeignClient(value = "some", url = "http://localhost:8282")
    interface SomeApi {

        @RequestLine("GET /some/{name}")
        String some(@PathVariable("name") String name);
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients
    static class OpenfeignInvokeInterceptorConfiguration {

        @Bean
        Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @RestController
        static class InvokeInterceptorController {

            @GetMapping
            public String throwExpectedException() {
                throw new BizException(2213, "msg", "illegal argument !", "test-app");
            }

            public String invokeErr() {
                return "404";
            }

            @GetMapping("/throwUnexpectedException")
            String throwUnexpectedException() {
                throw new RuntimeException(new RuntimeException("first level"));
            }

            @GetMapping("/{id}")
            String testPathVariable(@PathVariable String id) {
                return id;
            }

            @GetMapping(value = "/testUserId", headers = {"X-Uid-Token"})
            String testUserId() {
                return "ok";
            }

            @GetMapping(value = "/testUserIdAndOthers/{name}", headers = {"X-Uid-Token", "some-header"})
            String testUserIdAndOthers(@PathVariable String name) {
                return name;
            }

            @GetMapping("/testRequestLine/{name}")
            String testRequestLine(@RequestHeader Map<String, String> headers, @PathVariable String name) {
                long size = headers.keySet().stream()
                        .filter(key -> key.startsWith("freeman"))
                        .count();
                return String.format("%s:%d headers", name, size);
            }

            @GetMapping("/requestMapping/{name}")
            String testRequestLine(@PathVariable String name) {
                return name;
            }

            @PostMapping("/requestMapping")
            int testRequestBody(@RequestBody Admin admin) {
                System.out.println(admin);
                Map<String, Object> map = BeanUtil.beanToMap(admin);
                return map.size();
            }

        }

    }
}
