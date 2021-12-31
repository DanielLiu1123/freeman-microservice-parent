package cn.liumouren.boot.messaging.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 *
 * @author freeman
 * @date 2021/12/28 15:29
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Event {

    String topic() default "";

    String tag() default "";

}
