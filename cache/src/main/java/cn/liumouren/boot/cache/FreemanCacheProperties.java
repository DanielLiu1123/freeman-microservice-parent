package cn.liumouren.boot.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 17:04
 */
@Data
@ConfigurationProperties("freeman.cache")
public class FreemanCacheProperties {
    private boolean enable = true;
    private Type type;

    enum Type {
        /**
         * 内存缓存, 使用 caffeine
         */
        MEMORY,
        /**
         * 分布式缓存 redis
         */
        REDIS
    }
}
