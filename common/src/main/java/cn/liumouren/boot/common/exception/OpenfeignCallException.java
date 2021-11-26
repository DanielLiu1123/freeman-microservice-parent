package cn.liumouren.boot.common.exception;

/**
 * openFeign 远程调用异常
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:33
 */
public class OpenfeignCallException extends RuntimeException {
    public OpenfeignCallException() {
    }

    public OpenfeignCallException(String message) {
        super(message);
    }

    public OpenfeignCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
