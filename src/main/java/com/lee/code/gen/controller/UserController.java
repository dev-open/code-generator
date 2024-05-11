package com.lee.code.gen.controller;

import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.dto.GetLoginUserInfoResponseDto;
import com.lee.code.gen.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API-User", description = "API-用户")
@RestController
@RestResponse
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "获取登录用户的信息", description = "获取登录用户的信息")
    @GetMapping
    public GetLoginUserInfoResponseDto getLoginUserInfo() {
        return service.getLoginUserInfo();
    }
}
