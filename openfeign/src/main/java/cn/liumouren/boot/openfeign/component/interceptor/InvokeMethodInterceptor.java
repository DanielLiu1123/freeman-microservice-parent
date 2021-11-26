package cn.liumouren.boot.openfeign.component.interceptor;

import cn.liumouren.boot.common.exception.BizException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * <p> FeignClient 调用方法的拦截器
 * <p> 我们对 openfeign 的方法调用做一些定制处理
 *
 * <p> eg. 我们希望远程调用异常返回统一异常
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/22 16:05
 */
public class InvokeMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            // 被调用方正常抛出异常
            if (e instanceof BizException) {
                throw e;
            }
            // 其他未在意料之内的异常统一返回 OpenfeignCallException
//            throw new OpenfeignCallException(e.getMessage());
            throw e;
        }
    }
}
