package cn.liumouren.boot.common;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:33
 */
public class FreemanWebException extends RuntimeException {
    public FreemanWebException() {
    }

    public FreemanWebException(String message) {
        super(message);
    }

    public FreemanWebException(String message, Throwable cause) {
        super(message, cause);
    }
}
