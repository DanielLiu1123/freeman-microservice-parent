package cn.liumouren.boot.web.component;

import cn.liumouren.boot.common.exception.BizException;
import cn.liumouren.boot.common.exception.WebException;
import cn.liumouren.boot.common.model.Err;
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

    /**
     * web 类异常统一处理
     *
     * @param e {@link WebException}
     * @return {@link Err}
     */
    @ExceptionHandler(WebException.class)
    public Err freemanWebExceptionHandler(WebException e) {
        // e.getCause() == null 说明 e 就是 cause
        String cause = e.getCause() == null ? e.toString() : e.getCause().toString();
        return Err.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), cause);
    }

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
