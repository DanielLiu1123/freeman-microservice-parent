package cn.liumouren.boot.redis;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/27 01:15
 */
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class FreemanRedisAutoConfiguration implements SmartInitializingSingleton {

    private final BeanFactory beanFactory;
    private final RedisConnectionFactory factory;
    private final ObjectProvider<RedisCacheCandidates> redisCacheCandidates;

    public FreemanRedisAutoConfiguration(BeanFactory beanFactory,
                                         ObjectProvider<RedisCacheCandidates> redisCacheCandidates,
                                         RedisConnectionFactory factory) {
        this.beanFactory = beanFactory;
        this.factory = factory;
        this.redisCacheCandidates = redisCacheCandidates;
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        return RedisTemplateUtil.build(factory, Object.class);
    }

    @Override
    public void afterSingletonsInstantiated() {
        setRedisUtilTemplates();
    }

    @SuppressWarnings("all")
    private void setRedisUtilTemplates() {
        RedisTemplate defaultTemplate = beanFactory.getBean("redisTemplate", RedisTemplate.class);
        List<RedisTemplate> templates = new ArrayList<>();
        redisCacheCandidates.ifAvailable(rc -> {
            rc.caching().stream().forEach(clz -> {
                templates.add(buildTemplate(factory, defaultTemplate, clz));
            });
        });
        RedisUtil.setTemplates(defaultTemplate, templates);
    }

    @SuppressWarnings("all")
    private RedisTemplate buildTemplate(RedisConnectionFactory factory, RedisTemplate redisTemplate, Class aClass) {
        RedisTemplate template = RedisTemplateUtil.build(factory, aClass);
        Field loader = ReflectionUtils.findField(RedisTemplate.class, "classLoader");
        loader.setAccessible(true);
        template.setBeanClassLoader((ClassLoader) ReflectionUtils.getField(loader, redisTemplate));
        template.afterPropertiesSet();
        return template;
    }

}
