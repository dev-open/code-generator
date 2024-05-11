package com.lee.code.gen.service.impl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.code.gen.client.GitlabClient;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.core.entity.*;
import com.lee.code.gen.exception.BizException;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.service.GeneratorService;
import com.lee.code.gen.service.TemplateService;
import com.lee.code.gen.util.TemplateUtil;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {

    private final TemplateService templateService;
    private final GitlabClient gitlabClient;
    private final ObjectMapper objectMapper;
    private final Executor executor = TtlExecutors.getTtlExecutor(new ForkJoinPool(8));

    /**
     * 下载生成的代码
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @param zip 输出
     * @return 项目名
     */
    @Override
    public String downloadCode(Integer projId, String ref, ZipOutputStream zip) {

        try {
            // 1. 获取配置
            var config = getTemplateConfig(projId, ref);

            // 2. 获取全部模板并填充
            List<TemplateConfigItem> templates = config.getTemplates();
            Map<String, Object> parameters = convertParameters(config.getParameters());
            List<CompletableFuture<TemplateInfo>> tasks = templates.stream()
                    .map(item ->
                            // 2.1 获取模板内容
                            CompletableFuture.supplyAsync(() -> templateService.getTemplate(projId, item.getTemplatePath(), ref), executor)).toList();
            tasks.stream().map(CompletableFuture::join)
                    .forEach(t -> {
                        // 2.2 填充模板
                        String content = TemplateUtil.fillTemplate(t.getTemplateContent(), parameters);
                        String path = TemplateUtil.fillTemplate(t.getTargetPath(), parameters);
                        // 2.3 输出到压缩文件
                        addToZip(zip, Path.of(config.getProjName(), path).toString(), content);
                    });
            return config.getProjName();
        } catch (Exception e) {
            throw new BizException(e, Constants.DOWNLOAD_TEMPLATE_FAIL_MSG_CODE, projId);
        }

    }

    /**
     * 获取可用的模板参数
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @return 可用的模板参数
     */
    @Override
    public List<TemplateParameter> getAvailableParameters(Integer projId, String ref) {
        // 1. 获取配置文件
        var config = getTemplateConfig(projId, ref);

        // 2. 编辑模板参数
        List<TemplateParameter> parameters = config.getParameters();
        return parameters.stream()
                // 2.1 排除未启用的模板参数
                .filter(TemplateParameter::getEnable).peek(p -> {
                    // 2.2 存在默认值，将默认值赋值给参数值
                    if (p.getDefaultValue() != null) {
                        p.setValue(p.getDefaultValue());
                    } else {
                        p.setValue(null);
                    }
                }).toList();
    }

    /**
     * 预览生成的代码
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @return 文件树
     */
    @Override
    public List<TemplateFileTree> preview(Integer projId, String ref) {
        // 1. 获取配置文件
        var config = getTemplateConfig(projId, ref);

        // 2.1 生成代码
        List<TemplateConfigItem> templates = config.getTemplates();
        Map<String, Object> parameters = convertParameters(config.getParameters());
        var tasks =  templates.stream().map(t -> CompletableFuture.supplyAsync(() -> {
            TemplateInfo info = new TemplateInfo();
            // 2.1.1 获取模板内容
            var template = templateService.getTemplate(projId, t.getTemplatePath(), ref);
            // 2.1.2 填充模板
            String content = TemplateUtil.fillTemplate(template.getTemplateContent(), parameters);
            String path = TemplateUtil.fillTemplate(template.getTargetPath(), parameters);
            // 2.1.3 属性设置
            info.setTemplateContent(content);
            info.setTargetPath(path);
            return info;
        }, executor).exceptionally(err -> null)).toList();
        var previewRet = tasks.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toMap(TemplateInfo::getTargetPath, TemplateInfo::getTemplateContent));
        // 预览结果小于模板总数，异常结束
        if (previewRet.size() < templates.size()) {
            throw new BizException(Constants.PREVIEW_GENERATED_CODE_FAIL_MSG_CODE);
        }
        // 2.2 获取文件树
        var paths = new ArrayList<>(previewRet.keySet());
        List<TemplateFileTree> trees = TemplateUtil.getTemplateFileTree(paths);
        // 2.3 设置生成的代码
        previewRet.forEach((k, v) -> trees.forEach(x -> setCode(x, k, v)));
        return trees;
    }

    /**
     * 添加到压缩文件
     */
    private void addToZip(ZipOutputStream zip, String path, String content) {
        try {
            zip.putNextEntry(new ZipEntry(path));
            zip.write(content.getBytes(StandardCharsets.UTF_8));
            zip.flush();
            zip.closeEntry();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    /**
     * 转换模板参数
     */
    private Map<String, Object> convertParameters(List<TemplateParameter> parameters) {
        return parameters.stream()
                // 过滤非启用的参数
                .filter(TemplateParameter::getEnable)
                // Map 的 value 可能为 null
                .collect(HashMap::new, (map, p) -> {
                    Object val = null;
                    if (p.getValue() != null) {
                        val =  Try.of(() -> objectMapper.readValue((String) p.getValue(), Class.forName(p.getValueClass()))).getOrElseThrow(ServerException::new);
                        map.put(p.getName(), val);
                    }
                }, HashMap::putAll);
    }

    /**
     * 获取模板配置
     */
    private TemplateConfig getTemplateConfig(Integer projId, String ref) {
        try {
            var configFile = gitlabClient.getFile(projId, Constants.CONFIG_FILE_PATH, ref);
            return objectMapper.readValue(TemplateUtil.decodeFileContent(configFile), TemplateConfig.class);
        } catch (JsonProcessingException e) {
            throw new ServerException(e);
        }
    }

    /**
     * 设置生成的代码
     */
    private void setCode(TemplateFileTree tree, String path, String code) {
        if (path.equals(tree.getPath())) {
            tree.setCode(code);
        } else {
            tree.getChildren().forEach(x -> setCode(x, path, code));
        }
    }
}
