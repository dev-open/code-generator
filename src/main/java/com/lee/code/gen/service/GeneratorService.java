package com.lee.code.gen.service;

import com.lee.code.gen.core.entity.TemplateFileTree;
import com.lee.code.gen.core.entity.TemplateParameter;
import com.lee.code.gen.dto.PreviewGeneratedCodeResponseDto;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface GeneratorService {

    String downloadCode(Integer projId, String ref, ZipOutputStream zip);

    List<TemplateParameter> getAvailableParameters(Integer projId, String ref);

    List<TemplateFileTree> preview(Integer projId, String ref);
}

