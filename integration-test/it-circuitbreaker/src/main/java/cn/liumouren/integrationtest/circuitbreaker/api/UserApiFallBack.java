package cn.liumouren.integrationtest.circuitbreaker.api;

import org.springframework.stereotype.Component;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 23:19
 */
@Component
public class UserApiFallBack implements UserApi {
    @Override
    public String ok(boolean ok) {
        return "fallback";
    }
}
