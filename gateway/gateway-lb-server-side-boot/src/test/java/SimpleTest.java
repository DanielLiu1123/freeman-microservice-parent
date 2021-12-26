import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.WeakHashMap;

/**
 *
 *
 * @author freeman
 * @date 2021/12/26 15:52
 */
public class SimpleTest {
    @Test
    public void test_weakHashMap(){
        WeakHashMap<String, Long> map = new WeakHashMap<>();
        String id = UUID.randomUUID().toString();
        map.put(id, System.currentTimeMillis());
//        id = null; // help gc
        System.gc();
        System.out.println(map);
    }
}
