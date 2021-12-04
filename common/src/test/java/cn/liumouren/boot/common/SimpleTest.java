package cn.liumouren.boot.common;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.liumouren.boot.common.util.EncryptUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/24 10:04
 */
public class SimpleTest {
    @Test
    public void testURI() throws IOException {
        ClassPathResource resource = new ClassPathResource("defaultconfig/freeman-microservice.yml");
        System.out.println(resource.getFilename());
    }

    @Test
    public void testClass() throws IOException {
        System.out.println(ResolvableType.forClass(StringList.class).getSuperType().getGeneric(0).resolve());
        Class<?> aClass = ResolvableType.forClass(MyHashMap.class)
                .getSuperType()
                .resolveGeneric(1, 0, 0);
        System.out.println(aClass);

        SomeList<String> list = new SomeList<>();
        Class<?> clz = ResolvableType.forClass(list.getClass())
                .resolveGeneric(0);
        System.out.println(clz);
    }

    class StringList extends ArrayList<String> {
    }

    class MyHashMap extends HashMap<String, HashMap<List<String>, Integer>> {
    }

    class SomeList<T> extends ArrayList<String> {
    }

    @Test
    public void test_base64() throws IOException {
        AES aes = SecureUtil.aes("freeman-microservice-parent-1123".getBytes(StandardCharsets.UTF_8));

        String ss = aes.encryptHex("llw1123");
        System.out.println(ss);

        System.out.println(aes.decryptStr(ss));
    }

    @Test
    public void test_util() throws IOException {
        String encode = EncryptUtil.encode("llw1123");
        String decode = EncryptUtil.decode(encode);
        System.out.println(encode);
        System.out.println(decode);
    }


}
