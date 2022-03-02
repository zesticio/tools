package com.zestic.retrofit.config;

import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.log4j.Level;

/**
 * @author deebendukumar
 */
@Data
public class Endpoint {

    private String identity;
    private String baseUrl;
}
