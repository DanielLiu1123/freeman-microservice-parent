package cn.liumouren.integrationtest.circuitbreaker.api;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author freeman
 * @date 2022/1/6 23:19
 */
@Component
public class UserApiFallBackFactory implements FallbackFactory<UserApi> {

    @Override
    public UserApi create(Throwable cause) {
        return new UserApi() {
            @Override
            public String ok(boolean ok) {
                return "fallback";
            }
        };
    }

}
