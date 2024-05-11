package com.lee.code.gen.validation;

import com.lee.code.gen.common.Constants;
import com.lee.code.gen.validation.constraints.TargetPath;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class TargetPathValidator implements ConstraintValidator<TargetPath, String> {

    private static final Pattern PATTERN = Pattern.compile(Constants.TARGET_PATH_REGEX);

    /**
     * 生成代码的目标路径
     * 不能以 / 开始和结束，以 / 分割，路径为英数字和-_，可包含变量 ${}
     * 例：src/main/java/controller/${controllerId}Controller.java
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }
}
