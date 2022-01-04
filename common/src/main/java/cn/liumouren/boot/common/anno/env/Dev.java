package cn.liumouren.boot.common.anno.env;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

/**
 *
 *
 * @author freeman
 * @date 2022/1/3 00:57
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Profile("dev")
public @interface Dev {
}
