package com.taikesoft.fly.business.constant;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.context.AppContext;

/**
 */

public class ResultStatus {
    public static final String SUCCESS = "ok";
    public static final String FAIL = "fail";
    /**
     * 密码
     */
    public static final String PWD = "pwd";
    /**
     * 账号表id
     */
    public static final String USER_ID = "user_id";
    /**
     * 账号
     */
    public static final String USER_NAME = "user_name";
    /**
     * 账号头像地址
     */
    public static final String USER_IMG = "user_img";
    /**
     * 真实姓名
     */
    public static final String REAL_NAME = "real_name";
    /**
     * 员工表id
     */
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String TOKEN = "token";
    public static final String LOGIN_STATUS = "login_status";
    public static final String UNLOGIN = "unlogin";
    public static final String LOGINED = "logined";
    public static final String USER_ERROR = "1001";
    public static final String PW_ERROR = "1002";
    public static final String TOKEN_INVALID = AppContext.mResource.getString(R.string.token_invalid);
    public static final String PHONE = "phone";
    public static final String DEPT = "dept";
    public static final String PORTRAIT_IMG = "portrait_img";
    public static final String CHANNEL_ID = "channelId";
}
