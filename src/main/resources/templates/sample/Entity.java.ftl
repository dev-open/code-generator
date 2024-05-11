package ${basePackage}.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
<#list entity.importList as i>
    import ${i!};
</#list>

/**
 * ${entity.comment!}
 */
@Data
<#if entity.tableName??>
@TableName("${entity.tableName}")
</#if>
public class ${entity.entityName} {
<#list entity.fields as field>
 <#-- 注释 -->
    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
    <#-- 主键 -->
    <#if field.isPrimaryKey>
        <#if field.isAutoIncrement>
    @TableId(value = "${field.columnName}", type = IdType.AUTO)
        <#else>
    TableId("${field.columnName}")
        </#if>
    </#if>
    private ${field.javaType} ${field.name};

</#list>
}