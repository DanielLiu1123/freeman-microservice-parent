package cn.liumouren.boot.openfeign;

import cn.liumouren.boot.openfeign.component.feign.CompositeContract;
import cn.liumouren.boot.openfeign.component.feign.FreemanErrorDecoder;
import cn.liumouren.boot.openfeign.component.interceptor.FromAppRequestInterceptor;
import cn.liumouren.boot.openfeign.component.interceptor.InvokeMethodInterceptor;
import cn.liumouren.boot.openfeign.component.processor.PrimerBeanDefinitionPostProcessor;
import cn.liumouren.boot.openfeign.component.processor.UserIdParameterProcessor;
import feign.Contract;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 22:54
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FreemanOpenfeignProperties.class)
public class FreemanOpenfeignAutoConfiguration {

    /**
     * spring cloud openfeign 自定义注解解析
     *
     * @see AnnotatedParameterProcessor
     */
    @Configuration(proxyBeanMethods = false)
    static class AnnotatedParameterProcessorConfiguration {

        @Bean
        public AnnotatedParameterProcessor userIdAnnotatedParameterProcessor() {
            return new UserIdParameterProcessor();
        }

    }

    /**
     * feign 默认配置
     *
     * @see ErrorDecoder
     * @see Contract
     * @see feign.codec.Encoder
     * @see feign.codec.Decoder
     */
    @Configuration(proxyBeanMethods = false)
    static class CustomizeFeignConfiguration {

        @Autowired(required = false)
        private FeignClientProperties feignClientProperties;

        @Bean
        public ErrorDecoder freemanErrorDecoder() {
            return new FreemanErrorDecoder();
        }

        @Bean
        public Contract freemanContract(ConversionService feignConversionService, List<AnnotatedParameterProcessor> processors) {
            boolean decodeSlash = feignClientProperties == null || feignClientProperties.isDecodeSlash();
            return new CompositeContract(processors, feignConversionService, decodeSlash);
        }

    }


    @Bean
    public RequestInterceptor fromAppRequestInterceptor(Environment environment) {
        return new FromAppRequestInterceptor(environment.getProperty("spring.application.name"));
    }

    @Bean
    static BeanFactoryPostProcessor primerBeanDefinitionPostProcessor() {
        return new PrimerBeanDefinitionPostProcessor();
    }

    /**
     * Advisor 在创建 BeanPostProcessor 时被初始化, 不应该导致 FreemanOpenfeignAutoConfiguration 过早初始化, 所以加上 static
     * <p> 控制台会打印 Bean 'xxx' of type [xxx] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
     */
    @Bean
    static Advisor dealControllerAndApiPointcutAdvisor() {
        Advice advice = new InvokeMethodInterceptor();

        // 我们对 FeignClient bean 做一些扩展处理
        // 但是我们需要排除 Controller
        Pointcut pointcut = new ComposablePointcut(FreemanOpenfeignAutoConfiguration::isApiAndNotController);

        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    private static boolean isApiAndNotController(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, FeignClient.class) != null
                && AnnotationUtils.findAnnotation(clazz, Controller.class) == null;
    }

}
