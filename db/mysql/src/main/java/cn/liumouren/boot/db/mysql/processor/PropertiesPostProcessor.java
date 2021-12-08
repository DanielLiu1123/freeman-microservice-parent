package cn.liumouren.boot.db.mysql.processor;

import cn.liumouren.boot.db.mysql.MysqlDynamicProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 *
 *
 * @author freeman
 * @date 2021/12/6 23:36
 */
public class PropertiesPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        MysqlDynamicProperties properties = beanFactory.getBean(MysqlDynamicProperties.class);
        // 根据配置文件生成 datasource bean definition
    }
}
