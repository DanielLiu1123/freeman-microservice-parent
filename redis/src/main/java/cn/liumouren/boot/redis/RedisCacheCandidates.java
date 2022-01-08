package cn.liumouren.boot.redis;

import java.util.Set;

/**
 * Mark the object type that we need to cache.
 *
 * @author freeman
 * @date 2022/1/8 18:05
 */
public interface RedisCacheCandidates {

    /**
     * object types which need cache
     * @return classes
     */
    Set<Class> caching();

}
