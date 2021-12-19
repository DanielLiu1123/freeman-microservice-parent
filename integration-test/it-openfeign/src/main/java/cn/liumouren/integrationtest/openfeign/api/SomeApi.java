package cn.liumouren.integrationtest.openfeign.api;

import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/29 14:15
 */
@FeignClient(value = "some", url = "http://localhost:8080")
public interface SomeApi {

    @RequestLine("GET /some/{name}")
    String some(@PathVariable String name);
}
