package cn.liumouren.boot.openfeign.component.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p> 因为我们需要预防在本模块注入 xxxApi 的情况
 * <p> 这时候会有 xxxApi 和 xxxController 两个 bean, 会注入失败
 * <p> 所以做如下处理:
 * <ul>
 *     <li> 给有注解 @FeignClient 的接口的 beanDefinition 设置 primer 为 false</li>
 *     <li> 给有注解 @Controller  的类的 beanDefinition 设置 primer 为 true</li>
 * </ul>
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/22 16:20
 */
public class PrimerBeanDefinitionPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // beanFactory.getBeanNamesForAnnotation(..) 会导致 FactoryBean 的过早初始化, 我们不使用

        Set<String> controllers = findBoth(beanFactory);

        // className 就是 feignClient 的 beanName
        Set<String> feignClients = findFeignClients(beanFactory, controllers);

        // 找到被两个注解都注释的 controller, 设置 primer 为 true
        for (String name : controllers) {
            beanFactory.getBeanDefinition(name).setPrimary(true);
        }

        // 设置 FeignClient 接口 primer 为 false
        for (String name : feignClients) {
            beanFactory.getBeanDefinition(name).setPrimary(false);
        }
    }

    private Set<String> findBoth(ConfigurableListableBeanFactory beanFactory) {
        Set<String> controllerAndApis = new HashSet<>();
        String[] names = beanFactory.getBeanDefinitionNames();
        for (String name : names) {
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            if (definition instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata metadata = ((AnnotatedBeanDefinition) definition).getMetadata();
                if (metadata.hasAnnotation(RestController.class.getName())
                        || metadata.hasAnnotation(Controller.class.getName())) {

                    // 先判断有没有 @RestController 或者 @Controller
                    String className = metadata.getClassName();
                    try {
                        Class<?> aClass = Class.forName(className);
                        if (AnnotationUtils.findAnnotation(aClass, FeignClient.class) != null) {

                            // 再判断是否有 @FeignClient 注解
                            controllerAndApis.add(name);
                        }
                    } catch (ClassNotFoundException ignore) {
                        // ignore
                    }
                }
            }
        }
        return controllerAndApis;
    }

    private Set<String> findFeignClients(ConfigurableListableBeanFactory beanFactory, Collection<String> beanNames) {
        return beanNames.stream()
                .map(beanName -> {
                    List<String> interfaceNames = new ArrayList<>();
                    try {
                        Class<?> clazz = Class.forName(beanFactory.getBeanDefinition(beanName).getBeanClassName());
                        Arrays.stream(clazz.getInterfaces())
                                // 可能有多个被 @FeignClient 注解的接口
                                .filter(inter -> inter.getAnnotation(FeignClient.class) != null)
                                .forEach(inter -> interfaceNames.add(inter.getName()));
                    } catch (ClassNotFoundException ignore) {
                        // Class.forName(..) 异常, 我们正常忽略
                    }
                    return interfaceNames;
                })
                .flatMap(Collection::stream)
                // 可能会出现重复情况
                .collect(Collectors.toSet());
    }

}
