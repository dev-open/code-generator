package com.lee.code.gen.controller;

import cn.opensrcdevelop.auth.client.authorize.annoation.Authorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.code.gen.annoation.IPRateLimiter;
import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.core.entity.TemplateParameter;
import com.lee.code.gen.dto.GetTableRequestDto;
import com.lee.code.gen.dto.GetTableResponseDto;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.service.DbService;
import com.lee.code.gen.util.WebUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "API-ParameterUtil", description = "接口-参数工具")
@RestController
@RestResponse
@RequestMapping("parameter-util")
@RequiredArgsConstructor
public class ParameterUtilController {

    private static final String ENTITY_PARAMETER_NAME = "entity";
    private static final String ENTITY_PARAMETER_DESC = "模板参数-实体信息";
    private static final String ATTACHMENT = "attachment; filename=\"{0}.json\"";

    private final DbService dbService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "获取数据库表对应的实体信息", description = "获取数据库表对应的实体信息")
    @Parameters({
            @Parameter(name = "tableName", description = "表名", in = ParameterIn.QUERY),
            @Parameter(name = "accessToken", description = "访问令牌", in = ParameterIn.QUERY),
    })
    @GetMapping("/entity")
    @IPRateLimiter(rate = 6, interval = 10)
    @Authorize({ "allParameterUtilPermissions", "getTableEntity" })
    public List<String> getTableEntity(@RequestParam @NotBlank String tableName, @RequestParam @NotBlank String accessToken) {
        List<String> response = new ArrayList<>();
        dbService.queryTableWithColumns(tableName, accessToken).forEach(t ->
                Try.run(() -> response.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t))).getOrElseThrow(ServerException::new));
        return response;
    }

    @Operation(summary = "下载模板参数-数据库表对应的实体信息", description = "下载模板参数-数据库表对应的实体信息")
    @GetMapping("/entity/download")
    @IPRateLimiter(rate = 6, interval = 10)
    @Authorize({ "allParameterUtilPermissions", "downloadJsonTableEntity" })
    public void downloadEntityParameter(@RequestParam @NotBlank String tableName, @RequestParam @NotBlank String accessToken) {
        TemplateParameter parameter = new TemplateParameter();
        parameter.setEnable(true);
        parameter.setDefaultValue(dbService.queryTableWithColumns(tableName, accessToken).stream().findFirst().orElse(null));
        parameter.setName(ENTITY_PARAMETER_NAME);
        parameter.setDesc(ENTITY_PARAMETER_DESC);
        doDownloadParameterJsonFile(List.of(parameter));
    }

    @Operation(summary = "获取数据库表信息", description = "获取数据库表信息")
    @PostMapping("/tables")
    @IPRateLimiter(rate = 4, interval = 10)
    @Authorize({ "allParameterUtilPermissions", "getTables" })
    public List<GetTableResponseDto> getTables(@RequestBody GetTableRequestDto requestDto) {
        return dbService.queryTable(requestDto);
    }

    /**
     * 下载模板参数 Json 文件
     */
    private void doDownloadParameterJsonFile(List<TemplateParameter> parameter) {
        if (!ObjectUtils.isEmpty(parameter)) {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parameter);
                output.write(jsonData.getBytes(StandardCharsets.UTF_8));
                byte[] data = output.toByteArray();

                HttpServletResponse response = WebUtil.getResponse().orElseThrow();
                response.reset();
                response.setHeader(Constants.HEADER_CONTENT_DISPOSITION, MessageFormat.format(ATTACHMENT, parameter.get(0).getName() + Constants.UNDERLINE + System.currentTimeMillis()));
                response.addHeader(Constants.HEADER_CONTENT_LENGTH, Constants.EMPTY_STRING + data.length);
                response.setContentType(Constants.CONTENT_TYPE_DOWNLOAD);

                IOUtils.write(data, response.getOutputStream());
            } catch (Exception e) {
                throw new ServerException(e);
            }
        }
    }
}
