package cn.liumouren.boot.db.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 * @author freeman
 * @date 2021/12/6 16:18
 */
@SpringBootTest(classes = MysqlDynamicPropertiesTest.Config.class)
public class MysqlDynamicPropertiesTest {

    @Autowired
    private MysqlDynamicProperties properties;
    @Autowired
    private DefaultListableBeanFactory beanFactory;
    @Autowired
    private ObjectProvider<JdbcTemplate> jdbcTemplate;
    @Autowired
    private ObjectProvider<TransactionManager> transactionManager;

    @Test
    public void test_dynamicMysqlProperties() {
        System.out.println(transactionManager.getIfAvailable());
        assertNotNull(transactionManager.getIfAvailable());
        assertNotNull(jdbcTemplate.getIfAvailable());
        assertNotNull(beanFactory.getBean("dataSource"));
        assertNotNull(beanFactory.getBean("ds-user", HikariDataSource.class));
        assertEquals(2, beanFactory.getBean("ds-user", HikariDataSource.class).getMaximumPoolSize());
        assertEquals(DruidDataSourceWrapper.class, beanFactory.getBean("ds-order").getClass());
        assertEquals(2, beanFactory.getBean("ds-order", DruidDataSource.class).getInitialSize());
        assertEquals(3, beanFactory.getBeanNamesForType(DataSource.class).length);
        assertNotNull(properties);
        assertEquals("root", properties.getDatasources().get(0).getUsername());
        assertEquals("llw1123..", properties.getDatasources().get(0).getPassword());
        assertEquals("root", properties.getDatasources().get(1).getUsername());
        assertEquals("llw1123..", properties.getDatasources().get(1).getPassword());
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories
    static class Config {

    }
}
