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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 01:39
 */
@SpringBootTest(
        classes = RedisUtilTest.Config.class,
        properties = {
                "logging.level.org.springframework.data.redis=debug"
        }
)
public class RedisUtilTest {

    @Test
    public void test_checkRedisUtil() {
        assertNotNull(RedisUtil.getDefaultTemplate());
        assertNotNull(RedisUtil.getEntityTempalteMappings());
    }

    @Test
    public void test_value() {
        RedisUtil.delete("value");

        // set
        User user = new User()
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setSex(Sex.MALE)
                .setBobbies(new ArrayList<>(List.of("gril", "programming")))
                .setMeta(new HashMap<>(Map.of("word", "hello")))
                .setName("freeman");
        RedisUtil.set("value", user);

        // get
        User u = RedisUtil.get("value", User.class);
        String s = RedisUtil.get("value", String.class);
        System.out.println(u);
        System.out.println(s);
        assertEquals(Sex.MALE, u.getSex());
    }


    @Test
    public void test_hash() {
        RedisUtil.delete("hash");

        // put
        User user = new User()
                .setAge(21)
                .setBirthday(LocalDateTime.now())
                .setCreateTime(new Date())
                .setSex(Sex.MALE)
                .setBobbies(new ArrayList<>(List.of("gril", "programming")))
                .setMeta(new HashMap<>(Map.of("word", "hello")))
                .setName("freeman");
        RedisUtil.put("hash", "1", user);

        // get
        User u = RedisUtil.hget("hash", "1", User.class);
        System.out.println(u);
        assertEquals(2, u.getBobbies().size());
    }

    @Test
    public void test_list() {
        RedisUtil.delete("list");

        // push
        List<User> users = new ArrayList<>();
        users.add(new User().setAge(20).setName("wlq").setBirthday(LocalDateTime.now()));
        users.add(new User().setAge(19).setName("llw").setBirthday(LocalDateTime.now()));
        users.add(new User().setAge(20).setName("zkl").setBirthday(LocalDateTime.now()));
        RedisUtil.leftPushAll("list", users);

        // query
        System.out.println("====== User type ======");
        RedisUtil.range("list", 0, -1, User.class).forEach(System.out::println);
        System.out.println("====== String type ======");
        RedisUtil.range("list", 0, -1, String.class).forEach(System.out::println);

        // size
        Long size = RedisUtil.sizeForList("list");
        System.out.println("size: " + size);
        assertEquals(3, size);
    }

    @Test
    public void test_set() {
        RedisUtil.delete("set", "set2");

        List<User> users = new ArrayList<>();
        User user1 = new User().setAge(20).setName("wlq").setBirthday(LocalDateTime.now());
        User user3 = new User().setAge(19).setName("wlq").setBirthday(LocalDateTime.now());
        User user2 = new User().setAge(18).setName("wlq").setBirthday(LocalDateTime.now());
        users.add(user1);
        users.add(user2);
        users.add(user3);

        // add
        users.forEach(user -> RedisUtil.add("set", user));

        // size
        assertEquals(3, RedisUtil.sizeForSet("set"));

        // query
        System.out.println("=====query====");
        Set<User> userSet = RedisUtil.members("set", User.class);
        Set<String> stringSet = RedisUtil.members("set", String.class);
        userSet.forEach(System.out::println);
        stringSet.forEach(System.out::println);
        assertEquals(3, userSet.size());
        assertEquals(3, stringSet.size());

        RedisUtil.add("set2", user3);

        // difference
        System.out.println("=====difference====");
        Set<User> difference = RedisUtil.difference(List.of("set", "set2"), User.class);
        difference.forEach(System.out::println);
        assertEquals(2, difference.size());

        // difference
        System.out.println("=====intersect====");
        Set<User> intersect = RedisUtil.intersect(List.of("set", "set2"), User.class);
        intersect.forEach(System.out::println);
        assertEquals(1, intersect.size());

        // remove
        assertEquals(1, RedisUtil.remove("set", user1));
    }

    @Configuration
    @EnableAutoConfiguration
    static class Config {

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
