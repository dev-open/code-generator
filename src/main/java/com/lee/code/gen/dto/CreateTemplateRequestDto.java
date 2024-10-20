package com.lee.code.gen.dto;

import com.lee.code.gen.common.Constants;
import com.lee.code.gen.validation.constraints.TargetPath;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "创建模板请求")
public class CreateTemplateRequestDto {

    /** 生成代码的目标路径 */
    @Schema(description = "生成代码的目标路径")
    @NotBlank
    @TargetPath
    private String targetPath;

    /** 工程 ID */
    @Schema(description = "工程 ID")
    @NotNull
    private Integer projId;


    /** 模板名称 */
    @Schema(description = "模板名称")
    @NotBlank
    @Pattern(regexp = Constants.TEMPLATE_FILENAME_REGX)
    private String templateName;
}
