package com.taikesoft.fly.business.webapi;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.taikesoft.fly.business.config.AppConfig;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 */

public class MainApi {

    public static void requestCommon(Context context, String partUrl, HttpEntity entity, AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(context, partUrl, entity,
                AppConfig.CONTENT_TYPE, handler);
    }

    public static void requestWipeContentType(Context context, String partUrl, HttpEntity entity, AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(context, partUrl, entity,
                "", handler);
    }

    public static void uploadFile(File file, String url, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(url, params, handler);
    }

    public static void uploadFile(File[] files, String url, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        try {
            int count = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i] != null && files[i].exists()) {
                    params.put("file"+count, files[i]);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(url, params, handler);
    }

    public static void uploadFile(String filePath, String url, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        try {
            InputStream inputStream = new FileInputStream(new File(filePath));
            params.put("file",inputStream,filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(url, params, handler);
    }


}
