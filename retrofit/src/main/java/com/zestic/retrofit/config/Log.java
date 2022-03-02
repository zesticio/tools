package com.zestic.retrofit.config;

import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author deebendukumar
 */
@Data
public class Log {

    private Boolean enabled = false;
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
}
