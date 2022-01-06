package cn.liumouren.integrationtest.circuitbreaker.api;

import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 23:17
 */
@FeignClient(value = "user", url = "http://localhost", fallback = UserApiFallBack.class)
public interface UserApi {

    @GetMapping("/{ok}")
    String ok(@Param("ok") boolean ok);

}
