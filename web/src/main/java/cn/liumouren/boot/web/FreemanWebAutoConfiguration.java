package cn.liumouren.boot.web;

import cn.liumouren.boot.web.component.FreemanWebExceptionHandler;
import cn.liumouren.boot.web.component.FreemanWebLogFilter;
import cn.liumouren.boot.web.component.UserIdArgumentResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 17:56
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FreemanWebProperties.class)
public class FreemanWebAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = "freeman.web.log.enable", havingValue = "true", matchIfMissing = true)
    @Import(FreemanWebLogFilter.class)
    static class LogFilterConfiguration {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = "freeman.web.exception-handler.enable", havingValue = "true", matchIfMissing = true)
    @Import(FreemanWebExceptionHandler.class)
    static class ExceptionHandlerConfiguration {
    }

    @Configuration(proxyBeanMethods = false)
    static class ArgumentResolverConfiguration implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            // 支持 @UserId 注解
            resolvers.add(new UserIdArgumentResolver());
        }
    }

}
