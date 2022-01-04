package cn.liumouren.boot.common.util;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * <p> 在我们想扫描自定义注解时会很有用
 *
 * @author freeman
 * @date 2022/1/4 18:23
 * @see org.springframework.cloud.openfeign.FeignClientsRegistrar#registerFeignClients(AnnotationMetadata, BeanDefinitionRegistry)
 */
public class BeanDefinitionScanner {
    private final Environment environment;
    private final ResourceLoader resourceLoader;

    public BeanDefinitionScanner(Environment environment,
                                 ResourceLoader resourceLoader) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
    }

    /**
     * 找到指定包下指定注解的 BeanDefinition
     * @param basePackage basePackage
     * @param annotationClasses 需要扫描的注解
     * @return BeanDefinitions
     */
    @SafeVarargs
    public final Set<BeanDefinition> findBeanDefinitions(String basePackage, Class<? extends Annotation>... annotationClasses) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        Arrays.stream(annotationClasses).forEach(clz -> scanner.addIncludeFilter(new AnnotationTypeFilter(clz)));
        return scanner.findCandidateComponents(basePackage);
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
        provider.setResourceLoader(this.resourceLoader);
        return provider;
    }


}
