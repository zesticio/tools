package com.zectic.retrofit.converter;

import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class DefaultRequestBodyConverter implements RequestBodyConverter {
    @Override
    public RequestBody toRequestBody(@NotNull Object entity, Type type) {
        return buildRequestBody(String.valueOf(entity));
    }
}
