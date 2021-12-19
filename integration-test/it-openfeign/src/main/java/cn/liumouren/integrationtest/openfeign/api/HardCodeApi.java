package cn.liumouren.integrationtest.openfeign.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 04:11
 */
@FeignClient("hard-code")
public interface HardCodeApi {

    @GetMapping("/get")
    String get();

}
