package cn.liumouren.boot.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 方便生成指定类型的 {@link RedisTemplate}
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 13:35
 */
public final class RedisTemplateUtil {

    /**
     * 生成指定类型的 redisTemplate
     *
     * @param factory {@link RedisConnectionFactory}
     * @param clz 指定类型Class
     * @param <T> 类型
     * @return {@link RedisTemplate}
     */
    public static <T> RedisTemplate<String, T> build(RedisConnectionFactory factory, Class<T> clz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

        // 解决LocalDateTime序列化问题
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        return build(factory, clz, mapper);
    }

    /**
     * 生成指定类型的 redisTemplate
     *
     * @param factory {@link RedisConnectionFactory}
     * @param clz 指定类型Class
     * @param mapper  {@link ObjectMapper}
     * @param <T> 类型
     * @return {@link RedisTemplate}
     */
    public static <T> RedisTemplate<String, T> build(RedisConnectionFactory factory, Class<T> clz, ObjectMapper mapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(clz);
        serializer.setObjectMapper(mapper);

        template.setDefaultSerializer(serializer);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        return template;
    }

}
