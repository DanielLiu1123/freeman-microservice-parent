package cn.liumouren.boot.db.mysql.util;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 分布式 id 生成工具, 基于 hutool {@link Snowflake}
 *
 * @author freeman
 * @date 2021/12/9 02:50
 */
@Component
public final class IdUtil {

    private static Snowflake snowflake;

    @Autowired
    public void setSnowflake(Snowflake snowflake) {
        IdUtil.snowflake = snowflake;
    }

    public long getWorkerId(long id) {
        return snowflake.getWorkerId(id);
    }

    /**
     * 根据Snowflake的ID，获取数据中心id
     *
     * @param id snowflake算法生成的id
     * @return 所属数据中心
     */
    public long getDataCenterId(long id) {
        return snowflake.getDataCenterId(id);
    }

    /**
     * 根据Snowflake的ID，获取生成时间
     *
     * @param id snowflake算法生成的id
     * @return 生成的时间
     */
    public long getGenerateDateTime(long id) {
        return snowflake.getGenerateDateTime(id);
    }

    /**
     * 下一个ID
     *
     * @return ID
     */
    public long nextId() {
        return snowflake.nextId();
    }

    /**
     * 下一个ID（字符串形式）
     *
     * @return ID 字符串形式
     */
    public String nextIdStr() {
        return snowflake.nextIdStr();
    }

}
