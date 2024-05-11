package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取登录用户信息响应")
public class GetLoginUserInfoResponseDto {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像")
    private String avatarUrl;
}
