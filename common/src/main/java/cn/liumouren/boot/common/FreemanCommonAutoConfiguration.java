package cn.liumouren.boot.common;

import cn.liumouren.boot.common.util.EnvUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/24 17:58
 */
public class FreemanCommonAutoConfiguration {

    @Bean
    public EnvUtil envUtil(Environment environment) {
        EnvUtil envUtil = new EnvUtil();
        envUtil.setEnvironment(environment);
        return envUtil;
    }

}
