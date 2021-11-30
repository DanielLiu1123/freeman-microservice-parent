package cn.liumouren.boot.openfeign.component.feign;

import cn.liumouren.boot.common.exception.BizException;
import cn.liumouren.boot.common.model.Err;
import com.alibaba.fastjson.JSON;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/25 16:53
 */
public class FreemanErrorDecoder extends ErrorDecoder.Default {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemanErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        byte[] body = {};
        try {
            if (response.body() != null) {
                body = Util.toByteArray(response.body().asInputStream());
            }
            Err err = JSON.parseObject(new String(body), Err.class);
            if (err != null && err.getCode() != null) {
                LOGGER.warn("openfeign 远程调用服务端异常, status: {}, path: {}, code: {}, message: {}",
                        response.status(), response.request().url(), err.getCode(), err.getMessage());
                return new BizException(err.getCode(), err.getMessage(), err.getReason(), err.getApp());
            } else {
                FeignException feignException = FeignException.errorStatus(methodKey, response);
                if (feignException instanceof FeignException.FeignClientException) {
                    LOGGER.warn("openfeign 远程调用客户端异常, status: {}, path: {}",
                            response.status(), response.request().url());
                } else if (feignException instanceof FeignException.FeignServerException) {
                    LOGGER.warn("openfeign 远程调用服务端未知异常, status: {}, path: {}",
                            response.status(), response.request().url());
                } else {
                    LOGGER.warn("openfeign 远程调用异常, status: {}, path: {}",
                            response.status(), response.request().url());
                }
            }
        } catch (Exception ignore) {

        }
        // 没有 code 或者出现任何异常, 我们走 feign 默认处理
        return super.decode(methodKey, response);
    }

}
