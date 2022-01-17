package com.zectic.retrofit.converter;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;

public interface ReplyBodyConverter {
    Object fromResponseBody(@NotNull ResponseBody body, Type type) throws IOException;
}
