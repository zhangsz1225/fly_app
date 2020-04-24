package com.taikesoft.fly.business.common.utils;

import android.util.Log;

public class TLog {

    private static final String LOG_TAG = "crcementgps";
    public static boolean DEBUG = false;

    public TLog() {

    }

    public static final void analytics(String log) {
        if (log == null)
            return;
        if (DEBUG)
            Log.d(LOG_TAG, log);
    }

    public static final void error(String log) {
        if (log == null)
            return;
        if (DEBUG)
            Log.e(LOG_TAG, "" + log);
    }

    public static final void log(String log) {
        if (log == null)
            return;
        if (DEBUG)
            Log.i(LOG_TAG, log);
    }

    public static final void log(String tag, String log) {
        if (log == null)
            return;
        if (DEBUG)
            Log.i(tag, log);
    }

    public static final void logv(String log) {
        if (log == null)
            return;
        if (DEBUG)
            Log.v(LOG_TAG, log);
    }

    public static final void warn(String log) {
        if (log == null)
            return;
        if (DEBUG)
            Log.w(LOG_TAG, log);
    }

    /**
     * 分段打印出较长log文本
     *
     * @param log       原log文本
     * @param showCount 规定每段显示的长度（最好不要超过eclipse限制长度）
     */
    /*public static void showLogCompletion(String log) {
        /*try {
            if (log.length() > 4000) {
                String show = log.substring(0, 4000);
//          System.out.println(show);
                Log.i("TAG", show + "");
                if ((log.length() - 4000) > 4000) {//剩下的文本还是大于规定长度
                    String partLog = log.substring(4000, log.length());
                    showLogCompletion(partLog);
                } else {
                    String surplusLog = log.substring(4000, log.length());
//              System.out.println(surplusLog);
                    Log.i("TAG", surplusLog + "");
                }
            } else {
//          System.out.println(log);
                Log.i("TAG", log + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
}
