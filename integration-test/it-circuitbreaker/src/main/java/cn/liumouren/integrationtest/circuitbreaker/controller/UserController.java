package cn.liumouren.integrationtest.circuitbreaker.controller;

import cn.liumouren.integrationtest.circuitbreaker.api.UserApi;
import feign.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 23:18
 */
@RestController
public class UserController implements UserApi {

    @Override
    @GetMapping("/{ok}")
    public String ok(@PathVariable boolean ok) {
        if (ok) {
            return "ok";
        }
        throw new RuntimeException("fail");
    }

}
