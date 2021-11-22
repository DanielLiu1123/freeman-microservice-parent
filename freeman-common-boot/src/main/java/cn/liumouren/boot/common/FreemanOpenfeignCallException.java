package cn.liumouren.boot.common;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:33
 */
public class FreemanOpenfeignCallException extends RuntimeException{
    public FreemanOpenfeignCallException() {
    }

    public FreemanOpenfeignCallException(String message) {
        super(message);
    }

    public FreemanOpenfeignCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
