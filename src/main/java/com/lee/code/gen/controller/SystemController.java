package com.lee.code.gen.controller;

import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.dto.UpdatePropertyRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "API-System", description = "接口-系统工具")
@RestController
@RestResponse
@RequestMapping("system")
@RequiredArgsConstructor
public class SystemController implements ApplicationContextAware {

    private final ConfigDataContextRefresher contextRefresher;

    private static final String PROPER_SOURCE_NAME = "dynamicProperties";
    private ConfigurableApplicationContext applicationCtx;

    @Operation(summary = "更新 property", description = "更新 property")
    @PutMapping("/property")
    public void updateProperty(@RequestBody @Valid @Size(min = 1) List<UpdatePropertyRequestDto> request) {
        var properties = request.stream().collect(Collectors.toMap(UpdatePropertyRequestDto::getProperty, UpdatePropertyRequestDto::getValue));
        MapPropertySource propertySource = new MapPropertySource(PROPER_SOURCE_NAME, properties);
        applicationCtx.getEnvironment().getPropertySources().addFirst(propertySource);
        contextRefresher.refresh();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.applicationCtx = (ConfigurableApplicationContext) ctx;
    }
}
