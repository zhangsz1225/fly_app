package com.taikesoft.fly.business.retrofit;

import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.ui.myinfo.MyInfoActivity;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 */

public class ResetInterceptor implements Interceptor {

    private final static String HOST = "192.168.0.252";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request;
        if (MyInfoActivity.IMG_STATUS) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().toString())
                    .addHeader("Host", HOST)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("application1", "android-v1.0")
                    .addHeader("application2", SharedPreferencesManager.getString(ResultStatus.USER_ID))
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().toString())
                    .addHeader("Host", HOST)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("application1", "android-v1.0")
                    .addHeader("application2", SharedPreferencesManager.getString(ResultStatus.USER_ID))
                    .addHeader("Content-Type", "application/json")
                    .build();
        }
        return chain.proceed(request);
    }

}
