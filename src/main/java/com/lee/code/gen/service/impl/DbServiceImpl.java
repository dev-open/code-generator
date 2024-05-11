package com.lee.code.gen.service.impl;

import com.lee.code.gen.common.Constants;
import com.lee.code.gen.common.DbType;
import com.lee.code.gen.common.JavaType;
import com.lee.code.gen.core.entity.DataSourceConfig;
import com.lee.code.gen.core.entity.FieldInfo;
import com.lee.code.gen.core.entity.TableColumn;
import com.lee.code.gen.core.entity.TableInfo;
import com.lee.code.gen.dto.GetTableRequestDto;
import com.lee.code.gen.dto.GetTableEntityResponseDto;
import com.lee.code.gen.dto.GetTableResponseDto;
import com.lee.code.gen.exception.BizException;
import com.lee.code.gen.service.DbService;
import com.lee.code.gen.util.DbUtil;
import com.lee.code.gen.util.LocalCacheUtil;
import com.lee.code.gen.util.TemplateUtil;
import com.lee.code.gen.util.TypeUtil;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DbServiceImpl implements DbService {

    /**
     * 获取表实体信息
     *
     * @param tableName 表名
     * @param accessToken 访问令牌
     * @return 表实体信息
     */
    @Override
    public List<GetTableEntityResponseDto> queryTableWithColumns(String tableName, String accessToken) {
        List<GetTableEntityResponseDto> response = new ArrayList<>();
        // 1. 数据源配置
        DataSourceConfig dataSourceConfig = LocalCacheUtil.get(accessToken);
        if (dataSourceConfig == null) {
            throw new BizException(Constants.GET_TABLE_ENTITY_ACCESS_TOKEN_EXPIRED_MSG_CODE);
        }
        DbUtil dbUtil = new DbUtil(dataSourceConfig);

        // 2. 获取表信息
        List<TableInfo> tableInfos = dbUtil.getTables(tableName, new String[] { "TABLE", "VIEW" });
        tableInfos.forEach(tb -> {
            GetTableEntityResponseDto entity = new GetTableEntityResponseDto();
            // 2.1 设置实体信息
            String name = tb.getName();
            entity.setEntityName(StringUtils.capitalize(TemplateUtil.underLineToCamel(name)));
            entity.setTableName(name);
            entity.setComment(tb.getComment());

            // 2.2 设置包和实体字段信息
            var ret = queryColumns(name, dbUtil);
            entity.setFields(ret._2);
            entity.setImportList(ret._1);
            response.add(entity);
        });
        return response;
    }

    /**
     * 获取表信息
     *
     * @param requestDto 请求
     * @return 表信息
     */
    @Override
    public List<GetTableResponseDto> queryTable(GetTableRequestDto requestDto) {
        List<GetTableResponseDto> response = new ArrayList<>();
        // 1. 数据源配置
        DataSourceConfig dataSourceConfig = getDataSourceConfig(requestDto);
        DbUtil dbUtil = new DbUtil(dataSourceConfig);
        String accessToken = dataSourceConfig.getAccessToken();
        // 2. 获取表信息
        List<TableInfo> tableInfos = dbUtil.getTables(null, new String[] { "TABLE", "VIEW" });
        tableInfos.forEach(tb -> {
            GetTableResponseDto table = new GetTableResponseDto();
            table.setTableName(tb.getName());
            table.setComment(tb.getComment());
            table.setType(tb.getType());
            table.setAccessToken(accessToken);
            response.add(table);
        });
        return response;
    }

    /**
     * 获取数据源配置
     */
    private synchronized DataSourceConfig getDataSourceConfig(GetTableRequestDto requestDto) {
        DbType dbType = DbType.valueOf(requestDto.getDbType());
        String cacheKey = String.join(Constants.UNDERLINE, dbType.getType(), UUID.randomUUID().toString().replace("-", ""));
        return LocalCacheUtil.computeIfAbsent(cacheKey, TimeUnit.MINUTES.toSeconds(5), () -> {
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setHost(requestDto.getHost());
            dataSourceConfig.setPort(requestDto.getPort());
            dataSourceConfig.setDbName(requestDto.getDbName());
            dataSourceConfig.setUsername(requestDto.getUsername());
            dataSourceConfig.setPassword(requestDto.getPassword());
            dataSourceConfig.setSchema(requestDto.getSchema());
            dataSourceConfig.setCatalog(requestDto.getCatalog());
            dataSourceConfig.setDbType(dbType);
            dataSourceConfig.setAccessToken(cacheKey);
            return dataSourceConfig;
        }, x -> {
            DataSourceConfig ds  = (DataSourceConfig) x.getData();
            ds.shutdown();
            log.info("Close datasource {} for jdbc-url '{}' successfully", ds.getConnectionPoolName(), ds.getJdbcUrl());
        });
    }

    /**
     * 获取列和包的信息
     */
    private Tuple2<Set<String>, List<FieldInfo>> queryColumns(String tableName, DbUtil dbUtil) {
        List<FieldInfo> fields = new ArrayList<>();
        Set<String> importList = new TreeSet<>();

        List<TableColumn> tableColumns = dbUtil.getColumns(tableName, true);
        tableColumns.forEach(c -> {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setName(TemplateUtil.underLineToCamel(c.getColumnName()));
            fieldInfo.setColumnName(c.getColumnName());
            fieldInfo.setComment(c.getComment());
            fieldInfo.setIsPrimaryKey(c.isPrimaryKey());
            fieldInfo.setIsAutoIncrement(c.isAutoIncrement());

            // 获取 Java 类型
            JavaType javaType = TypeUtil.convert2JavaType(c);
            fieldInfo.setJavaType(javaType.getType());
            if (StringUtils.isNotEmpty(javaType.getPkg())) {
                importList.add(javaType.getPkg());
            }
            fields.add(fieldInfo);
        });

        return Tuple.of(importList, fields);
    }
}
