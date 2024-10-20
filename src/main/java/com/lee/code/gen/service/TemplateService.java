package com.lee.code.gen.service;

import com.lee.code.gen.common.RPage;
import com.lee.code.gen.core.entity.TemplateConfigItem;
import com.lee.code.gen.core.entity.TemplateInfo;
import com.lee.code.gen.core.entity.TemplateParameter;
import com.lee.code.gen.dto.TemplateParameterRequestDto;
import com.lee.code.gen.dto.TemplateProjResponseDto;

import java.util.List;

public interface TemplateService {

    void createTemplateProj(String projName, String author, String desc);

    void createTemplate(String path, Integer projId, String content, String targetPath, String parameters);

    TemplateInfo getTemplate(Integer projId, String templatePath, String ref);

    List<TemplateConfigItem> getTemplateConfigs(Integer projId, String ref);

    List<TemplateParameter> getParameters(Integer projId, String ref);

    void setParameters(Integer projId, String ref, List<TemplateParameterRequestDto> parameters, boolean updateExisted);

    RPage<List<TemplateProjResponseDto>> getTemplateProjs(String search, String orderBy, Integer page, Integer perPage, String ref);

    void updateTemplateContent(Integer projId, String path, String content, String ref);

    void removeTemplate(Integer projId, String templatePath, String ref);

    void removeParameter(Integer projId, String parameterName, String ref);

    void removeTemplateProj(Integer projId);

    void setTargetPath(Integer projId, String ref, String templateName, String targetPath);

    void importParameters(Integer projId, String ref, String parametersJson);
}
