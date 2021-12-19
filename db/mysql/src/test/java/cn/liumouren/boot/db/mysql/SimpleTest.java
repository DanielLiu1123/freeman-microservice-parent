package cn.liumouren.boot.db.mysql;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.liumouren.boot.db.mysql.util.SnowflakeUtil;
import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author freeman
 * @date 2021/12/9 10:30
 */
public class SimpleTest {
//    @Test
    public void test_snowFlake(){
        SnowflakeKeyGenerateAlgorithm algorithm = new SnowflakeKeyGenerateAlgorithm();
        ReflectUtil.setFieldValue(algorithm, "workerId", 110);
        Comparable<?> key = algorithm.generateKey();
        System.out.println(key);

        System.out.println(DateUtil.date(SnowflakeUtil.getGenerateDateTime((Long) key)));
        System.out.println(SnowflakeUtil.getWorkerId((Long) key));
    }
}
