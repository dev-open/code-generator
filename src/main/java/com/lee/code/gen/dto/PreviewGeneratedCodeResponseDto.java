package com.lee.code.gen.dto;

import com.lee.code.gen.core.entity.TemplateFileTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "预览生成代码响应")
public class PreviewGeneratedCodeResponseDto {

    /** 文件树 */
    @Schema(description = "文件树")
    private List<TemplateFileTree> trees;
}
