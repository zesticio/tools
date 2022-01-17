package com.zectic.retrofit.converter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public interface RequestBodyConverter {
    MediaType CONTENT_TYPE_TEXT = MediaType.get(MimeTypeUtils.TEXT_PLAIN_VALUE);
    MediaType CONTENT_TYPE_JSON = MediaType.get(MimeTypeUtils.APPLICATION_JSON_VALUE);

    RequestBody toRequestBody(@NotNull Object entity, Type type) throws IOException;

    default RequestBody buildRequestBody(String payload) {
        return RequestBody.create(payload, CONTENT_TYPE_TEXT);
    }
}
