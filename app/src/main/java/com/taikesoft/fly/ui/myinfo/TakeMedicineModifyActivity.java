package com.taikesoft.fly.ui.myinfo;

import android.content.Intent;
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
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.picker.SinglePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;
import com.taikesoft.fly.ui.nurse.entity.MedicineInfoEntity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 服药
 */
public class TakeMedicineModifyActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.tv_medicine)
    TextView tvMedicine;
    @BindView(R.id.tv_medicine_unit)
    TextView tvMedicineUnit;
    @BindView(R.id.et_dosage)
    EditText etDosage;
    @BindView(R.id.ll_nurse)
    LinearLayout ll_nurse;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    @BindView(R.id.btn_del)
    Button mBtnDel;
    private static final int RESULT_RECODE = 0;
    private List<MedicineInfoEntity> medicineList = new ArrayList<>();
    private List<DictionaryBean> medicineUnitDictList = new ArrayList<>();
    private MedicineInfoEntity selectedMedicine;
    private DictionaryBean selectedMedicineUnit;
    private List<MedicineInfoEntity> mMedicineData;
    public static TakeMedicineModifyActivity mTakeActivity;
    private CommonUtil mCommonUtil;
    private RecordBean mRecordBean;
    private String mSpiritType;

    @Override
    public void setView() {
        setContentView(R.layout.activity_take_medicine_modify);
        ButterKnife.bind(this);
        mTakeActivity = this;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void initView() {
        setTitle(AppContext.mResource.getString(R.string.take_record_detail_title));
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
        }
        tvNurse.setText(mRecordBean.getNurses());
        tvNurseType.setText(tvNurseTypeContent);
        tvChildName.setText(mRecordBean.getChildName());
        mMedicineData = (List<MedicineInfoEntity>) getIntent().getSerializableExtra("mMedicineData");
        //会被后面的initWebData覆盖，故initWebData做判断
        if (mMedicineData != null && mMedicineData.size() > 0) {
            selectedMedicine = mMedicineData.get(mMedicineData.size() - 1);
            tvMedicine.setText(selectedMedicine.getName());
        } else {
            tvMedicine.setText(mRecordBean.getContent());
        }
        selectedMedicineUnit = (DictionaryBean) getIntent().getSerializableExtra("mMedicineUnit");
        if (selectedMedicineUnit != null) {
            tvMedicineUnit.setText(selectedMedicineUnit.getName());
        } else {
            selectedMedicineUnit = new DictionaryBean();
            selectedMedicineUnit.setName(mRecordBean.getNurseValueUnit());
            selectedMedicineUnit.setParentId("MedUnit");
            tvMedicineUnit.setText(mRecordBean.getNurseValueUnit());
        }
        String mDosage = getIntent().getStringExtra("mDosage");
        if (mDosage != null) {
            etDosage.setText(mDosage);
        } else {
            etDosage.setText(mRecordBean.getNurseValue());
        }

        tvMedicineUnit.setOnClickListener(this);
        tvMedicine.setOnClickListener(this);
        etDosage.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mCommonUtil = CommonUtil.getInstance();
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
            case R.id.tv_medicine:
                Intent intentMedicine = new Intent(this, ChooseMedicineModifyActivity.class);
                intentMedicine.putExtra("mTagId", "medicine");
                //修改后
                intentMedicine.putExtra("mMedicine", selectedMedicine);
                //原有值
                intentMedicine.putExtra("mRecordBean", mRecordBean);
                //修改后
                intentMedicine.putExtra("mDosage", etDosage.getText().toString());
                //修改后
                intentMedicine.putExtra("mMedicineUnit", selectedMedicineUnit);
                startActivityForResult(intentMedicine, RESULT_RECODE);
                break;
            case R.id.tv_medicine_unit:
                onSinglePicker(tvMedicineUnit);
                break;
            case R.id.btn_del:
                HttpEntity delHttpEntity = mCommonUtil.fillDelHttpEntity(mRecordBean.getId());
                submitNurseRecord(AppConfig.DEL_NURSE_NOTE, delHttpEntity, 1);
                break;
            case R.id.btn_save:
                if (StringUtils.isEmpty(tvMedicine.getText().toString())) {
                    AppContext.showToast("请选择药品");
                } else if (StringUtils.isEmpty(tvMedicineUnit.getText().toString())) {
                    AppContext.showToast("请选择单位");
                } else if (StringUtils.isEmpty(etDosage.getText().toString())) {
                    AppContext.showToast("请输入剂量");
                } else {
                    HttpEntity entity = mCommonUtil.fillModifyHttpEntity(mRecordBean.getId(), null, selectedMedicine.getName(), new BigDecimal(etDosage.getText().toString()), null, mSpiritType);
                    submitNurseRecord(AppConfig.UPDATE_NURSE_NOTE, entity, 0);
                }
                break;
        }

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
        if (resultCode == 2) {
            if (requestCode == RESULT_RECODE) {
                mMedicineData = (ArrayList<MedicineInfoEntity>) data.getSerializableExtra("mMedicineData");
                if (mMedicineData == null) {
                    mMedicineData = new ArrayList<>();
                }
            }
        }
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
                                bean.setParentId("Med");
                                bean.setName(info.getName());
                                medicineUnitDictList.add(bean);
                            }
                        }
                        if (medicineList != null && medicineList.size() > 0) {
                            if (StringUtils.isEmpty(tvMedicine.getText().toString())) {
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
                            if (StringUtils.isEmpty(tvMedicineUnit.getText().toString())) {
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
