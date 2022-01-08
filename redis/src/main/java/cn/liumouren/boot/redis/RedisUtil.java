package cn.liumouren.boot.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * redis 工具类, 我们项目里不应该直接依赖 {@link RedisTemplate}
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/12/4 12:27 PM
 */
@SuppressWarnings("all")
public final class RedisUtil {

    private RedisUtil() {
    }

    private static RedisTemplate defaultTemplate;

    private static Map<Class, RedisTemplate> mappings;

    public static void setTemplates(RedisTemplate defaultTemplate, List<RedisTemplate> templates) {
        RedisUtil.defaultTemplate = defaultTemplate;
        RedisUtil.mappings = templates.stream()
                .collect(Collectors.toMap(RedisTemplateUtil::getValueType, Function.identity()));
    }

    /**
     * value
     * <p> set 操作, 永不过期
     *
     * @param key key
     * @param value value
     */
    public static <T> void set(String key, T value) {
        chose(value.getClass()).opsForValue().set(key, value);
    }

    /**
     * value
     * <p> 带过期时间的 set 操作
     *
     * @param key key
     * @param value value
     * @param ttl 过期时间
     */
    public static <T> void set(String key, T value, Duration ttl) {
        chose(value.getClass()).opsForValue().set(key, value, ttl);
    }

    /**
     * value
     * <p> get 操作
     *
     * @param key key
     * @return T
     */
    public static <T> T get(String key, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return cast(template.opsForValue().get(key), clz, template);
    }


    // ================== hash 操作 ====================

    /**
     * hash
     * <p> 批量插入数据
     *
     * @param key key
     * @param map map
     */
    public static <T> void putAll(String key, Map<String, T> map, Class<T> clz) {
        // map may empty
        chose(clz).opsForHash().putAll(key, map);
    }

    /**
     * hash
     * <p> 插入一条数据
     *
     * @param key key
     * @param hashKey hashKey
     * @param t T
     */
    public static <T> void put(String key, String hashKey, T t) {
        chose(t.getClass()).opsForHash().put(key, hashKey, t);
    }

    /**
     * hash
     * <p> 获取一条数据
     *
     * @param key key
     * @param hashKey hashKey
     * @return T
     */
    public static <T> T hget(String key, String hashKey, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return cast(template.opsForHash().get(key, hashKey), clz, template);
    }

    // ================== 通用操作 ====================

    /**
     * 删除一个 key
     *
     * @param key key
     * @return 是否删除成功
     */
    public static Boolean delete(String key) {
        // use same connect factory
        return defaultTemplate.delete(key);
    }

    /**
     * 给指定 key 设置过期时间
     *
     * @param key key
     * @param ttl 存活时间
     * @return 是否设置成功
     */
    public static Boolean expire(String key, Duration ttl) {
        return defaultTemplate.expire(key, ttl);
    }

    /**
     * 让指定 key 在指定时间过期
     *
     * @param key key
     * @param date Date
     * @return 是否设置成功
     */
    public static Boolean expire(String key, Date date) {
        return defaultTemplate.expireAt(key, date);
    }

    public static RedisTemplate getDefaultTemplate() {
        return defaultTemplate;
    }

    public static Map<Class, RedisTemplate> getMappings() {
        return mappings;
    }

    private static RedisTemplate chose(Class clz) {
        RedisTemplate template = mappings.get(clz);
        return template != null ? template : defaultTemplate;
    }

    /**
     * Transform object according to the RedisTemplate serialization configuration
     *
     * @param o o
     * @param clz target type
     * @param template RedisTemplate
     */
    private static <T> T cast(Object o, Class<T> clz, RedisTemplate template) {
        if (o instanceof JSONObject) {
            FastJsonConfig config = RedisTemplateUtil.getFastJsonConfig(template);
            String json = JSON.toJSONString(o, config.getSerializeConfig());
            o = JSON.parseObject(json, clz, config.getParserConfig());
        }
        return (T) o;
    }

}
