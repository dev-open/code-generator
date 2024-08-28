package com.lee.code.gen.controller;

import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.dto.UpdatePropertyRequestDto;
import com.lee.code.gen.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API-System", description = "接口-系统工具")
@RestController
@RestResponse
@RequestMapping("system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService service;

    @Operation(summary = "更新 property", description = "更新 property")
    @PutMapping("/property")
    @PreAuthorize("@pms.hasAnyPermission('allSystemUtilPermissions', 'updateProperty')")
    public void updateProperty(@RequestBody @Valid UpdatePropertyRequestDto requestDto) {
        service.updateProperty(requestDto.getProperties());
    }

    @Operation(summary = "清空所有的响应缓存", description = "清空所有的响应缓存")
    @GetMapping("/cache/clear")
    @PreAuthorize("@pms.hasAnyPermission('allSystemUtilPermissions', 'clearCaches')")
    public void clearResponseCache() {
        service.clearResponseCache();
    }
}
