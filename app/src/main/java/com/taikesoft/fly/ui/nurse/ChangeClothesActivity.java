package com.taikesoft.fly.ui.nurse;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.picker.SinglePicker;
import com.taikesoft.fly.business.common.view.picker.TimePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.entity.FillEntity;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.adapter.SearchHeaderAdapter;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 更换
 */
public class ChangeClothesActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_add_icon)
    ImageView mIvAddIcon;
    @BindView(R.id.recyclerview_header)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_add_child)
    TextView mTvAdd;
    @BindView(R.id.rbOk)
    RadioButton rbOk;
    @BindView(R.id.rbNot)
    RadioButton rbNot;
    @BindView(R.id.tv_nurse_date)
    TextView tvNurseDate;
    @BindView(R.id.tv_nurse_time)
    TextView tvNurseTime;
    @BindView(R.id.tv_clothes)
    TextView tvClothes;
    @BindView(R.id.tv_change_time)
    TextView tvChangeTime;
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
    private List<DictionaryBean> clothesTypes;
    private DictionaryBean selectedClothesType;
    private static final int RESULT_RECODE = 0;
    private NurseItemBean mNurseItemBean;
    private List<ChildInfoEntity> mChildren = new ArrayList<>();
    private SearchHeaderAdapter searchHeaderAdapter;
    private int width;
    private CommonUtil mCommonUtil;
    private int amount = 1;
    private String mSpiritType = "好";

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_clothes);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void initView() {
        mNurseItemBean = (NurseItemBean) getIntent().getSerializableExtra("mNurseItemBean");
        mChildren = (List<ChildInfoEntity>) getIntent().getSerializableExtra("mChildData");
        setTitle(mNurseItemBean.getNurseItem());
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        tvNurse.setText(SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
        rbOk.setChecked(true);
        tvNurseTime.setText(mNurseItemBean.getNurseTime());
        //设置默认时间
        String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        tvNurseDate.setText(dateStr.substring(0, 10));
        tvChangeTime.setText(dateStr.substring(11, 16));
        tvTimes.setText(String.valueOf(amount));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //添加头部添加recyclerView
        searchHeaderAdapter = new SearchHeaderAdapter(this, mChildren, width);
        mRecyclerView.setAdapter(searchHeaderAdapter);
        initHeadList();
        //头像点击删除
        searchHeaderAdapter.setOnItemClickListener(new SearchHeaderAdapter.OnClickLisenerI() {
            @Override
            public void setOnClickListener(View view, int position) {
                mChildren.remove(position);
                initHeadList();
            }
        });
        mIvAddIcon.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
        rbOk.setOnClickListener(this);
        rbNot.setOnClickListener(this);
        tvChangeTime.setOnClickListener(this);
        tvClothes.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
        mBtnMinus.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mCommonUtil = CommonUtil.getInstance();
    }

    private void initHeadList() {
        if (mChildren.size() > 0) {
            mIvAddIcon.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            searchHeaderAdapter.notifyDataSetChanged();
            int itemCount = searchHeaderAdapter.getItemCount();
            ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
            if (itemCount >= 6) {
                layoutParams.width = 3 * width / 4;
            } else {
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            mRecyclerView.setLayoutParams(layoutParams);
            mRecyclerView.scrollToPosition(itemCount - 1);
        } else {
            mIvAddIcon.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_add_icon:
                goToChooseChildren();
                break;
            case R.id.tv_add_child:
                goToChooseChildren();
                break;
            case R.id.rbOk:
                rbOk.setChecked(true);
                mSpiritType = "好";
                break;
            case R.id.rbNot:
                rbNot.setChecked(true);
                mSpiritType = "不好";
                break;
            case R.id.tv_change_time:
                onTimePicker();
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
            case R.id.tv_clothes:
                onSinglePicker(tvClothes, clothesTypes);
                break;
            case R.id.btn_save:
                if (mChildren.size() == 0) {
                    AppContext.showToast("请添加儿童");
                } else {
                    String changeTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " " + tvChangeTime.getText().toString() + ":00";
                    HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, mNurseItemBean, changeTime, selectedClothesType.getName(), new BigDecimal(tvTimes.getText().toString()), null, null, mSpiritType);
                    submitNurseRecord(entity, AppConfig.INSERT_NURSE_NOTE);
                }
                break;
        }

    }

    private void goToChooseChildren() {
        Intent intentChoose = new Intent(this, ChooseChildrenActivity.class);
        intentChoose.putExtra("mTagId", "clothes");
        intentChoose.putExtra("mNurseItemBean", mNurseItemBean);
        intentChoose.putExtra("childData", (Serializable) mChildren);
        startActivityForResult(intentChoose, RESULT_RECODE);
    }

    @Override
    public void setListener() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == RESULT_RECODE) {
                mChildren = (ArrayList<ChildInfoEntity>) data.getSerializableExtra("mChildData");
                if (mChildren == null) {
                    mChildren = new ArrayList<>();
                }
            }
        }
    }

    private void submitNurseRecord(HttpEntity entity, String url) {
        MainApi.requestCommon(this, url, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    String httpResult = new String(bytes);
                    TLog.log(url + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getState(), ResultStatus.SUCCESS)) {
                        AppContext.showToast(R.string.submit_success);
                        finish();
                    } else {
                        AppContext.showToast(base.getMessage());
                        finish();
                    }
                    return;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.submit_fail);
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.submit_fail);
            }
        });
    }

    @Override
    protected void initWebData() {
        initLoading("");
        requestTypes();
    }

    private void requestTypes() {
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
        MainApi.requestCommon(this, AppConfig.LIST_CLOTHES_TYPES, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_CLOTHES_TYPES + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<DictionaryBean>>() {
                    }.getType();
                    BaseListData<DictionaryBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        clothesTypes = datas.getData();
                        //初始化衣服类别，默认选择第一项
                        if (clothesTypes != null && clothesTypes.size() > 0) {
                            selectedClothesType = clothesTypes.get(0);
                            tvClothes.setText(selectedClothesType.getName());
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

    public void onSinglePicker(TextView tv, List<DictionaryBean> clothesTypes) {
        SinglePicker<DictionaryBean> picker = new SinglePicker<>(this, clothesTypes);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(false);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<DictionaryBean>() {
            @Override
            public void onItemPicked(int index, DictionaryBean item) {
                selectedClothesType = item;
                tv.setText(item.getName());
            }
        });
        picker.show();
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }
}
