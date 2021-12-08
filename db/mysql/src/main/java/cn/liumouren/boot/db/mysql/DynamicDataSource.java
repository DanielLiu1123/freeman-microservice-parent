package cn.liumouren.boot.db.mysql;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * 动态数据源
 *
 * @author freeman
 * @date 2021/12/6 11:45
 */
public class DynamicDataSource extends AbstractDataSource implements InitializingBean {

    /**
     * data source 和 表名的映射关系
     */
    private final Map<String, List<String>> dsTableMapping = new HashMap<>();


    @Override
    public Connection getConnection() throws SQLException {
        for (DataSource dataSource : DataSourceHolder.getDataSources().values()) {
            return dataSource.getConnection();
        }
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DataSource> map = DataSourceHolder.getDataSources();
        for (Map.Entry<String, DataSource> entry : map.entrySet()) {
            Connection connection = entry.getValue().getConnection();
            List<String> names = getTableNames(connection);
            dsTableMapping.put(entry.getKey(), names);
        }
    }

    private List<String> getTableNames(Connection connection) throws SQLException {
        String dbName = getDbName(connection);
        String sql = "select table_name from information_schema.tables where table_schema='" + dbName + "'";
        Statement statement = connection.createStatement();
        List<String> names = new ArrayList<>();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()) {
            names.add(result.getString("TABLE_NAME"));
        }
        return names;
    }

    private String getDbName(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select database() as db_name");
            resultSet.next();
            return resultSet.getString("db_name");
        } catch (SQLException e) {
            throw new RuntimeException("没有找到对应数据库名字");
        }
    }
}
