package cn.liumouren.boot.web.component;


import cn.hutool.core.util.StrUtil;
import cn.liumouren.boot.common.anno.UserId;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *
 *
 * @author freeman
 * @date 2021/12/4 5:07 PM
 */
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 解析的 header, 按先后顺序
     */
    private static final String[] HEADERS = {
            UserId.HEADER
    };

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(UserId.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        for (String header : HEADERS) {
            String userId = webRequest.getHeader(header);
            if (StrUtil.isNotBlank(userId)) {
                // TODO 这里应该有解密操作
                return userId;
            }
        }

        // 说明没有从指定 header 里解析到 userId
        UserId anno = parameter.getParameterAnnotation(UserId.class);
        if (anno != null && anno.required()) {
            throw new IllegalArgumentException("需要 userId !");
        }

        return null;
    }
}
