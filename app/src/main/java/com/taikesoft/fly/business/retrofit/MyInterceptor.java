package com.taikesoft.fly.business.retrofit;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * created by yezhengyu on 2018/11/23 16:05
 */

public class MyInterceptor implements Interceptor {

    private final static String HOST = "192.168.0.252";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("Accept-Language", Locale.getDefault().toString())
                .addHeader("Host", HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        return chain.proceed(request);
    }

}
