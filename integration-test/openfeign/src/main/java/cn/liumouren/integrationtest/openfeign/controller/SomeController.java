package cn.liumouren.integrationtest.openfeign.controller;

import cn.liumouren.integrationtest.openfeign.api.SomeApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/29 14:16
 */
@RestController
public class SomeController implements SomeApi {
    @Override
    @GetMapping("/some/{name}")
    public String some(@PathVariable String name) {
        System.out.println("进入 controller, name: " + name);
        return name;
    }


}
