package cn.liumouren.boot.db.mysql;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ReflectUtil;
import cn.liumouren.boot.db.mysql.util.IdUtil;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static cn.liumouren.boot.db.mysql.MysqlDynamicProperties.*;


/**
 *
 *
 * @author freeman
 * @date 2021/12/6 16:30
 */
@Configuration
@EnableConfigurationProperties(MysqlDynamicProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@Import(IdUtil.class)
public class MysqlDynamicAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public Snowflake snowflake() {
        return new Snowflake();
    }

    public MysqlDynamicAutoConfiguration(DefaultListableBeanFactory beanFactory, MysqlDynamicProperties properties) {
//        init(beanFactory, properties);
    }

    private void init(DefaultListableBeanFactory beanFactory, MysqlDynamicProperties properties) {
        List<DsConfig> configs = properties.getDatasources();
        if (configs == null) {
            return;
        }

        for (DsConfig config : configs) {
            if (config.getType() == null) {
                config.setType(properties.getDefaultType());
            }
            // 注册 data source bean definition
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class, () -> {
                return createDataSource(config);
            });
            beanFactory.registerBeanDefinition(config.getName(), builder.getBeanDefinition());

            // 注册完后直接创建 data source
            DataSource dataSource = beanFactory.getBean(config.getName(), DataSource.class);
            registerEntityManagerFactoryBean(beanFactory, "em" + config.getName(), dataSource);

            DataSourceHolder.add(config.getName(), dataSource);
        }
    }

    private DataSource createDataSource(DsConfig cfg) {
        if (HikariConfig.class.isAssignableFrom(cfg.getType())) {
            HikariConfig config = new HikariConfig();
            config.setPoolName(cfg.getName());
            config.setJdbcUrl(cfg.getUrl());
            config.setDriverClassName(cfg.getJdbcClassName());
            config.setUsername(cfg.getUsername());
            config.setPassword(cfg.getPassword());

            Map<String, Object> extra = cfg.getExtra();
            if (extra != null) {
                extra.forEach((key, val) -> ReflectUtil.setFieldValue(config, key, val));
            }
            return new HikariDataSource(config);
        }
        if (DruidAbstractDataSource.class.isAssignableFrom(cfg.getType())) {
            DruidDataSourceWrapper druidDataSource = new DruidDataSourceWrapper();
            druidDataSource.setName(cfg.getName());
            druidDataSource.setDriverClassName(cfg.getJdbcClassName());
            druidDataSource.setUrl(cfg.getUrl());
            druidDataSource.setUsername(cfg.getUsername());
            druidDataSource.setPassword(cfg.getPassword());

            Map<String, Object> extra = cfg.getExtra();
            if (extra != null) {
                extra.forEach((key, val) -> ReflectUtil.setFieldValue(druidDataSource, key, val));
            }
            return druidDataSource;
        }
        // 只支持 hikari 和 druid
        throw new IllegalArgumentException("不支持的数据源类型");
    }

    private void registerEntityManagerFactoryBean(DefaultListableBeanFactory beanFactory, String beanName, DataSource dataSource) {
        // 注册 LocalContainerEntityManagerFactoryBean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class, () -> {
            HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
            jpaVendorAdapter.setGenerateDdl(false);

            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

            factoryBean.setDataSource(dataSource);
            factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
            return factoryBean;
        });
        beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

}
