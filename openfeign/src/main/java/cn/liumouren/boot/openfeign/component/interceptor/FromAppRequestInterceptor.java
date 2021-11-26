package cn.liumouren.boot.openfeign.component.interceptor;

import cn.liumouren.boot.common.constant.WebConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/22 13:55
 */
public class FromAppRequestInterceptor implements RequestInterceptor {

    private final String appName;

    public FromAppRequestInterceptor(String appName) {
        this.appName = appName;
    }

    @Override
    public void apply(RequestTemplate template) {
        if (!template.headers().containsKey(WebConst.Header.FROM_APP)) {
            template.header(WebConst.Header.FROM_APP, appName);
        }
    }
}
