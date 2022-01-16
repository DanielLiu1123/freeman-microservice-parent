package cn.liumouren.boot.redis;

import cn.liumouren.boot.redis.codec.LocalDateTimeSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * 方便生成指定类型的 {@link RedisTemplate}
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 13:35
 */
@SuppressWarnings("all")
public final class RedisTemplateUtil {

    /**
     * 生成 V 为指定类型的 redisTemplate
     *
     * @param factory {@link RedisConnectionFactory}
     * @param clz 指定类型Class
     * @param <T> 类型
     * @return {@link RedisTemplate}
     */
    public static <T> RedisTemplate<String, T> build(RedisConnectionFactory factory, Class<T> clz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        FastJsonRedisSerializer<T> serializer = new FastJsonRedisSerializer<>(clz);
        FastJsonConfig config = new FastJsonConfig();

        // serialize
        SerializeConfig serializeConfig = config.getSerializeConfig();
        serializeConfig.put(LocalDateTime.class, LocalDateTimeSerializer.instance);

        serializer.setFastJsonConfig(config);

        template.setDefaultSerializer(serializer);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        return template;
    }

    public static Class getValueType(RedisTemplate redisTemplate) {
        try {
            FastJsonRedisSerializer serializer = (FastJsonRedisSerializer) redisTemplate.getDefaultSerializer();
            Field type = FastJsonRedisSerializer.class.getDeclaredField("type");
            type.setAccessible(true);
            return (Class) type.get(serializer);
        } catch (Exception e) {
            throw new IllegalArgumentException("get entity class error.", e);
        }
    }

    public static FastJsonConfig getFastJsonConfig(RedisTemplate redisTemplate) {
        FastJsonRedisSerializer serializer = (FastJsonRedisSerializer) redisTemplate.getDefaultSerializer();
        return serializer.getFastJsonConfig();
    }

}
