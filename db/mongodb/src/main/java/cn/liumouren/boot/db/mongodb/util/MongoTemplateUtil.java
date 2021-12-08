package cn.liumouren.boot.db.mongodb.util;

import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.Constructor;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 19:31
 */
public final class MongoTemplateUtil {
    /**
     *
     * @param factory
     * @param mongoTemplate
     * @return
     */
    public static MongoTemplate copy(MongoDatabaseFactory factory, MongoTemplate mongoTemplate) {
        try {
            Constructor<MongoTemplate> constructor = MongoTemplate.class.getDeclaredConstructor(MongoDatabaseFactory.class, MongoTemplate.class);
            constructor.setAccessible(true);
            return constructor.newInstance(factory, mongoTemplate);
        } catch (Exception e) {
            throw new RuntimeException("copy mongoTemplate failed");
        }
    }
}
