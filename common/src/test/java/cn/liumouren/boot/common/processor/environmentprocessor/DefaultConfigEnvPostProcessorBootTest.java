package cn.liumouren.boot.common.processor.environmentprocessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/24 09:44
 */
@SpringBootTest(
        classes = DefaultConfigEnvPostProcessorBootTest.class,
        properties = {
                "spring.application.name=default-config-env-postprocessor-boot-test",
                "feign.okhttp.enabled=false"
        }
)
@EnableAutoConfiguration
public class DefaultConfigEnvPostProcessorBootTest {

    @Autowired
    private Environment environment;
    @Value("${spring.main.allow-bean-definition-overriding}")
    private boolean allowOverriding;
    @Value("${feign.okhttp.enabled}")
    private boolean enableOkhttp;

    @Test
    public void test_defaultConfigIsWorking() {
        // 默认配置里为 true, 验证是否生效
        assertTrue(allowOverriding);
        // 默认配置里为 true, 验证优先级是否正确
        assertFalse(enableOkhttp);

        assertEquals("UTF-8", environment.getProperty("server.servlet.encoding.charset"));
        assertEquals("true", environment.getProperty("spring.main.allow-bean-definition-overriding"));
        assertEquals("false", environment.getProperty("feign.okhttp.enabled"));
    }

}
