package cn.liumouren.boot.common.util;

import cn.liumouren.boot.common.constant.enumeration.Env;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.core.env.Environment;

/**
 * 环境相关工具类
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/24 17:55
 */
public final class EnvUtil {
    private static Environment environment;

    public void setEnvironment(Environment environment) {
        EnvUtil.environment = environment;
    }

    public static Env getEnv() {
        try {
            String profile = environment.getProperty("spring.profiles.active", "dev");
            return Env.valueOf(profile.toUpperCase());
        } catch (Exception ignore) {
            // 任何异常, 我们都默认当前环境为 dev
        }
        return Env.DEV;
    }

}
