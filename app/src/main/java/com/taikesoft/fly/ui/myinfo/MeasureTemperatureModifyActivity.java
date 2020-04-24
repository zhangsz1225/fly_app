package com.taikesoft.fly.ui.myinfo;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.Base;
import com.taikesoft.fly.business.common.utils.CommonUtil;
import com.taikesoft.fly.business.common.utils.ConvertUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.picker.NumberPicker;
import com.taikesoft.fly.business.common.view.picker.TimePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.math.BigDecimal;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 修改体温测量记录
 */

public class MeasureTemperatureModifyActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_measure_time)
    TextView tvMeasureTime;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    @BindView(R.id.btn_del)
    Button mBtnDel;
    private CommonUtil mCommonUtil;
    private RecordBean mRecordBean;
    private String mSpiritType;
    @Override
    public void setView() {
        setContentView(R.layout.activity_measure_temperature_modify);
        ButterKnife.bind(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.measure_temperature_modify_title));
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
            if (opTime.length() > 15) {
                opTime = mRecordBean.getOperateTime().substring(11, 16);
                tvMeasureTime.setText(opTime);
            }
        }
        tvNurse.setText(mRecordBean.getNurses());
        tvNurseType.setText(tvNurseTypeContent);
        tvChildName.setText(mRecordBean.getChildName());
        tvTemperature.setText(mRecordBean.getNurseValue());
        tvTemperature.setOnClickListener(this);
        tvMeasureTime.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mCommonUtil = CommonUtil.getInstance();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rbOk:
                rbOk.setChecked(true);
                mSpiritType = "好";
                break;
            case R.id.rbNot:
                rbNot.setChecked(true);
                mSpiritType = "不好";
                break;
            case R.id.tv_measure_time:
                onTimePicker();
                break;
            case R.id.tv_temperature:
                onTemperaturePicker();
                break;
            case R.id.btn_del:
                HttpEntity delHttpEntity = mCommonUtil.fillDelHttpEntity(mRecordBean.getId());
                submitNurseRecord(AppConfig.DEL_NURSE_NOTE, delHttpEntity, 1);
                break;
            case R.id.btn_save:
                    //测量时间只能改时分秒，不能改日期
                    String measureTime = mRecordBean.getOperateTime().substring(0, 11) + tvMeasureTime.getText().toString() + ":00";
                    mRecordBean.setOperateTime(measureTime);
                    HttpEntity entity = mCommonUtil.fillModifyHttpEntity(mRecordBean.getId(), measureTime, mRecordBean.getNurseItem(), new BigDecimal(tvTemperature.getText().toString()), null,mSpiritType);
                    submitNurseRecord(AppConfig.UPDATE_NURSE_NOTE, entity, 0);
                break;
        }

    }

    public void onTemperaturePicker() {
        NumberPicker picker = new NumberPicker(this);
        picker.setWidth(picker.getScreenWidthPixels());
        picker.setCycleDisable(false);
        picker.setAnimationStyle(R.style.popupwindow_anim_style);
        picker.setDividerVisible(false);
        picker.setOffset(3);//偏移量
        picker.setRange(33.0, 42.0, 0.1);//数字范围
        picker.setSelectedItem(Double.parseDouble(mRecordBean.getNurseValue()));
        picker.setLabel("℃");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                tvTemperature.setText(String.valueOf(item.floatValue()));
            }
        });
        picker.show();
    }

    public void onTimePicker() {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setAnimationStyle(R.style.popupwindow_anim_style);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setTextPadding(ConvertUtils.toPx(this, 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                tvMeasureTime.setText(hour + ":" + minute);
            }
        });
        picker.show();
    }


    @Override
    protected void initWebData() {

    }
    @Override
    protected boolean hasLoading() {
        return true;
    }

    private void submitNurseRecord(String url, HttpEntity entity, int operate) {
        MainApi.requestCommon(this, url, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    String httpResult = new String(bytes);
                    TLog.log(url + "-->" + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getState(), ResultStatus.SUCCESS)) {
                        if (operate == 0) {
                            AppContext.showToast(R.string.submit_modify_success);
                        } else {
                            AppContext.showToast(R.string.submit_del_success);
                        }
                        finish();
                    } else {
                        AppContext.showToast(base.getMessage());
                        finish();
                    }
                    return;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (operate == 0) {
                    AppContext.showToast(R.string.submit_modify_fail);
                } else {
                    AppContext.showToast(R.string.submit_del_fail);
                }
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (operate == 0) {
                    AppContext.showToast(R.string.submit_modify_fail);
                } else {
                    AppContext.showToast(R.string.submit_del_fail);
                }
            }
        });
    }
}
