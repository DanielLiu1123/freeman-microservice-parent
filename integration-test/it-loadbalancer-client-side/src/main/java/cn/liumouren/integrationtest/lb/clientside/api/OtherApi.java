package cn.liumouren.integrationtest.lb.clientside.api;

import cn.liumouren.boot.lb.common.Namespace;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 14:32
 */
@FeignClient("client-side-app")
@Namespace("api")
public interface OtherApi {

    @GetMapping("/getOther")
    String getOther();
}
