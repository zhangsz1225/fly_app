package com.taikesoft.fly.business.config;

import android.os.Environment;

/**
 * 配置类
 */
public class ConfigAppPath {
    /**
     * 文件下载地址
     */
    public final static String downLoadPath = Environment.getExternalStorageDirectory()
            + "/fly/appInfo/";
}
