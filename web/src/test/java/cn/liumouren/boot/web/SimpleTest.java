package cn.liumouren.boot.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/21 20:32
 */
public class SimpleTest {

    @Test
    public void test_jdkProxy() {
        ProxyFactory proxyFactory = new ProxyFactory(new UserApi());
        Object proxy = proxyFactory.getProxy();

        Assertions.assertThat(proxy).isInstanceOf(Api.class);
        Assertions.assertThat(proxy).isNotInstanceOf(UserApi.class);
    }

    @Test
    public void test_cglibProxy() {
        ProxyFactory proxyFactory = new ProxyFactory(new Person());
        proxyFactory.setProxyTargetClass(true);
        Object proxy = proxyFactory.getProxy();

        Assertions.assertThat(proxy).isInstanceOf(Person.class);
    }

    interface Api {
        String get();

        String put();
    }

    class UserApi implements Api {

        @Override
        public String get() {
            return "user";
        }

        @Override
        public String put() {
            return "put";
        }

    }

    class Person {

    }

}
