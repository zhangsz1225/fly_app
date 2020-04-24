package com.taikesoft.fly.ui.myinfo;

import android.view.View;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.context.AppContext;


/**
 *
 */

public class WarnmsgDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle, tvType, tvContent, tvWarnTime;
    private String flag;
    @Override
    protected void initView() {
        flag = getIntent().getStringExtra("flag");
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(AppContext.mResource.getString(R.string.message_detail_title));

        tvType = (TextView) findViewById(R.id.tvType);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvWarnTime = (TextView) findViewById(R.id.tvWarnTime);


        tvType.setText("消息类型：" + getIntent().getStringExtra("type"));
        tvContent.setText("消息内容：" + getIntent().getStringExtra("content"));
        tvWarnTime.setText("发送时间：" + FromatDate(getIntent().getStringExtra("time")));

    }
    private String FromatDate(String time) {
        return time.substring(0, 4) + "年" + time.substring(5, 7) + "月" + time.substring(8, 10) + "日 " + time.substring(11, 19);
    }
    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }
    @Override
    public void onClick(View view) {

    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_warnmsg_detail);
    }

    @Override
    public void setListener() {

    }
}
