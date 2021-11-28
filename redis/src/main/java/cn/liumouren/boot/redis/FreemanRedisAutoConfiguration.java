package cn.liumouren.boot.redis;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 01:15
 */
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class FreemanRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return RedisTemplateUtil.build(redisConnectionFactory, Object.class);
    }

    @Bean
    @ConditionalOnMissingBean(RedisUtil.class)
    public RedisUtil<?> redisUtil(RedisTemplate<String, ?> redisTemplate) {
        return new RedisUtil<>(redisTemplate);
    }

}
