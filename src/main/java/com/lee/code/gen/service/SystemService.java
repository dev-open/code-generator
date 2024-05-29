package com.lee.code.gen.service;

import com.lee.code.gen.core.entity.SystemProperty;
import org.springframework.context.EnvironmentAware;

import java.util.List;

public interface SystemService extends EnvironmentAware {

    void updateProperty(List<SystemProperty> request);

    void clearResponseCache();
}
