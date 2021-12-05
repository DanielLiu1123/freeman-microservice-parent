package cn.liumouren.boot.common.processor;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * 实现 {@link EnvironmentPostProcessor} 加载默认配置文件
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/23 20:26
 */
public class DefaultConfigEnvPostProcessor implements EnvironmentPostProcessor, ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final DeferredLog LOGGER = new DeferredLog();

    /**
     * 默认加载的配置
     */
    private final String[] locations = {
            "defaultconfig/freeman-microservice.yml"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        for (String location : locations) {
            ClassPathResource resource = new ClassPathResource(location);
            // 默认配置, 最低优先级
            environment.getPropertySources().addLast(loadProperties(resource));
            LOGGER.info("add default config " + resource.getFilename());
        }
    }

    private PropertySource<?> loadProperties(Resource resource) {
        if (!resource.exists()) {
            LOGGER.warn(resource.getFilename() + " doesn't exist");
        }

        // 使用 YamlPropertiesFactoryBean 解析 yaml 文件
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(resource);

        Properties prop = bean.getObject();

        return new PropertiesPropertySource(resource.getFilename(), prop);
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        // 当触发 ApplicationEnvironmentPreparedEvent 事件时
        // postProcessEnvironment(..) 已经执行, 我们打印日志
        LOGGER.replayTo(DefaultConfigEnvPostProcessor.class);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
