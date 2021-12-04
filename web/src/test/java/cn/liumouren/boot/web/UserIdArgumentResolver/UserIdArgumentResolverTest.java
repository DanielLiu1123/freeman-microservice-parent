package cn.liumouren.boot.web.UserIdArgumentResolver;

import cn.hutool.core.util.StrUtil;
import cn.liumouren.boot.common.anno.UserId;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 * @author freeman
 * @date 2021/12/4 5:02 PM
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = UserIdArgumentResolverTest.UserIdConfig.class,
        properties = {
                "server.port=9097",
                "spring.application.name=userid-argument-resolver-test"
        }
)
public class UserIdArgumentResolverTest {

    OkHttpClient client = new OkHttpClient();

    @Test
    public void test_userId_ok() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:9097")
                .addHeader(UserId.HEADER, "1")
                .build();
        Response response = client.newCall(request).execute();
        String res = new String(response.body().bytes());

        assertEquals("1", res);
    }

    @Test
    public void test_userId_whenNoHeader() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:9097")
                .build();
        Response response = client.newCall(request).execute();

        assertNotEquals(200, response.code());
    }

    @Test
    public void test_userId_whenNotRequired() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:9097/userIdNotRequired")
                .build();
        Response response = client.newCall(request).execute();

        assertEquals(200, response.code());
        assertTrue(StrUtil.isEmpty(response.body().string()));
    }

    @Configuration
    @EnableAutoConfiguration
    static class UserIdConfig {

        @RestController
        static class Controller {

            @GetMapping
            public String userId(@UserId String userId) {
                return userId;
            }

            @GetMapping("/userIdNotRequired")
            public String userIdNotRequired(@UserId(required = false) String userId) {
                return userId;
            }
        }


    }
}
