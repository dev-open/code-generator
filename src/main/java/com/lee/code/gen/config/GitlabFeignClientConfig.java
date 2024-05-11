package com.lee.code.gen.config;

import com.lee.code.gen.client.GitlabFeignDecoder;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;

public class GitlabFeignClientConfig {

    @Bean
    public Decoder gitlabFeignDecoder(ObjectFactory<HttpMessageConverters> msgConverters, ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        return new GitlabFeignDecoder(new SpringDecoder(msgConverters, customizers));
    }
}
