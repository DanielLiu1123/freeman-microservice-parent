package cn.liumouren.boot.db.mysql;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author freeman
 * @date 2021/12/6 16:00
 */
@Data
@ConfigurationProperties(MysqlDynamicProperties.PREFIX)
public class MysqlDynamicProperties {
    public static final String PREFIX = "spring.datasource.dynamic";

    /**
     * 默认不启用动态数据源
     */
    private boolean enable = true;
    /**
     * 默认类型
     */
    private Class<? extends DataSource> defaultType = HikariDataSource.class;

    private List<DsConfig> datasources;


    @Data
    static class DsConfig {
        private Class<? extends DataSource> type;
        private String name;
        private String url;
        private String jdbcClassName = "com.mysql.cj.jdbc.Driver";
        private String username;
        private String password;
        /**
         * 额外配置, 根据 type 类型决定 eg. hikari (HikariConfig), druid (DruidAbstractDataSource)
         */
        private Map<String, Object> extra;
    }

//    @Data
//    static class HikariConfig {
//        private String catalog;
//        private long connectionTimeout = SECONDS.toMillis(30);
//
//        private long validationTimeout = SECONDS.toMillis(5);
//
//        private long idleTimeout = MINUTES.toMillis(10);
//
//        private long leakDetectionThreshold;
//        private long maxLifetime = MINUTES.toMillis(30);
//        private int maxPoolSize = -1;
//        private int minIdle = -1;
//        private String username;
//        private String password;
//
//        // Properties NOT changeable at runtime
//        private long initializationFailTimeout = 1;
//        private String connectionInitSql;
//        private String connectionTestQuery;
//        private String dataSourceClassName;
//        private String dataSourceJndiName;
//        private String driverClassName;
//        private String exceptionOverrideClassName;
//        private String url;
//        private String poolName;
//        private String schema;
//        private String transactionIsolationName;
//        private boolean isAutoCommit = true;
//        private boolean isReadOnly;
//        private boolean isIsolateInternalQueries;
//        private boolean isRegisterMbeans;
//        private boolean isAllowPoolSuspension;
//        private DataSource dataSource;
//        private Properties dataSourceProperties = new Properties();
//        private ThreadFactory threadFactory;
//        private ScheduledExecutorService scheduledExecutor;
//        private MetricsTrackerFactory metricsTrackerFactory;
//        private Object metricRegistry;
//        private Object healthCheckRegistry;
//        private Properties healthCheckProperties = new Properties();
//
//        private boolean sealed;
//    }
//
//    @Data
//    static class DruidConfig {
//        private String username;
//        private String password;
//        private String url;
//        private String driverClassName;
//        private ClassLoader driverClassLoader;
//        private Properties connectProperties = new Properties();
//
//        private PasswordCallback passwordCallback;
//        private NameCallback userCallback;
//
//        private int initialSize = 0;
//        private int maxActive = 8;
//        private int minIdle = 0;
//        private int maxIdle = 8;
//        private long maxWait = -1;
//        private int notFullTimeoutRetryCount = 0;
//
//        private String validationQuery = null;
//        private int validationQueryTimeout = -1;
//        private boolean testOnBorrow = false;
//        private boolean testOnReturn = false;
//        private boolean testWhileIdle = true;
//        private boolean poolPreparedStatements = false;
//        private boolean sharePreparedStatements = false;
//        private int maxPoolPreparedStatementPerConnectionSize = 10;
//
//        private boolean inited = false;
//        private boolean initExceptionThrow = true;
//
//        private PrintWriter logWriter = new PrintWriter(System.out);
//
//        private List<Filter> filters = new CopyOnWriteArrayList<>();
//        private boolean clearFiltersEnable = true;
//        private ExceptionSorter exceptionSorter = null;
//
//        private Driver driver;
//
//        private int queryTimeout;
//        private int transactionQueryTimeout;
//
//        private long createTimespan;
//
//        private int maxWaitThreadCount = -1;
//        private boolean accessToUnderlyingConnectionAllowed = true;
//
//        private long timeBetweenEvictionRunsMillis = 60 * 1000L;
//        private int numTestsPerEvictionRun = 3;
//        private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;
//        private long maxEvictableIdleTimeMillis = 1000L * 60L * 60L * 7;
//        private long keepAliveBetweenTimeMillis = 60 * 1000L * 2;
//        private long phyTimeoutMillis = -1;
//        private long phyMaxUseCount = -1;
//
//        private boolean removeAbandoned;
//        private long removeAbandonedTimeoutMillis = 300 * 1000;
//        private boolean logAbandoned;
//
//        private int maxOpenPreparedStatements = -1;
//
//        private List<String> connectionInitSqls;
//
//        private String dbTypeName;
//
//        private long timeBetweenConnectErrorMillis = 500;
//    }


}
