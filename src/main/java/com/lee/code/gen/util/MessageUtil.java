package com.lee.code.gen.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageUtil {

    // 国际化消息
    private final MessageSource messageSource;

    public String getMsg(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public String getMsg(String code, String defaultMsg, Object... args) {
        return messageSource.getMessage(code, args, defaultMsg, LocaleContextHolder.getLocale());
    }
}
