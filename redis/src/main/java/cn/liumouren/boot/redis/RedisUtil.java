package cn.liumouren.boot.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * redis 工具类, 我们项目里不应该直接依赖 {@link RedisTemplate}
 *
 * <p> 如果该工具类没有提供你需要的方法, 使用 {@link #chose(Class)} 方法选择正确的 redisTemplate 进行操作
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/12/4 12:27 PM
 */
@SuppressWarnings("all")
public final class RedisUtil {

    private static RedisTemplate defaultTemplate;

    private static Map<Class, RedisTemplate> entityTempalteMappings;

    public static void setDefaultTemplate(RedisTemplate defaultTemplate) {
        RedisUtil.defaultTemplate = defaultTemplate;
    }

    public static void setEntityTempalteMappings(List<RedisTemplate> entityTempalteMappings) {
        RedisUtil.entityTempalteMappings = entityTempalteMappings.stream()
                .collect(Collectors.toMap(RedisTemplateUtil::getValueType, Function.identity()));
    }

    // ================== value 操作 ====================

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
        return castOne(template.opsForValue().get(key), clz, template);
    }


    // ================== hash 操作 ====================

    /**
     * hash
     * <p> 批量插入数据
     *
     * @param key key
     * @param map map
     */
    public static <T> void putAll(String key, Map<String, T> map) {
        checkNotNull(map);
        if (map.isEmpty()) {
            defaultTemplate.opsForHash().putAll(key, map);
            return;
        }
        chose(map.values().iterator().next().getClass()).opsForHash().putAll(key, map);
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
        return castOne(template.opsForHash().get(key, hashKey), clz, template);
    }


    // ================== List操作 ====================

    public static <T> T leftPop(String key, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return castOne(template.opsForList().leftPop(key), clz, template);
    }

    public static <T> T leftPop(String key, Class<T> clz, Duration timeout) {
        RedisTemplate template = chose(clz);
        return castOne(template.opsForList().leftPop(key, timeout), clz, template);
    }

    public static <T> Long leftPush(String key, T t) {
        RedisTemplate template = chose(t.getClass());
        return template.opsForList().leftPush(key, t);
    }

    public static <T> Long leftPushAll(String key, Collection<T> values) {
        checkNotNull(values);
        if (values.isEmpty()) {
            return defaultTemplate.opsForList().leftPushAll(key, values);
        }
        RedisTemplate template = chose(values.iterator().next().getClass());
        return template.opsForList().leftPushAll(key, values);
    }

    public static <T> T rightPop(String key, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return castOne(template.opsForList().rightPop(key), clz, template);
    }

    public static <T> T rightPop(String key, Class<T> clz, Duration timeout) {
        RedisTemplate template = chose(clz);
        return castOne(template.opsForList().rightPop(key, timeout), clz, template);
    }

    public static <T> Long rightPush(String key, T t) {
        RedisTemplate template = chose(t.getClass());
        return template.opsForList().rightPush(key, t);
    }

    public static <T> Long rightPushAll(String key, Collection<T> values) {
        checkNotNull(values);
        if (values.isEmpty()) {
            return defaultTemplate.opsForList().rightPushAll(key, values);
        }
        RedisTemplate template = chose(values.iterator().next().getClass());
        return template.opsForList().rightPushAll(key, values);
    }

    public static Long sizeForList(String key) {
        return defaultTemplate.opsForList().size(key);
    }

    public static <T> List<T> range(String key, long start, long end, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return castMany(template.opsForList().range(key, start, end), clz, template);
    }


    // ================== Set 操作 ====================

    /**
     * Set operation.
     * @return the number of added element.
     */
    public static <T> Long add(String key, T... values) {
        RedisTemplate template = chose(values[0].getClass());
        return template.opsForSet().add(key, values);
    }

    /**
     * Set operation.
     * @return the number of removed elements.
     */
    public static <T> Long remove(String key, T... values) {
        checkArrayNotEmpty(values);
        RedisTemplate template = chose(values[0].getClass());
        return template.opsForSet().remove(key, values);
    }

    /**
     * Set operation.
     */
    public static <T> Set<T> members(String key, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return new HashSet<>(castMany(template.opsForSet().members(key), clz, template));
    }

    public static <T> Set<T> difference(Collection<String> keys, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return new HashSet<>(castMany(template.opsForSet().difference(keys), clz, template));
    }

    public static <T> Set<T> intersect(Collection<String> keys, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return new HashSet<>(castMany(template.opsForSet().intersect(keys), clz, template));
    }

    public static Long sizeForSet(String key) {
        return defaultTemplate.opsForSet().size(key);
    }

    // ================== Zset 操作 ====================


    public static <T> Boolean zadd(String key, T value, double score) {
        RedisTemplate template = chose(value.getClass());
        return template.opsForZSet().add(key, value, score);
    }

    public static <T> Long zadd(String key, Set<TypedTuple> tuples) {
        checkNotNull(tuples);
        if (tuples.isEmpty()) {
            defaultTemplate.opsForZSet().add(key, tuples);
        }
        RedisTemplate template = chose(tuples.iterator().next().getValue().getClass());
        return template.opsForZSet().add(key, tuples);
    }

    public static <T> Set<T> zrange(String key, long start, long end, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return new HashSet<>(castMany(template.opsForZSet().range(key, start, end), clz, template));
    }

    public static <T> Set<T> zreverseRange(String key, long start, long end, Class<T> clz) {
        RedisTemplate template = chose(clz);
        return new HashSet<>(castMany(template.opsForZSet().reverseRange(key, start, end), clz, template));
    }

    /**
     * @return new score
     */
    public static <T> Double zincrementScore(String key, T value, double delta) {
        RedisTemplate template = chose(value.getClass());
        return template.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * Determine the index of element with value in a sorted set.
     * <p> start with 0
     */
    public static <T> Long zrank(String key, T value) {
        RedisTemplate template = chose(value.getClass());
        return template.opsForZSet().rank(key, value);
    }

    public static <T> Long zremove(String key, T... values) {
        checkArrayNotEmpty(values);
        RedisTemplate template = chose(values[0].getClass());
        return template.opsForZSet().remove(key, values);
    }

    public static Long zcount(String key, double min, double max) {
        return defaultTemplate.opsForZSet().count(key, min, max);
    }

    public static Long sizeForZset(String key) {
        return defaultTemplate.opsForZSet().size(key);
    }

    // TODO
    // ================== hyperloglog 操作 ====================

    /**
     * @return 如果有值被添加到了 key, 返回 1, 如果没有值被添加到了 key, 返回 0
     */
    public static <T> Long pfadd(String key, T... values) {
        checkArrayNotEmpty(values);
        RedisTemplate template = chose(values[0].getClass());
        return template.opsForHyperLogLog().add(key, values);
    }

    public static Long pfcount(String... key) {
        return defaultTemplate.opsForHyperLogLog().size(key);
    }

    public static Long pfmerge(String destination, String... sourceKeys) {
        return defaultTemplate.opsForHyperLogLog().union(destination, sourceKeys);
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

    public static Long delete(String... key) {
        return defaultTemplate.delete(Arrays.asList(key));
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

    public static Map<Class, RedisTemplate> getEntityTempalteMappings() {
        return entityTempalteMappings;
    }

    public static RedisTemplate chose(Class clz) {
        return Optional.ofNullable(entityTempalteMappings.get(clz)).orElse(defaultTemplate);
    }

    /**
     * Transform object according to the RedisTemplate serialization configuration
     *
     * @param o o
     * @param clz target type
     * @param template RedisTemplate
     */
    private static <T> T castOne(Object o, Class<T> clz, RedisTemplate template) {
        if (o.getClass() != clz && Map.class.isAssignableFrom(o.getClass())) {
            // need parse again
            RedisSerializer serializer = template.getDefaultSerializer();
            if (serializer instanceof FastJsonRedisSerializer) {
                FastJsonConfig config = RedisTemplateUtil.getFastJsonConfig(template);
                String json = JSON.toJSONString(o, config.getSerializeConfig());
                o = JSON.parseObject(json, clz, config.getParserConfig());
            }
        }
        return (T) o;
    }

    private static <T> List<T> castMany(Collection coll, Class<T> clz, RedisTemplate template) {
        if (coll == null || coll.isEmpty()) {
            return new ArrayList<>();
        }

        Class<?> elementClass = coll.iterator().next().getClass();
        if (elementClass != clz && Map.class.isAssignableFrom(elementClass)) {
            // need parse again
            RedisSerializer serializer = template.getDefaultSerializer();
            if (serializer instanceof FastJsonRedisSerializer) {
                FastJsonConfig config = RedisTemplateUtil.getFastJsonConfig(template);
                String json = JSON.toJSONString(coll, config.getSerializeConfig());
                return JSON.parseArray(json, clz, config.getParserConfig());
            }
        }
        return new ArrayList<>(coll);
    }

    private static void checkNotNull(Object mapOrCollection) {
        if (mapOrCollection == null) {
            throw new IllegalArgumentException(mapOrCollection.getClass().getSimpleName().toLowerCase() + " can't be null");
        }
    }

    private static void checkArrayNotEmpty(Object[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("can not be empty.");
        }
    }

}
