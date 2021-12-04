package cn.liumouren.boot.openfeign.component.processor;

import cn.liumouren.boot.common.anno.UserId;
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/26 11:07
 */
public class UserIdParameterProcessor implements AnnotatedParameterProcessor {
    private static final Class<UserId> ANNOTATION = UserId.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        MethodMetadata data = context.getMethodMetadata();
        String varName = '{' + UserId.HEADER + '}';
        if (!containMapValues(data.template().headers(), varName)) {
            int i = context.getParameterIndex();
            final Collection<String> names =
                    data.indexToName().containsKey(i) ? data.indexToName().get(i) : new ArrayList<>();
            names.add(UserId.HEADER);
            data.indexToName().put(i, names);
            data.template().header(UserId.HEADER, varName);
        }
        return true;
    }

    private <K, V> boolean containMapValues(Map<K, Collection<V>> map, V search) {
        Collection<Collection<V>> values = map.values();
        for (Collection<V> entry : values) {
            if (entry.contains(search)) {
                return true;
            }
        }
        return false;
    }
}
