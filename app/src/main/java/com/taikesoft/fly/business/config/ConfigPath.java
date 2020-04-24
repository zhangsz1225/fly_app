package com.taikesoft.fly.business.config;

import android.os.Environment;

import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;

/**
 * 配置类
 */
public class ConfigPath {
    /**
     * 文件地址
     */
    public final static String demsPath = Environment.getExternalStorageDirectory()
            + "/fly/";
    /**
     * 文件下载地址
     */
    public static String downLoadPathRoot = Environment.getExternalStorageDirectory()
            + "/fly/" + "downloads";

    public static String downLoadPath = Environment.getExternalStorageDirectory()
            + "/fly/" + "downloads" + "/" + SharedPreferencesManager.getString(ResultStatus.USER_NAME) + "/";
    /**
     * 图片下载地址
     */
    public final static String downloadImagePath = Environment.getExternalStorageDirectory()
            + "/fly/" + "downloadImagePath/";

}
