package cn.liumouren.boot.db.mongodb.core;

import cn.hutool.core.collection.CollUtil;
import cn.liumouren.boot.db.mongodb.MongoDynamicProperties;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 *
 * @author freeman
 * @date 2021/12/31 12:34
 */
public class MongoTemplatePostProcessor implements BeanFactoryPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(MongoTemplatePostProcessor.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            // 注意这里 bean 名字 mongoTemplate 覆盖了 spring 默认装配
            // 主要目的是为了让 jpa 也走这个 MongoOperations
            // see EnableMongoRepositories#mongoTemplateRef()
            ((BeanDefinitionRegistry) beanFactory).removeBeanDefinition("mongoTemplate");
            AbstractBeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(DynamicMongoTemplate.class, () -> {
                return dynamicMongoTemplate(
                        beanFactory.getBean(MongoDatabaseFactory.class),
                        beanFactory.getBean(MongoConverter.class),
                        beanFactory.getBean(MongoDynamicProperties.class)
                );
            }).getBeanDefinition();
            ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition("mongoTemplate", bd);
            return;
        }
        log.warn("{} 不是 BeanDefinitionRegistry 类型, 动态数据源注册失败", beanFactory.getClass());
    }

    /**
     * @see EnableMongoRepositories#mongoTemplateRef()
     */
    private DynamicMongoTemplate dynamicMongoTemplate(MongoDatabaseFactory factory,
                                                      MongoConverter converter,
                                                      MongoDynamicProperties properties) {
        Assert.notNull(properties.getDatasources(), "datasources not null");

        MongoTemplate defaultTemplate = new MongoTemplate(factory, converter);
        DynamicMongoTemplate dynamicMongoTemplate = new DynamicMongoTemplate(factory, converter, defaultTemplate);

        // 映射关系 集合名 -> 连接名
        Map<String, String> mappings = new HashMap<>(8);
        Map<String, MongoTemplate> templateMappings = new HashMap<>(4);

        // 填充映射关系
        populateMappings(converter, properties, mappings, templateMappings);

        dynamicMongoTemplate.setTemplateMappings(templateMappings);
        dynamicMongoTemplate.setCollectionNameDsMappings(mappings);
        return dynamicMongoTemplate;
    }

    private void populateMappings(MongoConverter converter,
                                  MongoDynamicProperties dynamicProperties,
                                  Map<String, String> mappings,
                                  Map<String, MongoTemplate> templateMappings) {
        for (Map.Entry<String, MongoDynamicProperties.MongoConfig> entry : dynamicProperties.getDatasources().entrySet()) {
            Assert.notNull(entry.getValue(), "config must not null");
            Assert.hasText(entry.getValue().getUri(), "uri must not empty");

            List<String> exclude = entry.getValue().getExclude();
            List<String> include = entry.getValue().getInclude();

            if (CollUtil.isNotEmpty(exclude) && CollUtil.isNotEmpty(include)) {
                throw new IllegalArgumentException("exclude 和 include 不能同时设值");
            }

            String dsName = entry.getKey();
            String dbName = Objects.requireNonNull(new ConnectionString(entry.getValue().getUri()).getDatabase());
            MongoClient client = MongoClients.create(entry.getValue().getUri());
            MongoDatabase db = client.getDatabase(dbName);
            List<String> collectionNames = CollUtil.newArrayList(db.listCollectionNames());
            if (CollUtil.isEmpty(collectionNames)) {
                continue;
            }

            // 填充 集合名 -> 连接名 映射关系
            if (CollUtil.isNotEmpty(include)) {
                List<String> names = include.stream()
                        .filter(collectionNames::contains)
                        .distinct().collect(Collectors.toList());
                addMapping(names, mappings, dsName);
            } else if (CollUtil.isNotEmpty(exclude)) {
                List<String> names = collectionNames.stream()
                        .filter(name -> !exclude.contains(name))
                        .distinct().collect(Collectors.toList());
                addMapping(names, mappings, dsName);
            } else {
                addMapping(collectionNames, mappings, dsName);
            }

            // 给每个数据源创建一个 mongoTemplate
            SimpleMongoClientDatabaseFactory factory =
                    new SimpleMongoClientDatabaseFactory(client, dbName);

            MongoTemplate template = new MongoTemplate(factory, converter);

            if (templateMappings.put(dsName, template) != null) {
                throw new IllegalArgumentException("数据源[" + dsName + "]已经存在 mongoTemplate 映射");
            }

            log.info("添加动态数据源[{}], 包含集合: {}", dsName, mappings.keySet());
        }
    }

    private void addMapping(List<String> collectionNames, Map<String, String> mappings, String dsName) {
        collectionNames.forEach(collectionNm -> {
            String old = mappings.put(collectionNm, dsName);
            if (old != null) {
                throw new IllegalArgumentException("集合[" + collectionNm + "]已经存在数据源[" + old + "]映射, 检查集合是否重名");
            }
        });
    }

}
