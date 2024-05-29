package com.lee.code.gen.service.impl;

import com.lee.code.gen.core.entity.SystemProperty;
import com.lee.code.gen.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final ConfigDataContextRefresher contextRefresher;
    private final CaffeineCacheManager cacheManager;
    private ConfigurableEnvironment environment;

    private static final String PROPER_SOURCE_NAME = "dynamicProperties";

    /**
     * 更新 property
     *
     * @param properties property集合
     */
    @Override
    public void updateProperty(List<SystemProperty> properties) {
        MapPropertySource propertySource;
        // 过滤不存在的 property
        var filteredRequest =  properties.stream().filter(x -> environment.containsProperty(x.getName())).toList();
        if (environment.getPropertySources().contains(PROPER_SOURCE_NAME)) {
            propertySource = (MapPropertySource) environment.getPropertySources().get(PROPER_SOURCE_NAME);
            if (propertySource != null) {
                filteredRequest.forEach(x ->
                        propertySource.getSource().put(x.getName(), x.getValue()));
            }
        } else {
            propertySource = new MapPropertySource(PROPER_SOURCE_NAME, properties.stream()
                    .collect(Collectors.toMap(SystemProperty::getName, SystemProperty::getValue)));
            environment.getPropertySources().addFirst(propertySource);
        }
        // 刷新上下上下文
        contextRefresher.refresh();
    }

    /**
     * 清空 response 缓存
     */
    @Override
    public void clearResponseCache() {
        cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .filter(Objects::nonNull).forEach(Cache::clear);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
