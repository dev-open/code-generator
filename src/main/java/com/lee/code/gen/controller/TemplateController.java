package com.lee.code.gen.controller;

import com.lee.code.gen.annoation.IPRateLimiter;
import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.common.RPage;
import com.lee.code.gen.core.entity.TemplateConfigItem;
import com.lee.code.gen.core.entity.TemplateInfo;
import com.lee.code.gen.core.entity.TemplateParameter;
import com.lee.code.gen.dto.*;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.service.TemplateService;
import com.lee.code.gen.util.TemplateUtil;
import com.lee.code.gen.validation.constraints.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "API-Template", description = "接口-模板管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("template")
@RestResponse
public class TemplateController {

    private final TemplateService templateService;

    @Operation(summary = "新建模板工程", description = "新建模板工程")
    @PostMapping("/projs")
    @PreAuthorize("@pms.hasAnyPermission('allTemplateProjsPermissions', 'createTemplateProj')")
    public void createTemplateProj(@RequestBody @Valid CreateTemplateProjRequestDto requestDto) {
        templateService.createTemplateProj(requestDto.getProjName(), TemplateUtil.getUsername(), requestDto.getDesc());
    }

    @Operation(summary = "上传模板", description = "上传模板")
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IPRateLimiter(1)
    @PreAuthorize("@pms.hasAnyPermission('allTemplatePermissions', 'uploadTemplate')")
    public void uploadTemplateFile(@Valid UploadTemplateRequestDto requestDto) {
        try {
            var templateFile = requestDto.getTemplateFile();
            String templateContent = IOUtils.toString(templateFile.getInputStream(), StandardCharsets.UTF_8);
            String parameterContent = Constants.EMPTY_STRING;

            if (requestDto.getParameterFile() != null) {
                var parameterFile = requestDto.getParameterFile();
                parameterContent = IOUtils.toString(parameterFile.getInputStream(), StandardCharsets.UTF_8);
                // Json 校验
                TemplateUtil.validJsonString(parameterContent, Constants.JSON_SCHEMA_TEMPLATE_PARAMETERS);
            }

            String path = templateFile.getOriginalFilename();
            templateService.createTemplate(path, requestDto.getProjId(), templateContent, requestDto.getTargetPath(), parameterContent);
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Operation(summary = "获取模板内容", description = "获取模板内容")
    @GetMapping
    @IPRateLimiter(rate = 6, interval = 10)
    @PreAuthorize("@pms.hasAnyPermission('allTemplatePermissions', 'getTemplateContent')")
    public TemplateInfo getTemplate(@Valid GetTemplateRequestDto requestDto) {
        return templateService.getTemplate(requestDto.getProjId(), requestDto.getTemplatePath(), requestDto.getRef());
    }

    @Operation(summary = "获取模板参数", description = "获取模板参数")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
            @Parameter(name = "ref", description = "分支、标签或提交的名称", in = ParameterIn.QUERY)
    })
    @GetMapping("/parameters/{id}")
    @IPRateLimiter(rate = 6, interval = 10)
    @PreAuthorize("@pms.hasAnyPermission('allTemplateParametersPermissions', 'listTemplateParameter')")
    public List<TemplateParameter> getParameters(@PathVariable Integer id, @RequestParam(required = false, defaultValue = Constants.DEFAULT_REF_MAIN) String ref) {
        return templateService.getParameters(id, ref);
    }

    @Operation(summary = "设置模板参数", description = "设置模板参数")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
            @Parameter(name = "ref", description = "分支、标签或提交的名称", in = ParameterIn.QUERY)
    })
    @PutMapping("/parameters/{id}")
    @PreAuthorize("@pms.hasAnyPermission('allTemplateParametersPermissions', 'setTemplateParameter')")
    public void setParameters(@PathVariable Integer id, @RequestParam(required = false, defaultValue = Constants.DEFAULT_REF_MAIN) String ref, @RequestBody @Valid SetTemplateParametersRequestDto requestDto) {
        templateService.setParameters(id, ref, requestDto.getParameters());
    }

    @Operation(summary = "获取模板工程", description = "获取模板工程")
    @Parameters({
            @Parameter(name = "search", description = "工程名检索关键字", in = ParameterIn.QUERY),
            @Parameter(name = "page", description = "页码", in = ParameterIn.QUERY),
            @Parameter(name = "perPage", description = "每页记录数", in = ParameterIn.QUERY),
    })
    @GetMapping("/projs")
    @IPRateLimiter(rate = 6, interval = 10)
    @PreAuthorize("@pms.hasAnyPermission('allTemplateProjsPermissions', 'listTemplateProj')")
    public RPage<List<TemplateProjResponseDto>> getTemplateProjs(@RequestParam(required = false) String search, @RequestParam(required = false) @Min(1) Integer page, @RequestParam(required = false) @Min(1) @Max(100) Integer perPage) {
        return templateService.getTemplateProjs(search, null, page, perPage, Constants.DEFAULT_REF_MAIN);
    }

    @Operation(summary = "获取模板配置信息", description = "获取模板配置信息")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID",  in = ParameterIn.PATH),
    })
    @GetMapping("/configs/{id}")
    @IPRateLimiter(rate = 6, interval = 10)
    @PreAuthorize("@pms.hasAnyPermission('allTemplateConfigsPermissions', 'listTemplateConfigs')")
    public List<TemplateConfigItem> getTemplateConfigs(@PathVariable Integer id) {
        return templateService.getTemplateConfigs(id, Constants.DEFAULT_REF_MAIN);
    }

    @Operation(summary = "更新模板内容", description = "更新模板内容")
    @PutMapping
    @IPRateLimiter(rate = 8, interval = 10)
    @PreAuthorize("@pms.hasAnyPermission('allTemplatePermissions', 'updateTemplateContent')")
    public void updateTemplateContent(@RequestBody @Valid UpdateTemplateContentRequestDto requestDto) {
        templateService.updateTemplateContent(requestDto.getProjId(), requestDto.getPath(), requestDto.getContent(), Constants.DEFAULT_REF_MAIN);
    }

    @Operation(summary = "删除模板", description = "删除模板")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID",  in = ParameterIn.PATH),
            @Parameter(name = "path", description = "模板路径", in = ParameterIn.QUERY),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasAnyPermission('allTemplatePermissions', 'deleteTemplate')")
    public void deleteTemplate(@PathVariable @NotNull Integer id, @RequestParam @NotBlank String path) {
        templateService.removeTemplate(id, path, Constants.DEFAULT_REF_MAIN);
    }

    @Operation(summary = "删除模板参数", description = "删除模板参数")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
            @Parameter(name = "name", description = "参数名", in = ParameterIn.QUERY),
    })
    @DeleteMapping("/parameters/{id}")
    @PreAuthorize("@pms.hasAnyPermission('allTemplateParametersPermissions', 'deleteTemplateParameter')")
    public void deleteParameter(@PathVariable @NotNull Integer id, @RequestParam @NotBlank String name) {
        templateService.removeParameter(id, name, Constants.DEFAULT_REF_MAIN);
    }

    @Operation(summary = "删除模板工程", description = "删除模板工程")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
    })
    @DeleteMapping("/projs/{id}")
    @PreAuthorize("@pms.hasAnyPermission('allTemplateProjsPermissions', 'deleteTemplateProj')")
    public void deleteProj(@PathVariable @NotNull Integer id) {
        templateService.removeTemplateProj(id);
    }

    @Operation(summary = "更新生成代码的目标路径", description = "更新生成代码的目标路径")
    @PutMapping("/configs/targetPath")
    @PreAuthorize("@pms.hasAnyPermission('allTemplateConfigsPermissions', 'updateTargetPath')")
    public void updateTargetPath(@RequestBody @Valid UpdateTargetPathRequestDto requestDto) {
        templateService.setTargetPath(requestDto.getProjId(), Constants.DEFAULT_REF_MAIN, requestDto.getTemplatePath(), requestDto.getTargetPath());
    }

    @Operation(summary = "导入模板参数 json 文件", description = "导入模板参数 json 文件")
    @PostMapping(value = "/parameters/{id}/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID",  in = ParameterIn.PATH),
    })
    @PreAuthorize("@pms.hasAnyPermission('allTemplateParametersPermissions', 'importTemplateParameter')")
    public void importParameter(@PathVariable Integer id, @NotNull @FileType(extensions = "json") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String parameterJson = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8);
                // Json 校验
                TemplateUtil.validJsonString(parameterJson, Constants.JSON_SCHEMA_TEMPLATE_PARAMETERS);
                templateService.importParameters(id, Constants.DEFAULT_REF_MAIN, parameterJson);
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

}
