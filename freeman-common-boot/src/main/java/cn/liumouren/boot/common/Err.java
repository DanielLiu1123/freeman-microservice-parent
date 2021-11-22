package cn.liumouren.boot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 18:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Err {
    private int code;
    private String message;
    private String cause;

    public static Err of(int code, String message, String cause) {
        return new Err(code, message, cause);
    }

}
