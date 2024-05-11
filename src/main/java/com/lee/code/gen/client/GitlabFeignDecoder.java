package com.lee.code.gen.client;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@RequiredArgsConstructor
public class GitlabFeignDecoder implements Decoder {

    private final SpringDecoder springDecoder;

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (type instanceof ParameterizedType newType) {
            if (FeignResponse.class.getName().equals(newType.getRawType().getTypeName())) {
                FeignResponse<?> feignResponse = new FeignResponse<>();
                feignResponse.setBody(springDecoder.decode(response, newType.getActualTypeArguments()[0]));
                feignResponse.setHeaders(response.headers());
                return feignResponse;
            }
            return springDecoder.decode(response, type);
        }
        return springDecoder.decode(response, type);
    }
}
