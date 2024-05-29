package com.lee.code.gen.dto;

import com.lee.code.gen.core.entity.SystemProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Schema(description = "更新 property 请求")
@Data
public class UpdatePropertyRequestDto {

    @Schema(description = "属性集合")
    @Valid
    @Size(min = 1, max = 20)
    List<SystemProperty> properties;
}
