package com.taikesoft.fly.ui.myinfo;

import android.view.View;
import android.widget.Button;
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
 * 修改更换
 */

public class ChangeClothesModifyActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.tv_child_name)
    TextView tvChildName;
    @BindView(R.id.rbOk)
    RadioButton rbOk;
    @BindView(R.id.rbNot)
    RadioButton rbNot;
    @BindView(R.id.tv_nurse_type)
    TextView tvNurseType;
    @BindView(R.id.tv_nurse_date)
    TextView tvNurseDate;
    @BindView(R.id.tv_change_time)
    TextView tvChangeTime;
    @BindView(R.id.tv_clothes)
    TextView tvClothes;
    @BindView(R.id.tv_times)
    TextView tvTimes;
    @BindView(R.id.add)
    Button mBtnAdd;
    @BindView(R.id.minus)
    Button mBtnMinus;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    @BindView(R.id.btn_del)
    Button mBtnDel;
    private ArrayList<DictionaryBean> mTypes;
    private List<DictionaryBean> clothesTypes;
    private DictionaryBean selectedClothesType;
    private RecordBean mRecordBean;
    private CommonUtil mCommonUtil;
    private int amount = 1;
    private String mSpiritType;

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_clothes_modify);
        ButterKnife.bind(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.change_clothes_modify_title));
        mRecordBean = (RecordBean) getIntent().getSerializableExtra("mRecordBean");
        String tvNurseTypeContent = mRecordBean.getNurseItem();
        if (mRecordBean.getNurseBeginTime() != null && mRecordBean.getNurseEndTime() != null) {
            tvNurseTypeContent += "," + mRecordBean.getNurseBeginTime() + "-" + mRecordBean.getNurseEndTime();
        }
        mSpiritType = mRecordBean.getSpirit();
        if (StringUtils.equals("好", mSpiritType)) {
            rbOk.setChecked(true);
        } else {
            rbNot.setChecked(true);
        }
        String opTime = mRecordBean.getOperateTime();
        if (opTime != null) {
            if (opTime.length() > 9) {
                tvNurseDate.setText(mRecordBean.getOperateTime().substring(0, 10));
            }
            if (opTime.length() > 15) {
                tvChangeTime.setText(mRecordBean.getOperateTime().substring(11, 16));
            }
        }
        tvNurse.setText(mRecordBean.getNurses());
        tvNurseType.setText(tvNurseTypeContent);
        tvChildName.setText(mRecordBean.getChildName());
        tvClothes.setText(mRecordBean.getContent());
        selectedClothesType = new DictionaryBean();
        selectedClothesType.setName(mRecordBean.getContent());
        amount = Integer.parseInt(mRecordBean.getNurseValue());
        tvTimes.setText(mRecordBean.getNurseValue());
        rbOk.setOnClickListener(this);
        rbNot.setOnClickListener(this);
        tvClothes.setOnClickListener(this);
        tvChangeTime.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
        mBtnMinus.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mCommonUtil = CommonUtil.getInstance();
    }

    public void onSinglePicker() {
        SinglePicker<DictionaryBean> picker = new SinglePicker<>(this, clothesTypes);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(false);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<DictionaryBean>() {
            @Override
            public void onItemPicked(int index, DictionaryBean item) {
                selectedClothesType = item;
                tvClothes.setText(item.getName());
            }
        });
        picker.show();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_change_time:
                onTimePicker();
                break;
            case R.id.tv_clothes:
                onSinglePicker();
                break;
            case R.id.add:
                String times = tvTimes.getText().toString();
                if (times == null || StringUtils.equals("", times)) {
                    amount = 1;
                    tvTimes.setText(String.valueOf(amount));
                } else {
                    if (++amount < 1) {
                        //先加，再判断
                        amount--;
                    } else {
                        tvTimes.setText(String.valueOf(amount));
                    }
                }
                break;
            case R.id.minus:
                String timesCur = tvTimes.getText().toString();
                if (timesCur == null || StringUtils.equals("", timesCur)) {
                    amount = 1;
                    tvTimes.setText(String.valueOf(amount));
                } else {
                    if (--amount < 1) {
                        //先减，再判断
                        amount++;
                    } else {
                        tvTimes.setText(String.valueOf(amount));
                    }
                }
                break;
            case R.id.rbOk:
                rbOk.setChecked(true);
                mSpiritType = "好";
                break;
            case R.id.rbNot:
                rbNot.setChecked(true);
                mSpiritType = "不好";
                break;
            case R.id.btn_del:
                HttpEntity delHttpEntity = mCommonUtil.fillDelHttpEntity(mRecordBean.getId());
                submitNurseRecord(AppConfig.DEL_NURSE_NOTE, delHttpEntity, 1);
                break;
            case R.id.btn_save:
                //更换时间只能改时分秒，不能改日期
                String changeTime = mRecordBean.getOperateTime().substring(0, 11) + tvChangeTime.getText().toString() + ":00";
                mRecordBean.setOperateTime(changeTime);
                HttpEntity entity = mCommonUtil.fillModifyHttpEntity(mRecordBean.getId(), changeTime, selectedClothesType.getName(), new BigDecimal(tvTimes.getText().toString()), null,mSpiritType);
                submitNurseRecord(AppConfig.UPDATE_NURSE_NOTE, entity, 0);
                break;
        }

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
                tvChangeTime.setText(hour + ":" + minute);
            }
        });
        picker.show();
    }

    @Override
    protected void initWebData() {
        initLoading("");
        requestTypes(AppConfig.LIST_TYPES);
    }

    private void requestTypes(String url) {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, url, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log(url + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<DictionaryBean>>() {
                    }.getType();
                    BaseListData<DictionaryBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        mTypes = datas.getData();
                        clothesTypes = new ArrayList<>();
                        for (DictionaryBean bean : mTypes) {
                            if (StringUtils.equals("ReplaceProject", bean.getParentId())) {
                                clothesTypes.add(bean);
                                continue;
                            }
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMessage());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.please_check_network);
                hideLoading();
            }
        });
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
