package cn.liumouren.boot.db.mysql.util;

import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;

/**
 * 好用的雪花算法 id 工具类
 * <p> note: 只针对 sharding-jdbc 雪花算法(默认 id 生成策略)实现, 其他雪花算法实现不可用
 *
 * @author freeman
 * @date 2021/12/9 11:02
 */
public final class SnowflakeUtil {

    private static final long EPOCH = SnowflakeKeyGenerateAlgorithm.EPOCH;

    /**
     * 根据Snowflake的ID，获取机器id
     *
     * @param id snowflake算法生成的id
     * @return 所属机器的id
     */
    public static long getWorkerId(long id) {
        return id >> 12 & ~(-1L << 10);
    }

    /**
     * 根据Snowflake的ID，获取生成时间
     *
     * @param id snowflake算法生成的id
     * @return 生成的时间
     */
    public static long getGenerateDateTime(long id) {
        return (id >> 22 & ~(-1L << 41L)) + EPOCH;
    }

}
