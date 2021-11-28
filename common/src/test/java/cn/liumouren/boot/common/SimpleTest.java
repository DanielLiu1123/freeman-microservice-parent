package cn.liumouren.boot.common;

import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
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


}
