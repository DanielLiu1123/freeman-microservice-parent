package cn.liumouren.boot.db.mongodb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

import static cn.liumouren.boot.db.mongodb.MongoDynamicProperties.PREFIX;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 16:28
 */
@Data
@ConfigurationProperties(PREFIX)
public class MongoDynamicProperties {
    public static final String PREFIX = "spring.data.mongodb.dynamic";

    private boolean enable = true;

    private Map<String, MongoConfig> datasources;

    @Data
    public static class MongoConfig {
        /**
         * 连接信息
         */
        private String uri;
        /**
         * 包括的集合名称, 不写默认包含所有
         * <p> note: 如果一个库里面有太多的集合, 建议添加该字段
         */
        private List<String> include;
        /**
         * 排除的集合名称
         */
        private List<String> exclude;
    }

}
