package cn.liumouren.integrationtest.lb.serverside.api;

import cn.liumouren.boot.lb.common.Namespace;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *
 *
 * @author freeman
 * @date 2021/12/21 02:18
 */
@Namespace("api")
@FeignClient("server-side")
public interface ServerSideApi {

}
