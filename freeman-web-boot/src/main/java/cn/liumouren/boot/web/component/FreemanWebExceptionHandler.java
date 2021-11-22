package cn.liumouren.boot.web.component;

import cn.liumouren.boot.common.Err;
import cn.liumouren.boot.common.FreemanWebException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 19:58
 */
@RestControllerAdvice
public class FreemanWebExceptionHandler {

    @ExceptionHandler(FreemanWebException.class)
    public Err freemanWebExceptionHandler(FreemanWebException e) {
        // e.getCause() == null 说明 e 就是 cause
        String cause = e.getCause() == null ? e.toString(): e.getCause().toString();
        return Err.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), cause);
    }
}
