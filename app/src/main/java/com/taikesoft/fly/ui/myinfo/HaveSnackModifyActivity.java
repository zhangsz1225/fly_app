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
 * 修改水果点心
 */

public class HaveSnackModifyActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.tv_snack)
    TextView tvSnack;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.ll_snack)
    LinearLayout ll_snack;
    @BindView(R.id.lbl_snack)
    LineBreakLayout lbl_snack;
    @BindView(R.id.ll_fruit)
    LinearLayout ll_fruit;
    @BindView(R.id.lbl_fruit)
    LineBreakLayout lbl_fruit;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    @BindView(R.id.btn_del)
    Button mBtnDel;
    private CommonUtil mCommonUtil;
    private ArrayList<DictionaryBean> mTypes;
    private List<DictionaryBean> snackList = new ArrayList<>();
    private List<DictionaryBean> fruitList = new ArrayList<>();
    private List<DictionaryBean> fruitLables = new ArrayList<>();
    private List<DictionaryBean> snackLables = new ArrayList<>();
    private RecordBean mRecordBean;
    private String mSpiritType;
    private String mType;

    @Override
    public void setView() {
        setContentView(R.layout.activity_have_snack_modify);
        ButterKnife.bind(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.have_snack_modify_title));
        mRecordBean = (RecordBean) getIntent().getSerializableExtra("mRecordBean");
        tvChildName.setText(mRecordBean.getChildName());
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
        }
        tvNurse.setText(mRecordBean.getNurses());
        tvNurseType.setText(tvNurseTypeContent);
        tvSnack.setText(mRecordBean.getContent());
        rbOk.setOnClickListener(this);
        rbNot.setOnClickListener(this);
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
            case R.id.btn_del:
                HttpEntity delHttpEntity = mCommonUtil.fillDelHttpEntity(mRecordBean.getId());
                submitNurseRecord(AppConfig.DEL_NURSE_NOTE, delHttpEntity, 1);
                break;
            case R.id.btn_save:
                String content = mRecordBean.getContent();
                List<String> lables = new ArrayList<>();
                if (StringUtils.equals("fruit",tvType.getText().toString())) {
                    for (DictionaryBean lable : fruitLables) {
                        lables.add(lable.getName());
                    }
                }else{
                    for (DictionaryBean lable : snackLables) {
                        lables.add(lable.getName());
                    }
                }
                if(lables.size() > 0){
                    content = TextUtils.join(",", lables);
                }
                HttpEntity entity = mCommonUtil.fillModifyHttpEntity(mRecordBean.getId(), null, content, null, null, mSpiritType);
                submitNurseRecord(AppConfig.UPDATE_NURSE_NOTE, entity, 0);
                break;
        }

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
                        // String type = "Fruit";
                        mTypes = datas.getData();
                        List<String> fruitStrs = new ArrayList<>();
                        for (DictionaryBean bean : mTypes) {

                            if (StringUtils.equals("Fruit", bean.getParentId())) {
                                fruitStrs.add(bean.getName());
                                if (!StringUtils.equals("无", bean.getName())) {
                                    fruitList.add(bean);
                                }
                            } else if (StringUtils.equals("Dessert", bean.getParentId())) {
                                if (!StringUtils.equals("无", bean.getName())) {
                                    snackList.add(bean);
                                }
                            } else {
                                continue;
                            }
                        }
                        String contentFirst = mRecordBean.getContent().split(",")[0];
                        if (fruitStrs.contains(contentFirst)) {
                            mType = "fruit";
                            ll_fruit.setVisibility(View.VISIBLE);
                            lbl_fruit.setVisibility(View.VISIBLE);
                            ll_snack.setVisibility(View.GONE);
                            lbl_snack.setVisibility(View.GONE);
                            lbl_fruit.setLables(fruitList, true);
                            fruitLables = lbl_fruit.getSelectedLables();
                            tvType.setText("水果：");
                        } else {
                            mType = "snack";
                            ll_fruit.setVisibility(View.GONE);
                            lbl_fruit.setVisibility(View.GONE);
                            ll_snack.setVisibility(View.VISIBLE);
                            lbl_snack.setVisibility(View.VISIBLE);
                            lbl_snack.setLables(snackList, true);
                            snackLables = lbl_snack.getSelectedLables();
                            tvType.setText("点心：");
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
