package cn.liumouren.boot.redis.redistemplate;

import cn.liumouren.boot.redis.RedisTemplateUtil;
import cn.liumouren.boot.redis.RedisUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 01:39
 */
@SpringBootTest(
        classes = RedisTemplateTest.Config.class,
        properties = {
                "logging.level.org.springframework.data.redis=debug"
        }
)
public class RedisTemplateTest {

    @Autowired
    private RedisUtil<User> redisUtil;
    @Autowired
    private RedisUtil<Admin> adminRedisUtil;

    @Test
    public void test_json() {
        User user = new User()
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setSex(Sex.MALE)
                .setName("freeman");

        redisUtil.set("freeman", user);

        User freeman = redisUtil.get("freeman");
        assertNull(freeman.getBobby());
        assertEquals(21, freeman.getAge());
        assertEquals(Sex.MALE, freeman.getSex());
    }

    @Test
    public void test_redisUtil() {
        User user = new User()
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setSex(Sex.MALE)
                .setName("freeman");
        // 模拟 user 表中插入 id 为 1 的数据
        redisUtil.put("user", "1", user);
        User u = redisUtil.hget("user", "1");
        assertEquals(user.birthday, u.getBirthday());

        adminRedisUtil.set(
                "hhh",
                new Admin()
                        .setAge(21)
                        .setBirthday(LocalDateTime.now())
                        .setCreateTime(new Date()).setBobby("girl"));
    }


    @Configuration
    @EnableAutoConfiguration
    static class Config {

        @Bean
        public RedisConnectionFactory connectionFactory() {
            RedisStandaloneConfiguration configuration =
                    new RedisStandaloneConfiguration("106.54.76.182", 6379);
            configuration.setDatabase(10);
            configuration.setPassword(RedisPassword.of("llw1123"));

            return new LettuceConnectionFactory(configuration);
        }

//        @Bean
        public RedisTemplate<String, User> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return RedisTemplateUtil.build(redisConnectionFactory, User.class, mapper);
        }

    }


    @Data
    @Accessors(chain = true)
    static class User {
        private String name;
        private LocalDateTime birthday;
        private Date createTime;
        private Integer age;
        private String bobby;
        private Sex sex;
    }

    @Data
    @Accessors(chain = true)
    static class Admin {
        private String name;
        private LocalDateTime birthday;
        private Date createTime;
        private Integer age;
        private String bobby;
        private Sex sex;
    }

    enum Sex {
        MALE, FEMALE
    }
}
