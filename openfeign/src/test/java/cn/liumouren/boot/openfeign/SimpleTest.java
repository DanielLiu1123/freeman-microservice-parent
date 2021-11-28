package cn.liumouren.boot.openfeign;

import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/22 21:13
 */
public class SimpleTest {

    @Test
    public void testClass() throws ClassNotFoundException {
        Class<?> aClass = Class.forName("cn.liumouren.boot.openfeign.primerbean.OpenfeignPrimerBeanBootTest$PrimerBean2Api");
        System.out.println(aClass);
    }
}
