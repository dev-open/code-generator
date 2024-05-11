package com.lee.code.gen.core.entity;

import com.lee.code.gen.common.Constants;
import com.lee.code.gen.common.DbType;
import com.lee.code.gen.exception.BizException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class DataSourceConfig {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** host */
    private String host;

    /** 端口 */
    private Integer port;

    /** 数据库类型 */
    private DbType dbType;

    /** 数据库名 */
    private String dbName;

    /** 模式 */
    private String schema;

    /** catalog */
    private String catalog;

    /** 访问令牌 */
    private String accessToken;

    private HikariDataSource dataSource;

    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (Exception e) {
            throw new BizException(e, Constants.GET_DB_CONNECTION_ERROR_MSG_CODE);
        }
    }

    public String getJdbcUrl() {
        return dbType.getJdbcUrl(host, port, dbName);
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public String getConnectionPoolName() {
        if (dataSource != null) {
            return dataSource.getPoolName();
        }
        return null;
    }

    private synchronized DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig hikariConfig = new HikariConfig();
            String jdbcUrl = getJdbcUrl();
            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setDriverClassName(dbType.getDriverClassName());
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            if (StringUtils.isNotEmpty(schema)) {
                hikariConfig.setSchema(schema);
            }
            if (StringUtils.isNotEmpty(catalog)) {
                hikariConfig.setCatalog(catalog);
            }
            hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(10));
            hikariConfig.setMaximumPoolSize(5);
            dataSource = new HikariDataSource(hikariConfig);
            log.info("Create datasource {} for jdbc-url '{}' successfully", dataSource.getPoolName(), jdbcUrl);
        }
        return dataSource;
    }
}
