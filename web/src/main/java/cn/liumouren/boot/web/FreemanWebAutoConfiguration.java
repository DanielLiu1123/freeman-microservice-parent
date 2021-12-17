package cn.liumouren.boot.web;

import cn.liumouren.boot.common.exception.BizException;
import cn.liumouren.boot.common.model.Err;
import cn.liumouren.boot.web.component.FreemanWebExceptionHandler;
import cn.liumouren.boot.web.component.FreemanWebLogFilter;
import cn.liumouren.boot.web.component.UserIdArgumentResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    @RestControllerAdvice
    static class BizExceptionConfiguration {
        /**
         * 业务异常统一处理
         * <p> 我们需要抓住 {@link BizException}, 而不能直接抛异常, 而是要返回异常
         * <p> 我们需要让调用方自己处理异常
         * <p> 直接抛异常会走 spring 的默认异常处理, 达不到我们想要的目的
         *
         * @param e {@link BizException}
         * @return {@link ResponseEntity}
         */
        @ExceptionHandler(BizException.class)
        public ResponseEntity<Err> freemanBizExceptionHandler(BizException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Err.of(e));
        }

    }

}
