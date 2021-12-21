package cn.liumouren.boot.lb.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static cn.liumouren.boot.lb.common.FreemanK8sDiscoveryProperties.*;

/**
 *
 *
 * @author freeman
 * @date 2021/12/21 02:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Namespace {

    String value() default DEFAULT_NAMESPACE;

}
