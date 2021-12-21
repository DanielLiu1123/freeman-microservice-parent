package cn.liumouren.integrationtest.lb.clientside.controller;

import cn.liumouren.integrationtest.lb.clientside.api.Other2Api;
import cn.liumouren.integrationtest.lb.clientside.api.OtherApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author freeman
 * @date 2021/12/21 15:02
 */
@RestController
public class OtherController implements OtherApi, Other2Api {

    @Override
    @GetMapping("/getOther")
    public String getOther() {
        return "getOther";
    }

    @Override
    @GetMapping("/getOther2")
    public String getOther2() {
        return "getOther2";
    }
}
