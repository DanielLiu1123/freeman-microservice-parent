package cn.liumouren.boot.common.util.componentscanner;

import cn.liumouren.boot.common.util.BeanDefinitionScanner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 *
 * @author freeman
 * @date 2022/1/4 18:46
 */
@SpringBootTest(
        classes = BeanDefinitionScannerTest.class,
        properties = {
                "spring.application.name=component-scanner-test-test"
        })
@Import(BeanDefinitionScanner.class)
public class BeanDefinitionScannerTest {

    @Autowired
    private BeanDefinitionScanner beanDefinitionScanner;

    @Test
    public void test_beanDefinitionScanner() {
        Set<BeanDefinition> definitions = beanDefinitionScanner.findBeanDefinitions(BeanDefinitionScannerTest.class.getPackageName(), Client.class);

        assertEquals(1, definitions.size());
        assertTrue(definitions.iterator().next() instanceof ScannedGenericBeanDefinition);
    }

    @Client
    static class SomeClient {

    }
}
