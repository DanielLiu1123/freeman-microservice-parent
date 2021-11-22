package cn.liumouren.boot.openfeign;

import cn.liumouren.boot.openfeign.component.interceptor.FromAppRequestInterceptor;
import cn.liumouren.boot.openfeign.component.interceptor.OpenfeignInvokeMethodInterceptor;
import cn.liumouren.boot.openfeign.component.processor.PrimerBeanDefinitionPostProcessor;
import feign.RequestInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 22:54
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FreemanOpenfeignProperties.class)
public class FreemanOpenfeignAutoConfiguration {

    @Bean
    public RequestInterceptor fromAppRequestInterceptor(Environment environment) {
        return new FromAppRequestInterceptor(environment.getProperty("spring.application.name"));
    }

    @Bean
    public BeanFactoryPostProcessor primerBeanDefinitionPostProcessor() {
        return new PrimerBeanDefinitionPostProcessor();
    }

    @Bean
    public PointcutAdvisor dealControllerAndApiPointcutAdvisor() {
        Advice advice = new OpenfeignInvokeMethodInterceptor();

        // 我们对 FeignClient bean 做一些扩展处理
        // 但是我们需要排除 Controller
        Pointcut pointcut = new ComposablePointcut(this::isApiAndNotController);

        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    private boolean isApiAndNotController(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, FeignClient.class) != null
                && AnnotationUtils.findAnnotation(clazz, Controller.class) == null;
    }

}
