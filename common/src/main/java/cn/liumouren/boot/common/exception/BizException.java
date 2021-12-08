package cn.liumouren.boot.common.exception;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 业务异常
 * <p> 主要针对远程调用
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/25 09:53
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BizException extends Exception {

    /**
     * 默认 null, 表示未知业务异常状态码
     */
    private Integer code;
    /**
     * 异常原因
     */
    private String reason;
    /**
     * 被调用方 app
     */
    private String app;

    public BizException(int code, String message, String reason, String app) {
        super(message);
        this.code = code;
        this.reason = reason;
        this.app = app;
    }

}
