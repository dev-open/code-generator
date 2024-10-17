package com.lee.code.gen.dto;

import com.lee.code.gen.common.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "设置模板参数请求")
public class TemplateParameterRequestDto {

    /** 参数名 */
    @Schema(description = "参数名")
    @NotBlank
    @Length(min = 1, max = 20)
    @Pattern(regexp = Constants.TEMPLATE_PARAM_REGX)
    private String name;

    /** 参数值 */
    @Schema(description = "参数值")
    private Object value;

    /** 默认值 */
    @Schema(description = "默认值")
    private Object defaultValue;

    /** 是否启用 */
    @Schema(description = "是否启用")
    @NotNull
    private Boolean enable;

    /** 描述 */
    @Schema(description = "描述")
    @NotBlank
    @Length(min = 1, max = 150)
    private String desc;
}
