package cn.liumouren.boot.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * redis 工具类, 我们项目里不应该直接依赖 {@link RedisTemplate}
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/12/4 12:27 PM
 * @param <T> 操作类型
 */
@Getter
@Setter
public class RedisUtil<T> {
    private RedisTemplate<String, T> template;

    public RedisUtil(RedisTemplate<String, T> template) {
        this.template = template;
    }

    /**
     * value
     * <p> set 操作, 永不过期
     *
     * @param key key
     * @param value value
     */
    public void set(String key, T value) {
        template.opsForValue().set(key, value);
    }

    /**
     * value
     * <p> 带过期时间的 set 操作
     *
     * @param key key
     * @param value value
     * @param ttl 过期时间
     */
    public void set(String key, T value, Duration ttl) {
        template.opsForValue().set(key, value, ttl);
    }

    /**
     * value
     * <p> get 操作
     *
     * @param key key
     * @return T
     */
    public T get(String key) {
        return template.opsForValue().get(key);
    }

    // ================== hash 操作 ====================

    /**
     * hash
     * <p> 批量插入数据
     *
     * @param key key
     * @param map map
     */
    public void putAll(String key, Map<String, T> map) {
        template.opsForHash().putAll(key, map);
    }

    /**
     * hash
     * <p> 插入一条数据
     *
     * @param key key
     * @param hashKey hashKey
     * @param t T
     */
    public void put(String key, String hashKey, T t) {
        template.opsForHash().put(key, hashKey, t);
    }

    /**
     * hash
     * <p> 获取一条数据
     *
     * @param key key
     * @param hashKey hashKey
     * @return T
     */
    @SuppressWarnings("unchecked")
    public T hget(String key, String hashKey) {
        return (T) template.opsForHash().get(key, hashKey);
    }

    // ================== 通用操作 ====================

    /**
     * 删除一个 key
     *
     * @param key key
     * @return 是否删除成功
     */
    public Boolean delete(String key) {
        return template.delete(key);
    }

    /**
     * 给指定 key 设置过期时间
     *
     * @param key key
     * @param ttl 存活时间
     * @return 是否设置成功
     */
    public Boolean expire(String key, Duration ttl) {
        return template.expire(key, ttl);
    }

    /**
     * 让指定 key 在指定时间过期
     *
     * @param key key
     * @param date Date
     * @return 是否设置成功
     */
    public Boolean expire(String key, Date date) {
        return template.expireAt(key, date);
    }

}
