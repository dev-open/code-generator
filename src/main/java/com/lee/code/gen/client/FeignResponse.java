package com.lee.code.gen.client;

import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Data
public class FeignResponse<T> {
    private Object body;
    private Map<String, Collection<String>> headers;

    @SuppressWarnings("unchecked")
    public T getBody() {
        return (T) body;
    }

    public Optional<String> getHeader(String header) {
        String headerVal = null;
        if (headers.containsKey(header)) {
            headerVal = headers.get(header).stream().findFirst().orElse(null);
        }
        return Optional.ofNullable(headerVal);
    }
}
