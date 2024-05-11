package com.lee.code.gen.service.impl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.lee.code.gen.client.FeignResponse;
import com.lee.code.gen.client.GitlabClient;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.common.RPage;
import com.lee.code.gen.core.entity.TemplateConfig;
import com.lee.code.gen.core.entity.TemplateConfigItem;
import com.lee.code.gen.core.entity.TemplateInfo;
import com.lee.code.gen.core.entity.TemplateParameter;
import com.lee.code.gen.dto.*;
import com.lee.code.gen.dto.gitlab.*;
import com.lee.code.gen.exception.BizException;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.service.TemplateService;
import com.lee.code.gen.util.TemplateUtil;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final GitlabClient gitlabClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, Object> localCache;
    private final CaffeineCacheManager cacheManager;
    private final Executor executor = TtlExecutors.getTtlExecutor(new ForkJoinPool(8));

    private static final String COMMIT_MSG_1 = "{0}创建了模板工程{1}";
    private static final String COMMIT_MSG_2 = "{0}创建了模板{1}";
    private static final String COMMIT_MSG_3 = "{0}更新了模板参数";
    private static final String COMMIT_MSG_4 = "{0}创建模板{1}失败";
    private static final String COMMIT_MSG_5 = "{0}更新了模板{1}的内容";
    private static final String COMMIT_MSG_6 = "{0}删除了模板{1}";
    private static final String COMMIT_MSG_7 = "{0}删除了模板参数{1}";
    private static final String COMMIT_MSG_8 = "{0}更新了模板{1}的生成代码的目标路径";
    private static final String TEMPLATE_CONFIG_CACHE_NAME = "templateConfigCache";
    private static final String TEMPLATE_PARAMETER_CACHE_NAME = "templateParameterCache";
    private static final String TEMPLATE_PROJ_CACHE_NAME = "templateProjCache";

    /**
     * 创建模板工程
     *
     * @param projName 工程名
     * @param author 作者
     * @param desc 描述
     */
    @Override
    @CacheEvict(cacheNames = TEMPLATE_PROJ_CACHE_NAME, allEntries = true)
    public void createTemplateProj(String projName, String author, String desc) {
        // 1. 创建工程
        var createProjRequestDto = new GitlabCreateProjRequestDto();
        createProjRequestDto.setName(projName);
        createProjRequestDto.setNamespaceId((Integer) localCache.getIfPresent(Constants.CACHED_GROUP_ID));
        createProjRequestDto.setDescription(desc);
        var createProjResponseDto = gitlabClient.createProj(createProjRequestDto);

        // 2. 创建配置文件
        try {
            TemplateConfig templateConfig = new TemplateConfig();
            templateConfig.setProjName(projName);
            templateConfig.setVersion(Constants.DEFAULT_VERSION);
            templateConfig.setAuthor(author);
            templateConfig.setTemplates(new ArrayList<>());
            templateConfig.setParameters(new ArrayList<>());

            var createNewFileRequestDto = new GitlabCreateNewFileRequestDto();
            createNewFileRequestDto.setCommitMessage(MessageFormat.format(COMMIT_MSG_1, TemplateUtil.getUsername(), projName));
            createNewFileRequestDto.setContent(getConfigJson(templateConfig));
            gitlabClient.createNewFile(createNewFileRequestDto, createProjResponseDto.getId(), Constants.CONFIG_FILE_PATH);
        } catch (Exception e) {
            // 删除已创建的工程
            var deleteProjRequestDto = new GitlabDeleteProjRequestDto();
            gitlabClient.deleteProj(deleteProjRequestDto, createProjResponseDto.getId());

            throw new BizException(e, Constants.CREATE_TEMPLATE_PROJ_FAIL_MSG_CODE);
        }
    }

    /**
     * 创建模板
     *
     * @param path 路径
     * @param projId 工程 ID
     * @param content 模板内容
     * @param targetPath 生成的文件的目标路径
     */
    @Override
    public void createTemplate(String path, Integer projId, String content, String targetPath, String parameter) {

        // 1. 创建模板文件
        var createTemplateRequest = new GitlabCreateNewFileRequestDto();
        String templateName = getTemplateName(path);
        String commitMsg = MessageFormat.format(COMMIT_MSG_2, TemplateUtil.getUsername(), templateName);
        createTemplateRequest.setCommitMessage(commitMsg);
        createTemplateRequest.setContent(content);
        var createTemplateResponse = gitlabClient.createNewFile(createTemplateRequest, projId, path);

        // 2. 读取并修改配置文件
        try {
            var templateConfig = getTemplateConfig(projId, createTemplateResponse.getBranch());

            // 2.1 追加模板配置
            TemplateConfigItem configItem = new TemplateConfigItem();
            configItem.setTemplateName(getTemplateName(createTemplateResponse.getFilePath()));
            configItem.setTemplatePath(createTemplateResponse.getFilePath());
            configItem.setTargetPath(targetPath);
            configItem.setEnable(Boolean.TRUE);
            templateConfig.getTemplates().add(configItem);

            if (StringUtils.hasText(parameter)) {
                // 2.2 追加参数
                List<TemplateParameter> templateParameters = objectMapper.readValue(parameter, new TypeReference<>() {});
                if (!CollectionUtils.isEmpty(templateParameters)) {
                    var parameters = templateConfig.getParameters();
                    parameters.addAll(templateParameters);

                    // 2.2.1 去除重复参数
                    List<TemplateParameter> filteredParameters = parameters.stream().collect(Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(TemplateParameter::getName))),
                            ArrayList::new
                    ));

                    // 2.2.2 将 value 和 defaultValue 转换为 Json 字符串、记录 Java 类型
                    for (TemplateParameter p : filteredParameters) {
                        p.setValueClass(getJavaType(p.getValue()));
                        p.setDefaultValueClass(getJavaType(p.getDefaultValue()));

                        p.setValue(toJsonString(p.getValue()));
                        p.setDefaultValue(toJsonString(p.getDefaultValue()));
                    }
                    templateConfig.setParameters(filteredParameters);
                }

            }

            // 2.2.3 递增版本号
            templateConfig.setVersion(TemplateUtil.incrementVersion(templateConfig.getVersion()));

            // 2.3 更新配置文件
            updateTemplateConfig(templateConfig, projId, createTemplateResponse.getBranch(), commitMsg);

            // 3. 更新模板配置、参数缓存
            // 3.1 创建缓存
            createCacheIfAbsent(TEMPLATE_CONFIG_CACHE_NAME, TEMPLATE_PARAMETER_CACHE_NAME);
            // 3.2 更新缓存
            String keyName = getCacheKey(projId.toString(), createTemplateResponse.getBranch());
            evictAndPutCache(TEMPLATE_CONFIG_CACHE_NAME, keyName, templateConfig.getTemplates());
            evictAndPutCache(TEMPLATE_PARAMETER_CACHE_NAME, keyName, templateConfig.getParameters());
            // 4. 清空模板工程缓存
            Optional.ofNullable(cacheManager.getCache(TEMPLATE_PROJ_CACHE_NAME)).ifPresent(org.springframework.cache.Cache::clear);
        } catch (Exception e) {
            // 删除已创建的模板
            GitlabDeleteFileRequestDto deleteTemplateRequest = new GitlabDeleteFileRequestDto();
            deleteTemplateRequest.setBranch(createTemplateResponse.getBranch());
            deleteTemplateRequest.setCommitMessage(MessageFormat.format(COMMIT_MSG_4, TemplateUtil.getUsername(), getTemplateName(path)));
            gitlabClient.deleteFile(deleteTemplateRequest, projId, path);

            throw new BizException(e, Constants.CREATE_TEMPLATE_FAIL_MSG_CODE, path);
        }
    }

    /**
     * 获取模板
     *
     * @param projId 项目 ID
     * @param templatePath 模板路径
     * @param ref 分支、标签或提交的名称
     * @return 模板信息
     */
    @Override
    public TemplateInfo getTemplate(Integer projId, String templatePath, String ref) {
        TemplateInfo templateInfo = new TemplateInfo();

        // 1. 获取模板内容
        var templateFile = gitlabClient.getFile(projId, templatePath, ref);
        var configFile = gitlabClient.getFile(projId, Constants.CONFIG_FILE_PATH, ref);

        try {
            TemplateConfig templateConfig = objectMapper.readValue(TemplateUtil.decodeFileContent(configFile), TemplateConfig.class);
            TemplateConfigItem targetTemplate = templateConfig.getTemplates()
                    .stream().filter(t -> templatePath.equals(t.getTemplatePath())).findFirst().orElse(new TemplateConfigItem());

            // 2. 设置模板信息
            templateInfo.setTargetPath(targetTemplate.getTargetPath());
            templateInfo.setTemplateContent(TemplateUtil.decodeFileContent(templateFile));

            return templateInfo;
        } catch (Exception e) {
            throw new BizException(e, Constants.GET_TEMPLATE_INFO_FAIL_MSG_CODE, templatePath);
        }
    }

    /**
     * 获取工程下全部的模板配置信息
     *
     * @param projId 工程
     * @param ref 分支、标签或提交的名称
     * @return 模板配置信息
     */
    @Override
    @Cacheable(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_CONFIG_CACHE_NAME)
    public List<TemplateConfigItem> getTemplateConfigs(Integer projId, String ref) {
        // 1. 获取配置文件
        var templateConfig = getTemplateConfig(projId, ref);
        // 2. 获取全部的模板配置信息
        return templateConfig.getTemplates();
    }

    /**
     * 获取参数
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @return 参数
     */
    @Override
    @Cacheable(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_PARAMETER_CACHE_NAME)
    public List<TemplateParameter> getParameters(Integer projId, String ref) {
        try {
            // 1. 获取配置文件
            var configFile = gitlabClient.getFile(projId, Constants.CONFIG_FILE_PATH, ref);
            var templateConfig = objectMapper.readValue(TemplateUtil.decodeFileContent(configFile), TemplateConfig.class);
            // 2. 获取参数
            return templateConfig.getParameters();
        } catch (Exception e) {
            throw new BizException(e, Constants.GET_TEMPLATE_PARAMETER_FAIL_MSG_CODE);
        }
    }

    /**
     * 设置参数
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @param requestParameters 参数
     */
    @Override
    @CacheEvict(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_PARAMETER_CACHE_NAME)
    public void setParameters(Integer projId, String ref, List<TemplateParameterRequestDto> requestParameters) {
        try {
            // 1. 获取配置文件
            TemplateConfig templateConfig = getTemplateConfig(projId, ref);
            List<TemplateParameter> parameters = templateConfig.getParameters();

            // 2. 设置模板参数
            Map<String, TemplateParameterRequestDto> tmpParams  = requestParameters.stream()
                    .collect(Collectors.toMap(TemplateParameterRequestDto::getName, v -> v));
            parameters.forEach(p -> {
                var pName = p.getName();
                var param = tmpParams.get(pName);
                // 2.1 更新已存在的模板参数
                if (param != null) {
                    editParam(p, param);
                    tmpParams.remove(pName);
                }
            });

            // 2.2 追加不存在的模板参数
            tmpParams.forEach((key, value) -> {
                TemplateParameter parameter = new TemplateParameter();
                editParam(parameter, value);
                parameters.add(parameter);
            });

            // 2.3 递增版本号
            if (!ObjectUtils.isEmpty(tmpParams)) {
                templateConfig.setVersion(TemplateUtil.incrementVersion(templateConfig.getVersion()));
            }

            // 3. 更新配置文件
            updateTemplateConfig(templateConfig, projId, ref, MessageFormat.format(COMMIT_MSG_3, TemplateUtil.getUsername()));

            // 4. 清空模板工程缓存
            Optional.ofNullable(cacheManager.getCache(TEMPLATE_PROJ_CACHE_NAME)).ifPresent(org.springframework.cache.Cache::clear);
        } catch (Exception e) {
            throw new BizException(e, Constants.SET_TEMPLATE_PARAMETERS_FAIL_MSG_CODE, projId);
        }
    }

    /**
     * 获取模板工程
     *
     * @param search 关键字
     * @param orderBy 排序字段（默认为 created_at）
     * @param page 页码（默认：1）
     * @param perPage 每页记录数（默认：20，最大：100）
     * @param ref 分支、标签或提交的名称
     * @return 模板工程
     */
    @Override
    @Cacheable(key = "#search + '_' + #orderBy + '_' + #page + '_' + #perPage + '_' + #ref", cacheNames = TEMPLATE_PROJ_CACHE_NAME)
    public RPage<List<TemplateProjResponseDto>> getTemplateProjs(String search, String orderBy, Integer page, Integer perPage, String ref) {
        RPage<List<TemplateProjResponseDto>> response = new RPage<>();
        // 1. 列举全部工程
        FeignResponse<List<GitlabProjResponseDto>> feignRep = gitlabClient.listGroupProjs((Integer) localCache.getIfPresent(Constants.CACHED_GROUP_ID), search, orderBy, page, perPage);

        // 2. 获取模板作者、版本信息
        List<CompletableFuture<TemplateProjResponseDto>> tasks = feignRep.getBody().stream().map(p -> CompletableFuture.supplyAsync(() -> {
            TemplateProjResponseDto rep = new TemplateProjResponseDto();
            rep.setName(p.getName());
            rep.setId(p.getId());
            rep.setDescription(p.getDescription());
            // UTC → local
            rep.setCreatedAt(TemplateUtil.convertUTC2Local(p.getCreatedAt()));
            rep.setWebUrl(p.getWebUrl());

            // 2.1 获取配置文件
            var configFile = gitlabClient.getFile(p.getId(), Constants.CONFIG_FILE_PATH, ref);
            var templateConfig = Try.of(() -> objectMapper.readValue(TemplateUtil.decodeFileContent(configFile), TemplateConfig.class))
                    .getOrElse(new TemplateConfig());
            rep.setAuthor(templateConfig.getAuthor());
            rep.setVersion(templateConfig.getVersion());

            return rep;
        }, executor)).toList();

        // 2.2 等待全部任务结束
        response.setPageData(tasks.stream().map(CompletableFuture::join).toList());
        TemplateUtil.setPageData(feignRep, response);
        return response;
    }

    /**
     * 更新模板内容
     *
     * @param projId 项目 ID
     * @param path 模板路径
     * @param content 模板内容
     * @param ref 分支、标签或提交的名称
     */
    @Override
    public void updateTemplateContent(Integer projId, String path, String content, String ref) {
        // 1. 更新模板文件
        var newTemplateRequest = new GitlabUpdateFileRequestDto();
        newTemplateRequest.setContent(content);
        newTemplateRequest.setBranch(ref);
        newTemplateRequest.setCommitMessage(MessageFormat.format(COMMIT_MSG_5, TemplateUtil.getUsername(), path));

        gitlabClient.updateFile(newTemplateRequest, projId, path);
    }

    /**
     * 移除存在的模板
     *
     * @param projId 项目 ID
     * @param templatePath 模板路径
     * @param ref 分支、标签或提交的名称
     */
    @Override
    @CacheEvict(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_CONFIG_CACHE_NAME)
    public void removeTemplate(Integer projId, String templatePath, String ref) {
        try {
            // 1. 读取配置文件
            TemplateConfig templateConfig = getTemplateConfig(projId, ref);
            var templates =  templateConfig.getTemplates();
            var ret = templates.stream().filter(t -> t.getTemplatePath().equals(templatePath)).findFirst().orElse(null);

            // 2. 移除存在的模板
            if (ret == null) {
                // 2.1 模板不存在
                throw new BizException(Constants.TEMPLATE_NOT_EXISTS_MSG_CODE, templatePath);
            }
            // 2.2 模板存在
            templates.remove(ret);

            // 2.3 递增版本号
            templateConfig.setVersion(TemplateUtil.incrementVersion(templateConfig.getVersion()));

            // 3. 更新配置文件
            String commitMsg = MessageFormat.format(COMMIT_MSG_6, TemplateUtil.getUsername(), templatePath);
            updateTemplateConfig(templateConfig, projId, ref, commitMsg);

            // 4. 删除模板文件
            GitlabDeleteFileRequestDto deleteTemplateRequest = new GitlabDeleteFileRequestDto();
            deleteTemplateRequest.setCommitMessage(commitMsg);
            gitlabClient.deleteFile(deleteTemplateRequest, projId, templatePath);

            // 5. 清空模板工程缓存
            Optional.ofNullable(cacheManager.getCache(TEMPLATE_PROJ_CACHE_NAME)).ifPresent(org.springframework.cache.Cache::clear);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(e, Constants.REMOVE_TEMPLATE_FAIL_MSG_CODE, templatePath);
        }
    }

    /**
     * 移除存在的模板参数
     *
     * @param projId 项目 ID
     * @param parameterName 参数名
     * @param ref 分支、标签或提交的名称
     */
    @Override
    @CacheEvict(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_PARAMETER_CACHE_NAME)
    public void removeParameter(Integer projId, String parameterName, String ref) {
        try {
            // 1. 读取配置文件
            TemplateConfig templateConfig = getTemplateConfig(projId, ref);
            var parameters =  templateConfig.getParameters();
            var ret = parameters.stream().filter(t -> t.getName().equals(parameterName)).findAny().orElse(null);

            // 2. 移除存在的模板参数
            if (ret == null) {
                // 2.1 模板参数不存在
                throw new BizException(Constants.TEMPLATE_PARAMETER_NOT_EXISTS_MSG_CODE, parameterName);
            }
            // 2.2 模板参数存在
            parameters.remove(ret);
            // 2.3 递增版本号
            templateConfig.setVersion(TemplateUtil.incrementVersion(templateConfig.getVersion()));
            // 3. 更新配置文件
            updateTemplateConfig(templateConfig, projId, ref, MessageFormat.format(COMMIT_MSG_7, TemplateUtil.getUsername(), parameterName));
            // 4. 清空模板工程缓存
            Optional.ofNullable(cacheManager.getCache(TEMPLATE_PROJ_CACHE_NAME)).ifPresent(org.springframework.cache.Cache::clear);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(e, Constants.REMOVE_TEMPLATE_PARAMETER_FAIL_MSG_CODE, parameterName);
        }
    }

    /**
     * 移除模板工程
     *
     * @param projId 项目 ID
     */
    @Override
    @CacheEvict(key = "#projId + '_main'", cacheNames = { TEMPLATE_CONFIG_CACHE_NAME, TEMPLATE_PARAMETER_CACHE_NAME })
    public void removeTemplateProj(Integer projId) {
        try {
            gitlabClient.deleteProj(new GitlabDeleteProjRequestDto(), projId);

            // 清空模板工程缓存
            Optional.ofNullable(cacheManager.getCache(TEMPLATE_PROJ_CACHE_NAME)).ifPresent(org.springframework.cache.Cache::clear);
        } catch (Exception e) {
            throw new BizException(e, Constants.REMOVE_TEMPLATE_PROJ_FAIL_MSG_CODE, projId);
        }
    }

    /**
     * 设置更新生成代码的目标路径
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @param templatePath 模板路径
     * @param targetPath 生成代码的目标路径
     */
    @Override
    @CacheEvict(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_CONFIG_CACHE_NAME)
    public void setTargetPath(Integer projId, String ref, String templatePath, String targetPath) {
        // 1. 读取配置文件
        TemplateConfig templateConfig = getTemplateConfig(projId, ref);
        var templates = templateConfig.getTemplates();
        var ret = templates.stream().filter(t -> t.getTemplatePath().equals(templatePath)).findAny().orElse(null);

        // 2. 更新模板配置
        if (ret == null) {
            // 2.1 模板不存在
            throw new BizException(Constants.TEMPLATE_NOT_EXISTS_MSG_CODE, templatePath);
        }
        // 2.2 更新生成代码的目标路径
        ret.setTargetPath(targetPath);
        // 2.3. 更新配置文件
        updateTemplateConfig(templateConfig, projId, ref, MessageFormat.format(COMMIT_MSG_8, TemplateUtil.getUsername(), templatePath));
    }

    /**
     * 导入模板参数
     *
     * @param projId 项目 ID
     * @param ref 分支、标签或提交的名称
     * @param parametersJson 模板参数 json 文件内容
     */
    @Override
    @CacheEvict(key = "#projId + '_' + #ref", cacheNames = TEMPLATE_PARAMETER_CACHE_NAME)
    public void importParameters(Integer projId, String ref, String parametersJson) {
        var requestParams =  Try.of(() -> objectMapper.readValue(parametersJson, new TypeReference<List<TemplateParameterRequestDto>>() {}))
                .getOrElseThrow(ServerException::new);
        setParameters(projId, ref, requestParams);
    }

    /**
     * 获取模板名称
     */
    private String getTemplateName(String path) {
        if (StringUtils.hasText(path) && path.contains("/")) {
            return path.substring(path.lastIndexOf("/"));
        }
        return path;
    }

    /**
     * 获取配置文件 JSON 字符串
     */
    private String getConfigJson(TemplateConfig config) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
    }

    /**
     * 将集合和 Map 转化为 Json 字符串
     */
    private Object toJsonString(Object value) throws JsonProcessingException {
        if (value != null) {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        }
        return null;
    }

    /**
     * 编辑模板参数
     */
    private void editParam(TemplateParameter p1, TemplateParameterRequestDto p2) {
        Try.run(() -> {
            p1.setName(p2.getName());
            p1.setEnable(p2.getEnable());
            p1.setDesc(p2.getDesc());

            // 设置参数值为 Json 字符串、并设置类型
            p1.setValueClass(getJavaType(p2.getValue()));
            p1.setValue(toJsonString(p2.getValue()));

            // 设置默认值值为 Json 字符串、并设置类型
            p1.setDefaultValueClass(getJavaType(p2.getDefaultValue()));
            p1.setDefaultValue(toJsonString(p2.getDefaultValue()));
        }).getOrElseThrow(ServerException::new);
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
     * 更新模板配置
     */
    private void updateTemplateConfig(TemplateConfig config, Integer projId, String ref, String commitMsg) {
        try {
            var updateFileRequestDto = new GitlabUpdateFileRequestDto();
            updateFileRequestDto.setBranch(ref);
            updateFileRequestDto.setContent(getConfigJson(config));
            updateFileRequestDto.setCommitMessage(commitMsg);
            gitlabClient.updateFile(updateFileRequestDto, projId, Constants.CONFIG_FILE_PATH);
        } catch (JsonProcessingException e) {
            throw new ServerException(e);
        }
    }

    /**
     * 获取缓存键
     */
    private String getCacheKey(String... names) {
        return String.join("_", names);
    }

    /**
     * 清除后加入缓存
     */
    private void evictAndPutCache(String cacheName, String key, Object val) {
       Optional.ofNullable(cacheManager.getCache(cacheName)).ifPresent(c -> {
           c.evictIfPresent(key);
           c.putIfAbsent(key, val);
       });
    }

    /**
     * 创建缓存（不存在）
     */
    private void createCacheIfAbsent(String... cacheNames) {
        for (String c : cacheNames) {
            var ret =  cacheManager.getCache(c);
            if (ret == null) {
                cacheManager.setCacheNames(List.of(c));
            }
        }
    }

    /**
     * 获取 java 类型名
     */
    private String getJavaType(Object val) {
        if (val == null) {
            return null;
        }

        return val.getClass().getTypeName();
    }
}
