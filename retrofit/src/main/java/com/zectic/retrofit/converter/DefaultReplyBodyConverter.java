package com.zectic.retrofit.converter;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class DefaultReplyBodyConverter implements ReplyBodyConverter {

    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) throws IOException {
        return body.source().readString(StandardCharsets.UTF_8);
    }
}
