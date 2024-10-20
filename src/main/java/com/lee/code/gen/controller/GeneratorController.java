package com.lee.code.gen.controller;

import cn.opensrcdevelop.auth.client.authorize.annoation.Authorize;
import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.core.entity.TemplateFileTree;
import com.lee.code.gen.core.entity.TemplateParameter;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.service.GeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Tag(name = "API-Generator", description = "接口-生成代码")
@RestController
@RequiredArgsConstructor
@RequestMapping("generator")
@RestResponse
public class GeneratorController {

    private static final String ATTACHMENT = "attachment; filename=\"{0}.zip\"";

    private final GeneratorService generatorService;

    @Operation(summary = "下载生成的代码（zip）", description = "下载生成的代码（zip）")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
            @Parameter(name = "ref", description = "分支、标签或提交的名称", in = ParameterIn.QUERY),
    })
    @GetMapping("/download/{id}")
    @Authorize({ "allGeneratorPermissions", "downloadGeneratedCode" })
    public void download(@PathVariable Integer id, @RequestParam(required = false, defaultValue = Constants.DEFAULT_REF_MAIN) String ref, HttpServletResponse response) {

        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(output)) {
            var projName = generatorService.downloadCode(id, ref, zip);
            byte[] data = output.toByteArray();

            response.reset();
            response.setHeader(Constants.HEADER_CONTENT_DISPOSITION, MessageFormat.format(ATTACHMENT, projName));
            response.addHeader(Constants.HEADER_CONTENT_LENGTH, Constants.EMPTY_STRING + data.length);
            response.setContentType(Constants.CONTENT_TYPE_DOWNLOAD);

            IOUtils.write(data, response.getOutputStream());
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Operation(summary = "获取可用的模板参数", description = "获取可用的模板参数")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
            @Parameter(name = "ref", description = "分支、标签或提交的名称", in = ParameterIn.QUERY),
    })
    @GetMapping("/parameters/{id}")
    @Authorize({ "allGeneratorPermissions", "getAvailableTemplateParameters" })
    public List<TemplateParameter> getAvailableParameters(@PathVariable Integer id, @RequestParam(required = false, defaultValue = Constants.DEFAULT_REF_MAIN) String ref) {
        return generatorService.getAvailableParameters(id, ref);
    }

    @Operation(summary = "预览生成的代码", description = "预览生成的代码")
    @Parameters({
            @Parameter(name = "id", description = "模板工程ID", in = ParameterIn.PATH),
            @Parameter(name = "ref", description = "分支、标签或提交的名称", in = ParameterIn.QUERY),
    })
    @GetMapping("/preview/{id}")
    @Authorize({ "allGeneratorPermissions", "previewGeneratedCode" })
    public List<TemplateFileTree> preview(@PathVariable Integer id, @RequestParam(required = false, defaultValue = Constants.DEFAULT_REF_MAIN) String ref) {
        return generatorService.preview(id, ref);
    }
}
