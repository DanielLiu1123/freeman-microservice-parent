package cn.liumouren.boot.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:00
 */
@Data
@ConfigurationProperties("freeman.web")
public class FreemanWebProperties {

    private Log log;
    private ExceptionHandler exceptionHandler;

    @Data
    static class Log {
        private boolean enable = true;
    }

    @Data
    static class ExceptionHandler {
        private boolean enable = true;
    }

}
