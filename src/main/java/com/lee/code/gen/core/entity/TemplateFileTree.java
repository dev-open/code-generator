package com.lee.code.gen.core.entity;

import lombok.Data;

import java.util.List;

/**
 * 模板文件树
 */
@Data
public class TemplateFileTree {

    /** 节点 ID */
    private Integer id;

    /** 文件夹或文件名称 */
    private String name;

    /** 全路径 */
    private String path;

    /** 父节点 ID */
    private Integer parentId;

    /** 子节点集合 */
    private List<TemplateFileTree> children;

    /** 生成的代码 */
    private String code;
}
