package cn.liumouren.boot.db.mysql;

import cn.liumouren.boot.db.mysql.util.SnowflakeUtil;
import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

/**
 *
 *
 * @author freeman
 * @date 2021/12/9 10:30
 */
public class SimpleTest {
    @Test
    public void test_snowFlake(){
        SnowflakeKeyGenerateAlgorithm algorithm = new SnowflakeKeyGenerateAlgorithm();
        ReflectionTestUtils.setField(algorithm, "workerId", 110);
        Comparable<?> key = algorithm.generateKey();
        System.out.println(key);

        System.out.println(new Date(SnowflakeUtil.getGenerateDateTime((Long) key)));
        System.out.println(SnowflakeUtil.getWorkerId((Long) key));
    }
}
