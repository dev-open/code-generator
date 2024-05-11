package com.lee.code.gen.dto;

import com.lee.code.gen.validation.constraints.TargetPath;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新生成代码的目标路径请求")
public class UpdateTargetPathRequestDto {

    /** 工程 ID */
    @Schema(description = "工程 ID ")
    @NotNull
    private Integer projId;

    /** 模板路径 */
    @Schema(description = "模板路径")
    @NotEmpty
    private String templatePath;

    /** 生成代码的目标路径 */
    @Schema(description = "生成代码的目标路径")
    @NotBlank
    @Length(min = 1, max = 100)
    @TargetPath
    private String targetPath;
}
