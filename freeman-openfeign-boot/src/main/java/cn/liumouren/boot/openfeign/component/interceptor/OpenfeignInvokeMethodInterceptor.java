package cn.liumouren.boot.openfeign.component.interceptor;

import cn.liumouren.boot.common.FreemanOpenfeignCallException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * <p> FeignClient 调用方法的拦截器
 * <p> 我们对openfeign调用做一些定制处理
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/22 16:05
 */
public class OpenfeignInvokeMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws FreemanOpenfeignCallException{
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            throw new FreemanOpenfeignCallException(e.getMessage());
        }
    }
}
