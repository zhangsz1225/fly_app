package com.taikesoft.fly.ui.nurse;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.picker.SinglePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.adapter.SearchHeaderAdapter;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;
import com.taikesoft.fly.ui.nurse.entity.MedicineInfoEntity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 服药
 */
public class TakeMedicineActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.tv_medicine)
    TextView tvMedicine;
    @BindView(R.id.tv_medicine_unit)
    TextView tvMedicineUnit;
    @BindView(R.id.et_dosage)
    EditText etDosage;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    private SearchHeaderAdapter searchHeaderAdapter;
    private List<MedicineInfoEntity> medicineList = new ArrayList<>();
    private List<DictionaryBean> medicineUnitDictList = new ArrayList<>();
    private MedicineInfoEntity selectedMedicine;
    private DictionaryBean selectedMedicineUnit;
    private NurseItemBean mNurseItemBean;
    private List<MedicineInfoEntity> mMedicineData;
    private List<ChildInfoEntity> mChildren = new ArrayList<>();
    private static final int RESULT_RECODE = 0;
    public static TakeMedicineActivity mTakeActivity;
    private CommonUtil mCommonUtil;
    private int width;
    private String mSpiritType = "好";

    @Override
    public void setView() {
        setContentView(R.layout.activity_take_medicine);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        mTakeActivity = this;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void initView() {
        mNurseItemBean = (NurseItemBean) getIntent().getSerializableExtra("mNurseItemBean");
        mChildren = (List<ChildInfoEntity>) getIntent().getSerializableExtra("mChildData");
        mMedicineData = (List<MedicineInfoEntity>) getIntent().getSerializableExtra("mMedicineData");
        setTitle(mNurseItemBean.getNurseItem());
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        //会被后面的initWebData覆盖，故initWebData做判断
        if (mMedicineData != null && mMedicineData.size() > 0) {
            selectedMedicine = mMedicineData.get(mMedicineData.size() - 1);
            tvMedicine.setText(selectedMedicine.getName());
        }
        selectedMedicineUnit = (DictionaryBean) getIntent().getSerializableExtra("mMedicineUnit");
        if (selectedMedicineUnit != null) {
            tvMedicineUnit.setText(selectedMedicineUnit.getName());
        }
        String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        tvNurseDate.setText(dateStr.substring(0, 10));
        tvNurse.setText(SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
        etDosage.setText(getIntent().getStringExtra("mDosage"));

        String spirit = getIntent().getStringExtra("mSpiritType");

        if (spirit != null) {
            mSpiritType = spirit;
            if (StringUtils.equals("好", mSpiritType)) {
                rbOk.setChecked(true);
            } else {
                rbNot.setChecked(true);
            }
        } else {
            rbOk.setChecked(true);
        }

        tvNurseTime.setText(mNurseItemBean.getNurseTime());
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
        tvMedicineUnit.setOnClickListener(this);
        tvMedicine.setOnClickListener(this);
        etDosage.setOnClickListener(this);
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
                Intent intentChoose = new Intent(this, ChooseChildrenActivity.class);
                intentChoose.putExtra("mTagId", "clothes");
                intentChoose.putExtra("mNurseItemBean", mNurseItemBean);
                intentChoose.putExtra("childData", (Serializable) mChildren);
                startActivityForResult(intentChoose, RESULT_RECODE);
                break;
            case R.id.tv_add_child:
                Intent intent = new Intent(this, ChooseChildrenActivity.class);
                intent.putExtra("mTagId", "clothes");
                intent.putExtra("mNurseItemBean", mNurseItemBean);
                intent.putExtra("childData", (Serializable) mChildren);
                startActivityForResult(intent, RESULT_RECODE);
                break;
            case R.id.rbOk:
                rbOk.setChecked(true);
                mSpiritType = "好";
                break;
            case R.id.rbNot:
                rbNot.setChecked(true);
                mSpiritType = "不好";
                break;
            case R.id.tv_medicine:
                Intent intentMedicine = new Intent(this, ChooseMedicineActivity.class);
                intentMedicine.putExtra("mTagId", "takeMedicine");
                intentMedicine.putExtra("mChildData", (Serializable) mChildren);
                intentMedicine.putExtra("mMedicine", selectedMedicine);
                intentMedicine.putExtra("mSpiritType", mSpiritType);
                intentMedicine.putExtra("mNurseItemBean", mNurseItemBean);
                intentMedicine.putExtra("mDosage", etDosage.getText().toString());
                intentMedicine.putExtra("mMedicineUnit", selectedMedicineUnit);
                startActivityForResult(intentMedicine, RESULT_RECODE);
                break;
            case R.id.tv_medicine_unit:
                onSinglePicker(tvMedicineUnit);
                break;
            case R.id.btn_save:
                if (mChildren.size() == 0) {
                    AppContext.showToast("请添加儿童");
                } else if (StringUtils.isEmpty(tvMedicine.getText().toString())) {
                    AppContext.showToast("请选择药品");
                } else if (StringUtils.isEmpty(tvMedicineUnit.getText().toString())) {
                    AppContext.showToast("请选择单位");
                } else if (StringUtils.isEmpty(etDosage.getText().toString())) {
                    AppContext.showToast("请输入剂量");
                } else {
                    HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, mNurseItemBean, DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), selectedMedicine.getName(), new BigDecimal(etDosage.getText().toString()), tvMedicineUnit.getText().toString(), null, mSpiritType);
                    submitNurseRecord(entity, AppConfig.INSERT_NURSE_NOTE);
                }
                break;
        }

    }

    public void onSinglePicker(TextView tv) {
        SinglePicker<DictionaryBean> picker = new SinglePicker<>(this, medicineUnitDictList);
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<DictionaryBean>() {
            @Override
            public void onItemPicked(int index, DictionaryBean item) {
                selectedMedicineUnit = item;
                tv.setText(item.getName());
            }
        });
        picker.show();
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
        if (resultCode == 2) {
            if (requestCode == RESULT_RECODE) {
                mMedicineData = (ArrayList<MedicineInfoEntity>) data.getSerializableExtra("mMedicineData");
                if (mMedicineData == null) {
                    mMedicineData = new ArrayList<>();
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
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.LIST_MEDICINE, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_MEDICINE + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<MedicineInfoEntity>>() {
                    }.getType();
                    BaseListData<MedicineInfoEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        List<MedicineInfoEntity> list = datas.getData();
                        for (MedicineInfoEntity info : list) {
                            DictionaryBean bean = new DictionaryBean();
                            if (StringUtils.equals("Med", info.getParentId())) {
                                medicineList.add(info);
                            } else {
                                bean.setParentId("MedUnit");
                                bean.setName(info.getName());
                                medicineUnitDictList.add(bean);
                            }
                        }
                        if (medicineList != null && medicineList.size() > 0) {
                            if (selectedMedicine == null) {
                                selectedMedicine = medicineList.get(0);
                                tvMedicine.setText(selectedMedicine.getName());
                            } else {
                                // selectedMedicine 赋值，带id
                                for (MedicineInfoEntity medicine : medicineList) {
                                    if (StringUtils.equals(tvMedicine.getText().toString(), medicine.getName())) {
                                        selectedMedicine = medicine;
                                        break;
                                    }
                                }
                            }
                        }
                        if (medicineUnitDictList != null && medicineUnitDictList.size() > 0) {
                            if (selectedMedicineUnit == null) {
                                selectedMedicineUnit = medicineUnitDictList.get(0);
                                tvMedicineUnit.setText(selectedMedicineUnit.getName());
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
}
