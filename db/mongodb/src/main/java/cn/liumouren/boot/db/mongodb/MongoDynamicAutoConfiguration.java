package cn.liumouren.boot.db.mongodb;

import cn.liumouren.boot.db.mongodb.core.MongoTemplatePostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.liumouren.boot.db.mongodb.MongoDynamicProperties.PREFIX;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 16:23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({MongoDynamicProperties.class})
@AutoConfigureAfter(MongoDataAutoConfiguration.class) // 我们在 mongoTemplate 装载完之后直接覆盖
@AutoConfigureBefore(MongoRepositoriesAutoConfiguration.class) // 在 jpa 之前
public class MongoDynamicAutoConfiguration {

    @Bean
    static BeanFactoryPostProcessor mongoTemplatePostProcessor() {
        return new MongoTemplatePostProcessor();
    }

}
