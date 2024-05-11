package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "新建模板工程请求")
public class CreateTemplateProjRequestDto {

    /** 工程名 */
    @NotBlank
    @Length(min = 1, max = 50)
    @Schema(description = "工程名")
    private String projName;

    /** 描述 */
    @Schema(description = "描述")
    @Length(min = 1, max = 150)
    private String desc;
}
