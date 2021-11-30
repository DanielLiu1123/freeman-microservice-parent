package cn.liumouren.boot.openfeign.component.feign;

import feign.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.convert.ConversionService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 扩展 {@link SpringMvcContract}, 实现 spring mvc 注解和 feign 注解的并用
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/26 10:58
 */
public class CompositeContract extends SpringMvcContract {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeContract.class);

    private final Contract defaultContract;

    private static final List<Class<? extends Annotation>> FEIGN_ANNOTATIONS = Arrays.asList(
            Body.class,
            HeaderMap.class,
            Headers.class,
            Param.class,
            QueryMap.class,
            RequestLine.class
    );

    public CompositeContract(List<AnnotatedParameterProcessor> parameterProcessors,
                             ConversionService feignConversionService,
                             boolean decodeSlash) {
        super(parameterProcessors, feignConversionService, decodeSlash);
        defaultContract = new Contract.Default();
    }

    @Override
    protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
        super.processAnnotationOnClass(data, clz);

        // TODO 有办法消除反射操作吗?
        try {
            Method method = BaseContract.class
                    .getDeclaredMethod("processAnnotationOnClass", MethodMetadata.class, Class.class);
            method.setAccessible(true);
            method.invoke(defaultContract, data, clz);
        } catch (Exception ignore) {
            LOGGER.warn("Default Contract解析类[{}]上注解出现异常", clz);
        }
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method) {
        if (isFeignAnno(annotation)) {
            // 防止方法上没有 非feign注解 情况报 NPE
            data.indexToExpander(new LinkedHashMap<>());
            try {
                Method med = BaseContract.class
                        .getDeclaredMethod("processAnnotationOnMethod", MethodMetadata.class, Annotation.class, Method.class);
                med.setAccessible(true);
                med.invoke(defaultContract, data, annotation, med);
            } catch (Exception ignore) {
                LOGGER.warn("Default Contract解析方法[{}]上注解[{}]出现异常", method.toString(), annotation.annotationType());
            }
        } else {
            super.processAnnotationOnMethod(data, annotation, method);
        }
    }

    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        // feign 注解
        List<Annotation> feignAnnos = Arrays.stream(annotations)
                .filter(anno -> FEIGN_ANNOTATIONS.contains(anno.annotationType()))
                .collect(Collectors.toList());

        // 非 feign 注解
        Annotation[] noneFeignAnnos = Arrays.stream(annotations)
                .filter(anno -> feignAnnos.stream().noneMatch(annotation -> annotation.equals(anno)))
                .toArray(Annotation[]::new);

        if (!feignAnnos.isEmpty()) {
            try {
                Method med = BaseContract.class
                        .getDeclaredMethod("processAnnotationsOnParameter", MethodMetadata.class, Annotation[].class, int.class);
                med.setAccessible(true);
                med.invoke(defaultContract, data, annotations, paramIndex);
            } catch (Exception ignore) {
                LOGGER.warn("Default Contract解析第[{}]个参数上注解[{}]出现异常",
                        paramIndex,
                        Arrays.stream(annotations)
                                .map(Annotation::annotationType)
                                .collect(Collectors.toList()));
            }
        }

        if (noneFeignAnnos.length > 0) {
            return super.processAnnotationsOnParameter(data, noneFeignAnnos, paramIndex);
        }

        // 不修改 feign 本身的处理逻辑
        return false;
    }

    private boolean isFeignAnno(Annotation annotation) {
        return FEIGN_ANNOTATIONS.contains(annotation.annotationType());
    }

}
