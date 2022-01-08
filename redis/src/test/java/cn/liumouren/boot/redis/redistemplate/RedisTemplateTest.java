package cn.liumouren.boot.redis.redistemplate;

import cn.liumouren.boot.redis.RedisCacheCandidates;
import cn.liumouren.boot.redis.RedisUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void test_checkRedisUtil() {
        System.out.println(RedisUtil.getDefaultTemplate());
        System.out.println(RedisUtil.getMappings());
    }

    @Test
    public void test_value() {
        User user = new User()
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setSex(Sex.MALE)
                .setBobbies(new ArrayList<>(List.of("gril", "programming")))
                .setMeta(new HashMap<>(Map.of("word", "hello")))
                .setName("freeman");

        RedisUtil.set("freeman", user);

        User freeman = RedisUtil.get("freeman", User.class);
        System.out.println(freeman);
        assertEquals(2, freeman.getBobbies().size());
        assertEquals(1, freeman.getMeta().size());
        assertEquals(21, freeman.getAge());
        assertEquals(Sex.MALE, freeman.getSex());
    }


    @Test
    public void test_hash() {
        User user = new User()
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setSex(Sex.MALE)
                .setBobbies(new ArrayList<>(List.of("gril", "programming")))
                .setMeta(new HashMap<>(Map.of("word", "hello")))
                .setName("freeman");
        RedisUtil.put("user", "1", user);
        User u = RedisUtil.hget("user", "1", User.class);
        System.out.println(u);
        assertEquals(2, u.getBobbies().size());
    }

    @Test
    public void test_adminRedis() {
        Admin adm = new Admin()
                .setName("daniel")
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setBobby("girl");
        RedisUtil.set("admin", adm);
        Admin admin = RedisUtil.get("admin", Admin.class);
        System.out.println(admin);
        assertEquals(21, admin.getAge());
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

        @Bean
        public RedisCacheCandidates redisCandidate() {
            return () -> Set.of(User.class, Admin.class);
        }

    }


    @Data
    @Accessors(chain = true)
    static class User {
        private String name;
        private LocalDateTime birthday;
        private Date createTime;
        private Integer age;
        private List<String> bobbies;
        private Sex sex;
        private Map<String, String> meta;
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
