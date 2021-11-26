package cn.liumouren.boot.openfeign.component.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;

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
        String[] controllerNames = beanFactory.getBeanNamesForAnnotation(Controller.class);
        String[] feignClientNames = beanFactory.getBeanNamesForAnnotation(FeignClient.class);

        // 找到被两个注解都注释的 controller, 设置 primer 为 true
        Set<String> controllers = Arrays.stream(feignClientNames)
                .filter(beanName -> Arrays.asList(controllerNames).contains(beanName))
                .collect(Collectors.toSet());

        for (String name : controllers) {
            beanFactory.getBeanDefinition(name).setPrimary(true);
        }

        // 设置 feignClient 的 primer 为 false
        // className 就是 feignClient 的 beanName
        Set<String> feignClients = getFeignClientInterfaceClassNames(beanFactory, controllers);

        for (String name : feignClients) {
            beanFactory.getBeanDefinition(name).setPrimary(false);
        }
    }

    private Set<String> getFeignClientInterfaceClassNames(ConfigurableListableBeanFactory beanFactory, Collection<String> beanNames) {
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
