package cn.liumouren.integrationtest.circuitbreaker.controller;

import cn.liumouren.integrationtest.circuitbreaker.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class TestController {

    @Autowired
    @Qualifier("cn.liumouren.integrationtest.circuitbreaker.api.UserApi")
    private UserApi userApi;

    @GetMapping("/test/{ok}")
    public String ok(@PathVariable boolean ok) {
        return userApi.ok(ok);
    }

}
