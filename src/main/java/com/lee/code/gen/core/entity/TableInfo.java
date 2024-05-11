package com.lee.code.gen.core.entity;

import lombok.Data;

import java.util.List;

/**
 * 表信息
 */
@Data
public class TableInfo {

    /** 表名 */
    private String name;

    /** 类型：TABLE VIEW */
    private String type;

    /** 注释 */
    private String comment;

    public boolean isView() {
        return "VIEW".equals(type);
    }
}
