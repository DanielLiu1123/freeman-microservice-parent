package cn.liumouren.boot.web;

import cn.liumouren.boot.web.component.FreemanWebExceptionHandler;
import cn.liumouren.boot.web.component.FreemanWebLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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

}
