package com.taikesoft.fly.ui.login;

import android.content.Intent;
import android.os.SystemClock;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;

import androidx.core.content.ContextCompat;

/**
 * 启动页
 */

public class LauncherActivity extends BaseActivity {

    @Override
    public void setView() {
        setContentView(R.layout.activity_launcher);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(getApplicationContext(), R.color.app_color_blue));
    }

    @Override
    public void setListener() {
    }

    @Override
    protected void initView() {
        super.initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                finish();
                 /* String loginStatus = SharedPreferencesManager.getString(ResultStatus.LOGIN_STATUS);
        if (loginStatus != null && StringUtils.equals(loginStatus, ResultStatus.LOGINED)) {
            AppContext.getInstance().reinitWebApi();
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
            finish();
        }*/
            }
        }).start();
    }
}
