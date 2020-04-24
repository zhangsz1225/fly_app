package com.taikesoft.fly.business.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.taikesoft.fly.R;
import com.taikesoft.fly.ui.login.LoginActivity;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.context.AppManager;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import androidx.multidex.MultiDexApplication;
import com.taikesoft.fly.business.common.utils.StringUtils;
public class BaseApplication extends MultiDexApplication {

    public static int app_vesion = 5;// 本地数据库版本号
    public static Context mContext;
    public static Resources mResource;

    private static String LAST_TOAST = "";
    private static long LAST_TOAST_TIME;
    private static Toast _toast;

    public static String pagesize = "20";// 分页数每页多少条
    public static Dialog tipDialog;
    public static String Serverpath = "http://192.168.0.252:8080";
    private static Toast toast = null;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public static synchronized BaseApplication context() {
        return (BaseApplication) mContext;
    }

    protected void init() {
        mContext = getApplicationContext();
        mResource = mContext.getResources();
        QMUISwipeBackActivityManager.init(this);
    }

    public static void showToast(int message) {
        //showToast(message, Toast.LENGTH_LONG, 0);
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(String message) {
        if (StringUtils.equalsIgnoreCase(message, ResultStatus.TOKEN_INVALID)) {
            Toast.makeText(mContext, AppContext.mResource.getString(R.string.timeout_pleaselogin), Toast.LENGTH_LONG).show();
            SharedPreferencesManager.putString(ResultStatus.LOGIN_STATUS, ResultStatus.UNLOGIN);
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return;
        } else {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    public static void showToastText(String msg) {
        if (toast == null) {
            toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showToast(int message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon);
    }

    public static void showToast(String message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon, Gravity.FILL_HORIZONTAL
                | Gravity.TOP);
    }

    public static void showToast(int message, int duration, int icon) {
        showToast(message, duration, icon, Gravity.FILL_HORIZONTAL
                | Gravity.TOP);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity) {
        showToast(mContext.getString(message), duration, icon, gravity);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity, Object... args) {
        showToast(mContext.getString(message, args), duration, icon, gravity);
    }

    public static void showToast(String message, int duration, int icon,
                                 int gravity) {
        if (message != null && !message.equalsIgnoreCase("")) {
            long time = System.currentTimeMillis();
            if (!message.equalsIgnoreCase(LAST_TOAST)
                    || Math.abs(time - LAST_TOAST_TIME) > 2000) {
                View view = LayoutInflater.from(mContext).inflate(
                        R.layout.view_toast, null);
                ((TextView) view.findViewById(R.id.title_tv)).setText(message);
                if (icon != 0) {
                    ImageView icon_iv = (ImageView) view
                            .findViewById(R.id.icon_iv);
                    icon_iv.setVisibility(View.VISIBLE);
                    icon_iv.setImageResource(icon);
                }
                if (_toast == null) {
                    _toast = new Toast(mContext);
                }
                _toast.setView(view);
                _toast.setDuration(duration);
                _toast.show();

                LAST_TOAST = message;
                LAST_TOAST_TIME = System.currentTimeMillis();
            }
        }
    }

    /**
     * 获取手机的版本
     *
     * @return
     */
    @SuppressWarnings("unused")
    public static String getVersionInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }
}
