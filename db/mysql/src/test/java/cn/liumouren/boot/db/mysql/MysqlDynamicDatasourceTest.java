package cn.liumouren.boot.db.mysql;


import cn.liumouren.boot.common.constant.ContainerVersion;
import cn.liumouren.boot.db.mysql.dao.ProductDao;
import cn.liumouren.boot.db.mysql.dao.UserDao;
import cn.liumouren.boot.db.mysql.model.Product;
import cn.liumouren.boot.db.mysql.model.User;
import cn.liumouren.boot.db.mysql.service.BizService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *
 *
 * @author freeman
 * @date 2022/1/11 01:28
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
public class MysqlDynamicDatasourceTest {

    @Container
    static JdbcDatabaseContainer mongo = new MySQLContainer(ContainerVersion.MYSQL)
            .withUsername("root").withPassword("root").withInitScript("sql/init.sql");

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.shardingsphere.datasource.ds-product.jdbc-url",
                () -> String.format("jdbc:mysql://localhost:%s/freeman_product?serverTimezone=Asia/Shanghai&characterEncoding=utf8", mongo.getFirstMappedPort()));
        registry.add("spring.shardingsphere.datasource.ds-user.jdbc-url",
                () -> String.format("jdbc:mysql://localhost:%s/freeman_user?serverTimezone=Asia/Shanghai&characterEncoding=utf8", mongo.getFirstMappedPort()));
    }

    @Autowired
    private UserDao userDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BizService bizService;

    @Test
    @Order(0)
    public void test_ready() {
        List<Product> products = productDao.findAll();
        List<User> users = userDao.findAll();

        assertThat(products.size()).isEqualTo(2);
        assertThat(users.size()).isEqualTo(4);
    }

    @Test
    @Order(1)
    public void test_insertToDifferentTable() {
        userDao.saveAll(Arrays.asList(new User().setId("5"), new User().setId("6")));

        assertThat(userDao.findAll().size()).isEqualTo(6);
    }

    @Test
    public void test_transactional() {
        assertThatThrownBy(() -> bizService.deleteErrFallback("1"));

        // rollback
        List<Product> products = productDao.findAll();
        List<User> users = userDao.findAll();

        assertThat(products.size()).isEqualTo(2);
        assertThat(users.size()).isEqualTo(6);

        assertThatThrownBy(() -> bizService.deleteErrNoFallback("1"));

        // not rollback
        List<Product> ps = productDao.findAll();
        List<User> us = userDao.findAll();

        assertThat(ps.size()).isEqualTo(1);
        assertThat(us.size()).isEqualTo(5);
    }


    @Configuration
    @EnableAutoConfiguration
    @ComponentScan
    @EnableJpaRepositories
    static class Config {

    }

}
