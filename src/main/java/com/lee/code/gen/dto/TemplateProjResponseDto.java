package com.lee.code.gen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lee.code.gen.common.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "获取模板工程响应")
public class TemplateProjResponseDto {

    /** 模板工程 ID */
    @Schema(description = "模板工程 ID")
    private Integer id;

    /** 模板工程名 */
    @Schema(description = "模板工程名")
    private String name;

    /** 模板工程描述 */
    @Schema(description = "模板工程描述")
    private String description;

    /** 模板工程作者 */
    @Schema(description = "模板工程作者")
    private String author;

    /** 模板工程版本 */
    @Schema(description = "模板工程版本")
    private String version;

    /** 模板工程 Web 地址 */
    @Schema(description = "模板工程 Web 地址")
    private String webUrl;

    /** 模板工程创建时间 */
    @Schema(description = "模板工程创建时间")
    @JsonFormat(pattern = Constants.LOCAL_DATETIME_FORMAT_WITHOUT_SSS)
    private LocalDateTime createdAt;
}
