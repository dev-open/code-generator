package com.lee.code.gen.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页实体
 */
@Data
@Schema(description = "分页响应")
public class RPage<T> {

    /** 分页数据 */
    @Schema(description = "分页数据")
    private T pageData;

    /** 数据总量 */
    @Schema(description = "数据总量")
    private Integer total;

    /** 总页数 */
    @Schema(description = "总页数")
    private Integer pages;

    /** 当前页 */
    @Schema(description = "当前页")
    private Integer page;

    /** 上一页 */
    @Schema(description = "上一页")
    private Integer prevPage;

    /** 下一页 */
    @Schema(description = "下一页")
    private Integer nextPage;

    /** 每页项目数 */
    @Schema(description = "每页项目数")
    private Integer perPage;
}
