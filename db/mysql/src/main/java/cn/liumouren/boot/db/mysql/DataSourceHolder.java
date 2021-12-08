package cn.liumouren.boot.db.mysql;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author freeman
 * @date 2021/12/6 18:55
 */
public class DataSourceHolder {

    private static final Map<String, DataSource> DATA_SOURCES = new ConcurrentHashMap<>();

    public static Map<String, DataSource> getDataSources() {
        return DATA_SOURCES;
    }

    public static void add(String dataSourceName, DataSource dataSource) {
        if (DATA_SOURCES.containsKey(dataSourceName)) {
            throw new IllegalArgumentException("dataSourceName already exist!");
        }
        DATA_SOURCES.put(dataSourceName, dataSource);
    }

}
