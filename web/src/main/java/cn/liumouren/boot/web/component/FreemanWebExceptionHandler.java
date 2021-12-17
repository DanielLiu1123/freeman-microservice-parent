package cn.liumouren.boot.web.component;

import cn.liumouren.boot.common.model.Err;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 19:58
 */
@RestControllerAdvice
public class FreemanWebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemanWebExceptionHandler.class);

    /**
     * 异常统一处理
     *
     * @param e Exception
     * @return {@link Err}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity freemanWebExceptionHandler(Exception e) {
        // e.getCause() == null 说明 e 就是 cause
        String cause = e.getCause() == null ? e.toString() : e.getCause().toString();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(cause);
    }

    /**
     * 最大只抓到 RuntimeException
     *
     * @param e e
     * @return {@link ResponseEntity}
     */
//    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity runtimeException(RuntimeException e) {
        String cause = e.getCause() == null ? e.toString() : e.getCause().toString();
        LOGGER.warn("{}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cause);
    }

}
