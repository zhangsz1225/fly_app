package com.taikesoft.fly.business.constant;

import com.taikesoft.fly.business.config.AppConfig;

/**
 */
public class SysCode {
    public static final String SUCCESS = "ok";
    public static final String OFFLINE_DOWNLOAD_URL = AppConfig.BASE_SERVER_URL + "quality/offline";
    //下载App的请求
    public static final String APP_DOWNLOAD_URL = AppConfig.BASE_SERVER_URL + "static/";
    //应用下载区分
    public static final String APPINFO_APPLICATION = "application";
    public static final String APPINFO_MYDOWNLOADS = "MyDownloads";
    public static final String APPINFO_MOREAPPLIACTIONS = "MoreAppliactions";

    public static final String ACTION_RELOAD_DATA_SUCCESS = "action_reload_data_success";
    public static final String ACTION_RELOAD_DATA_FUCK_SUCCESS = "action_reload_data_fuck_success";
    //头像修改
    public static final String CHANGE_PORTRAIT_SUCCESS = "change_portrait_success";
    public static final String FAILURE_INTERNET = "failure_internet";
    public static final String JUMP_CURRENTINDEX = "jump_currentIndex";
}
