package cn.liumouren.boot.common.model;

import cn.liumouren.boot.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Err {
    /**
     * 业务异常状态码
     */
    private Integer code;
    private String message;
    private String reason;
    private String app;

    public static Err of(Integer code, String message, String cause) {
        return new Err(code, message, cause, null);
    }

    public static Err of(Integer code, String message, String cause, String clazz) {
        return new Err(code, message, cause, clazz);
    }

    public static Err of(BizException e) {
        String cause = e.getReason() == null ? e.toString() : e.getReason();
        return Err.of(e.getCode(), e.getMessage(), cause, e.getApp());
    }



}
