/*
 *
 * Version:  1.0.0
 *
 * Authors:  Kumar <kumar@elitasolutions.in>
 *
 *******************************************************************************
 *
 * Copyright (c) 2009,2010,2011 Elita IT Solutions
 * All Rights Reserved.
 *
 *******************************************************************************
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Elita IT Solutions and its suppliers, if any.
 * The intellectual and technical concepts contained
 * herein are proprietary to Elita IT Solutions
 * and its suppliers and may be covered by India and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Elita IT Solutions.
 *
 * The above copyright notice and this permission notice must be included
 * in all copies of this file.
 *
 * Description:
 */
package com.zestic.retrofit.interceptor;

import com.zestic.retrofit.annotation.HttpInterceptor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author deebendukumar
 */
@Component
@HttpInterceptor(name = "basic-interceptor")
public class BasicInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
