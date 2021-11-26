package cn.liumouren.boot.openfeign.component;

import feign.*;
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
public class CombinationContract extends SpringMvcContract {

    private final DeclarativeContract defaultContract;

    private static final List<Class<? extends Annotation>> FEIGN_ANNOTATIONS = Arrays.asList(
            Body.class,
            HeaderMap.class,
            Headers.class,
            Param.class,
            QueryMap.class,
            RequestLine.class
    );

    public CombinationContract(List<AnnotatedParameterProcessor> parameterProcessors,
                               ConversionService feignConversionService,
                               boolean decodeSlash) {
        super(parameterProcessors, feignConversionService, decodeSlash);
        defaultContract = new Contract.Default();
    }

    @Override
    protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
        super.processAnnotationOnClass(data, clz);

        try {
            Method method = DeclarativeContract.class
                    .getDeclaredMethod("processAnnotationOnClass", MethodMetadata.class, Class.class);
            method.setAccessible(true);
            method.invoke(defaultContract, data, clz);
        } catch (Exception ignore) {

        }
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method) {
        if (isFeignAnno(annotation)) {
            // 防止方法上没有 非feign注解 情况报 NPE
            data.indexToExpander(new LinkedHashMap<>());
            try {
                Method med = DeclarativeContract.class
                        .getDeclaredMethod("processAnnotationOnMethod", MethodMetadata.class, Annotation.class, Method.class);
                med.setAccessible(true);
                med.invoke(defaultContract, data, annotation, med);
            } catch (Exception ignore) {

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
                Method med = DeclarativeContract.class
                        .getDeclaredMethod("processAnnotationsOnParameter", MethodMetadata.class, Annotation[].class, int.class);
                med.setAccessible(true);
                med.invoke(defaultContract, data, annotations, paramIndex);
            } catch (Exception ignore) {

            }
        }

        // 先处理非 feign 注解, 不然会出现 NPE
        if (noneFeignAnnos.length != 0) {
            return super.processAnnotationsOnParameter(data, noneFeignAnnos, paramIndex);
        }

        // 不修改 feign 本身的处理逻辑
        return false;
    }

    private boolean isFeignAnno(Annotation annotation) {
        return FEIGN_ANNOTATIONS.contains(annotation.annotationType());
    }

}
