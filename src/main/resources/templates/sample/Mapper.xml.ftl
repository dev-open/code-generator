<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${basePackage}.mapper.${entity.entityName}Mapper">
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${basePackage}.entity.${entity.entityName}">
<#list entity.fields as field>
<#if field.isPrimaryKey>
        <id column="${field.columnName}" property="${field.name}" />
    <#else>
        <result column="${field.columnName}" property="${field.name}" />
</#if>
</#list>
    </resultMap>
</mapper>