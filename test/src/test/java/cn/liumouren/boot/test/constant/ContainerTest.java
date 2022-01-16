package cn.liumouren.boot.test.constant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static cn.liumouren.boot.common.constant.ContainerVersion.MONGO;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 *
 * @author freeman
 * @date 2022/1/12 00:54
 */
@SpringBootTest
@SpringBootApplication
@Testcontainers
public class ContainerTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer(MONGO);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", () -> mongo.getFirstMappedPort());
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void mongodb_is_ready() {
        assertThat(mongoTemplate).isNotNull();
    }

}
