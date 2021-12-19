package cn.liumouren.boot.db.mongodb;

import cn.hutool.core.collection.CollUtil;
import cn.liumouren.boot.db.mongodb.core.DynamicMongoTemplate;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
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

import static cn.liumouren.boot.db.mongodb.MongoDynamicProperties.MongoConfig;
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

    /**
     * 注意这里 bean 名字 mongoTemplate 覆盖了 spring 默认装配
     * <p> 主要目的是为了让 jpa 也走这个 MongoOperations
     * <p> note: 需要开启 spring.main.allow-bean-definition-overriding=true
     *
     * @see EnableMongoRepositories#mongoTemplateRef()
     */
    @Bean
    public MongoOperations mongoTemplate(MongoDatabaseFactory factory,
                                         MongoConverter converter,
                                         MongoDynamicProperties properties) {
        Assert.notNull(properties.getDatasources(), "datasources not null");

        MongoTemplate template = new MongoTemplate(factory, converter);
        DynamicMongoTemplate dynamicMongoTemplate = new DynamicMongoTemplate(factory, converter, template);

        // 映射关系 集合名 -> 连接名
        Map<String, String> mappings = new HashMap<>(32);
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
        for (Map.Entry<String, MongoConfig> entry : dynamicProperties.getDatasources().entrySet()) {
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

            MongoTemplate template = createMongoTemplate(factory, converter);

            if (templateMappings.put(dsName, template) != null) {
                throw new IllegalArgumentException("数据源[" + dsName + "]已经存在 mongoTemplate 映射");
            }
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

    private MongoTemplate createMongoTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }


}
