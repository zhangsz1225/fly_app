package com.taikesoft.fly.ui.myinfo;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.Base;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.CommonUtil;
import com.taikesoft.fly.business.common.utils.ConvertUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.LineBreakLayout;
import com.taikesoft.fly.business.common.view.picker.NumberPicker;
import com.taikesoft.fly.business.common.view.picker.SinglePicker;
import com.taikesoft.fly.business.common.view.picker.TimePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 删除无需填报的记录
 */

public class NoNeedFillInDelActivity extends BaseActivity {
    @BindView(R.id.tv_child_name)
    TextView tvChildName;
    @BindView(R.id.rbOk)
    RadioButton rbOk;
    @BindView(R.id.rbNot)
    RadioButton rbNot;
    @BindView(R.id.tv_nurse_date)
    TextView tvNurseDate;
    @BindView(R.id.tv_nurse_type)
    TextView tvNurseType;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_del)
    Button mBtnDel;
    private CommonUtil mCommonUtil;
    private RecordBean mRecordBean;
    private String mSpiritType;
    @Override
    public void setView() {
        setContentView(R.layout.activity_no_need_fill_in_del);
        ButterKnife.bind(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.record_delete_title));
        mRecordBean = (RecordBean) getIntent().getSerializableExtra("mRecordBean");
        String tvNurseTypeContent = mRecordBean.getNurseItem();
        if (mRecordBean.getNurseBeginTime() != null && mRecordBean.getNurseEndTime() != null) {
            tvNurseTypeContent += "," + mRecordBean.getNurseBeginTime() + "-" + mRecordBean.getNurseEndTime();
        }
        mSpiritType = mRecordBean.getSpirit();
        if(StringUtils.equals("好",mSpiritType)){
            rbOk.setChecked(true);
        }else{
            rbNot.setChecked(true);
        }
        String opTime = mRecordBean.getOperateTime();
        if (opTime != null) {
            if (opTime.length() > 9) {
                tvNurseDate.setText(mRecordBean.getOperateTime().substring(0,10));
            }
        }
        tvNurse.setText(mRecordBean.getNurses());
        tvNurseType.setText(tvNurseTypeContent);
        tvChildName.setText(mRecordBean.getChildName());
        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpEntity delHttpEntity = mCommonUtil.fillDelHttpEntity(mRecordBean.getId());
                submitNurseRecord(delHttpEntity);
            }
        });
        rbOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbOk.setChecked(true);
                mSpiritType = "好";
            }
        });
        rbNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbNot.setChecked(true);
                mSpiritType = "不好";
            }
        });

        mCommonUtil = CommonUtil.getInstance();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void initWebData() {

    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private void submitNurseRecord(HttpEntity entity) {
        MainApi.requestCommon(this, AppConfig.DEL_NURSE_NOTE, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.DEL_NURSE_NOTE + "-->" + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getState(), ResultStatus.SUCCESS)) {
                        AppContext.showToast(R.string.submit_del_success);
                        finish();
                    } else {
                        AppContext.showToast(base.getMessage());
                        finish();
                    }
                    return;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.submit_del_fail);
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.submit_del_fail);
            }
        });
    }
}
