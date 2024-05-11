package com.lee.code.gen.dto;

import com.lee.code.gen.validation.constraints.FileType;
import com.lee.code.gen.validation.constraints.TargetPath;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "上传模板请求")
public class UploadTemplateRequestDto {

    /** 生成代码的目标路径 */
    @Schema(description = "生成代码的目标路径")
    @NotBlank
    @Length(min = 1, max = 100)
    @TargetPath
    private String targetPath;

    /** 工程 ID */
    @Schema(description = "工程 ID")
    @NotNull
    private Integer projId;

    /** 模板文件 */
    @Schema(description = "模板文件")
    @NotNull
    @FileType(extensions = "ftl")
    private MultipartFile templateFile;

    /** 参数文件 */
    @Schema(description = "参数文件")
    @FileType(extensions = "json")
    private MultipartFile parameterFile;
}