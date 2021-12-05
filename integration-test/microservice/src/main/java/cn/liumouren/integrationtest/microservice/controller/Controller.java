package cn.liumouren.integrationtest.microservice.controller;

import cn.liumouren.integrationtest.microservice.vo.UserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 *
 *
 * @author freeman
 * @date 2021/12/5 17:12
 */
@RestController
public class Controller {

    @GetMapping
    public UserVo get() {
        return new UserVo()
                .setName("freeman")
                .setBirthday(new Date());
    }
}
