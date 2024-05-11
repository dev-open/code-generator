package com.lee.code.gen.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DbType {

    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&remarks=true&useInformationSchema=true"),
    SQL_SERVER("SQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%s;DatabaseName=%s"),
    POSTGRE_SQL("PostgreSQL", "org.postgresql.Driver", "jdbc:postgresql://%s:%s/%s");

    @Getter
    private final String type;
    @Getter
    private final String driverClassName;
    private final String jdbcUrl;

    public String getJdbcUrl(String host, Integer port, String database) {
        return String.format(jdbcUrl, host, port, database);
    }

    public static DbType valueOf(int type) {
        return switch (type) {
            case 1 -> SQL_SERVER;
            case 2 -> POSTGRE_SQL;
            default -> MYSQL;
        };
    }
}
