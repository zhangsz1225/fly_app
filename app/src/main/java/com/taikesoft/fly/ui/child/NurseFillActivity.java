package com.taikesoft.fly.ui.child;

import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.taikesoft.fly.business.common.view.LineBreakLayout;
import com.taikesoft.fly.business.common.view.SingleTaskDialog;
import com.taikesoft.fly.business.common.view.picker.NumberPicker;
import com.taikesoft.fly.business.common.view.picker.SinglePicker;
import com.taikesoft.fly.business.common.view.picker.TimePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.child.bean.NurseTaskBean;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.adapter.SearchHeaderAdapter;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;
import com.taikesoft.fly.ui.nurse.entity.MedicineInfoEntity;
import com.taikesoft.fly.ui.nurse.entity.SelectTaskEntity;

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
 * 儿童主页，选儿童后，统一的护理填报页
 */

public class NurseFillActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_add_icon)
    ImageView mIvAddIcon;
    @BindView(R.id.recyclerview_header)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_add_child)
    TextView mTvAdd;
    @BindView(R.id.tv_selected_nurse_type)
    TextView mTvSelectType;
    @BindView(R.id.rbOk)
    RadioButton rbOk;
    @BindView(R.id.rbNot)
    RadioButton rbNot;
    @BindView(R.id.tv_nurse_date)
    TextView tvNurseDate;
    @BindView(R.id.ll_clothes)
    LinearLayout ll_clothes;
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
    private ArrayList<DictionaryBean> mTypes;
    @BindView(R.id.ll_temperature)
    LinearLayout ll_temperature;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_measure_time)
    TextView tvMeasureTime;
    @BindView(R.id.rl_snack)
    RelativeLayout rl_snack;
    @BindView(R.id.ll_snack)
    LinearLayout ll_snack;
    @BindView(R.id.lbl_snack)
    LineBreakLayout lbl_snack;
    @BindView(R.id.rl_fruit)
    RelativeLayout rl_fruit;
    @BindView(R.id.ll_fruit)
    LinearLayout ll_fruit;
    @BindView(R.id.lbl_fruit)
    LineBreakLayout lbl_fruit;
    @BindView(R.id.ll_medicine)
    LinearLayout ll_medicine;
    @BindView(R.id.tv_medicine)
    TextView tvMedicine;
    @BindView(R.id.tv_medicine_unit)
    TextView tvMedicineUnit;
    @BindView(R.id.et_dosage)
    EditText etDosage;
    @BindView(R.id.ll_water)
    LinearLayout ll_water;
    @BindView(R.id.rbWater)
    RadioButton rbWater;
    @BindView(R.id.rbMilk)
    RadioButton rbMilk;
    @BindView(R.id.tv_consumption)
    TextView tvConsumption;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    private SingleTaskDialog mSingleDialog;
    private SearchHeaderAdapter searchHeaderAdapter;
    private CommonUtil mCommonUtil;
    private List<DictionaryBean> snackList = new ArrayList<>();
    private List<DictionaryBean> fruitList = new ArrayList<>();
    private List<DictionaryBean> selectedSnackLables = new ArrayList<>();
    private List<DictionaryBean> selectedFruitLables = new ArrayList<>();
    private List<DictionaryBean> clothesTypes;
    private ArrayList<NurseTaskBean> mAllItems;
    private List<MedicineInfoEntity> mMedicineData;
    private List<MedicineInfoEntity> medicineList = new ArrayList<>();
    private List<DictionaryBean> medicineUnitDictList = new ArrayList<>();
    private List<ChildInfoEntity> mChildren = new ArrayList<>();
    private SelectTaskEntity mSelectTaskEntity;
    private DictionaryBean selectedClothesType;
    private MedicineInfoEntity selectedMedicine;
    private DictionaryBean selectedMedicineUnit;
    private static final int RESULT_RECODE = 0;
    private int width;
    private int amount = 1;
    private String mSpiritType = "好";
    private String mDrinkType = "喝水";
    public static NurseFillActivity mFillActivity;

    @Override
    public void setView() {
        setContentView(R.layout.activity_nurse_fill);
        ButterKnife.bind(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        mFillActivity = this;
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void initView() {
        mChildren = (List<ChildInfoEntity>) getIntent().getSerializableExtra("mChildData");
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        //服药选择时记录下儿童状态
        if (mSpiritType != null) {
            if (StringUtils.equals("好", mSpiritType)) {
                rbOk.setChecked(true);
            } else {
                rbNot.setChecked(true);
            }
        } else {
            rbOk.setChecked(true);
        }
        setTitle(AppContext.mContext.getString(R.string.nurse_record_detail_title));
        //设置默认时间
        String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        tvNurseDate.setText(dateStr.substring(0, 10));
        tvNurse.setText(SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
        tvTimes.setText(String.valueOf(amount));
        tvConsumption.setText("100");
        rbWater.setChecked(true);
        ll_medicine.setVisibility(View.GONE);
        ll_clothes.setVisibility(View.GONE);
        rl_snack.setVisibility(View.GONE);
        rl_fruit.setVisibility(View.GONE);
        ll_snack.setVisibility(View.GONE);
        ll_fruit.setVisibility(View.GONE);
        ll_water.setVisibility(View.GONE);
        ll_temperature.setVisibility(View.GONE);
        mBtnSave.setVisibility(View.GONE);
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
        mSelectTaskEntity = (SelectTaskEntity) getIntent().getSerializableExtra("mSelectTaskEntity");
        if (mSelectTaskEntity != null) {
            mTvSelectType.setText(mSelectTaskEntity.getNurseItem() + (mSelectTaskEntity.getNurseTime() == null ? "" : "," + mSelectTaskEntity.getNurseTime()));
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_temperature.getVisibility() == View.VISIBLE) {
                ll_temperature.setVisibility(View.GONE);
            }
            if (rl_snack.getVisibility() == View.VISIBLE) {
                rl_snack.setVisibility(View.GONE);
            }
            if (rl_fruit.getVisibility() == View.VISIBLE) {
                rl_fruit.setVisibility(View.GONE);
            }
            if (ll_snack.getVisibility() == View.VISIBLE) {
                ll_snack.setVisibility(View.GONE);
            }
            if (ll_fruit.getVisibility() == View.VISIBLE) {
                ll_fruit.setVisibility(View.GONE);
            }
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_clothes.getVisibility() == View.VISIBLE) {
                ll_clothes.setVisibility(View.GONE);
            }
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
            ll_medicine.setVisibility(View.VISIBLE);

            List<MedicineInfoEntity> medicineData = (List<MedicineInfoEntity>) getIntent().getSerializableExtra("mMedicineData");
            if (medicineData != null && medicineData.size() > 0) {
                selectedMedicine = medicineData.get(medicineData.size() - 1);
            }
            selectedMedicineUnit = (DictionaryBean) getIntent().getSerializableExtra("mMedicineUnit");
            etDosage.setText(getIntent().getStringExtra("mDosage"));
            tvMedicine.setText(selectedMedicine.getName());
            tvMedicineUnit.setText(selectedMedicineUnit.getName());
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
            mBtnSave.setVisibility(View.VISIBLE);
        }
        mIvAddIcon.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
        rbOk.setOnClickListener(this);
        rbNot.setOnClickListener(this);
        mTvSelectType.setOnClickListener(this);
        tvClothes.setOnClickListener(this);
        tvChangeTime.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
        mBtnMinus.setOnClickListener(this);
        rbWater.setOnClickListener(this);
        rbMilk.setOnClickListener(this);
        tvConsumption.setOnClickListener(this);
        tvTemperature.setOnClickListener(this);
        tvMeasureTime.setOnClickListener(this);
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

    public void onConsumptionPicker() {
        NumberPicker picker = new NumberPicker(this);
        picker.setWidth(picker.getScreenWidthPixels());
        picker.setCycleDisable(false);
        picker.setAnimationStyle(R.style.popupwindow_anim_style);
        picker.setDividerVisible(false);
        picker.setOffset(3);//偏移量
        picker.setRange(50, 500, 10);//数字范围
        picker.setSelectedItem(100);
        picker.setLabel("ml");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                tvConsumption.setText(String.valueOf(item.intValue()));
            }
        });
        picker.show();
    }

    public void onSingleUnitPicker(View view, TextView tv) {
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
                // AppContext.showToast("index=" + index + ", id=" + item.getId() + ", name=" + item.getName());
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
            case R.id.rbOk:
                rbOk.setChecked(true);
                mSpiritType = "好";
                break;
            case R.id.rbNot:
                rbNot.setChecked(true);
                mSpiritType = "不好";
                break;
            case R.id.rbWater:
                rbWater.setChecked(true);
                mDrinkType = "喝水";
                break;
            case R.id.rbMilk:
                rbMilk.setChecked(true);
                mDrinkType = "喝奶";
                break;
            case R.id.tv_selected_nurse_type:
                mSingleDialog = new SingleTaskDialog(this, mSelectTaskEntity, new SingleTaskDialog.DataBackListener() {
                    @Override
                    public void getData(SelectTaskEntity entity) {
                        nurseItemFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_task));
                if (mAllItems != null && mAllItems.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
            case R.id.tv_change_time:
                onTimePicker(tvChangeTime);
                break;
            case R.id.tv_clothes:
                onSinglePicker(tvClothes, clothesTypes);
                break;
            case R.id.tv_medicine:
                Intent intentMedicine = new Intent(this, ChooseChildMedicineActivity.class);
                intentMedicine.putExtra("mTagId", "nurseFill");
                intentMedicine.putExtra("mChildData", (Serializable) mChildren);
                intentMedicine.putExtra("mMedicine", selectedMedicine);
                intentMedicine.putExtra("mSelectTaskEntity", mSelectTaskEntity);
                intentMedicine.putExtra("mDosage", etDosage.getText().toString());
                intentMedicine.putExtra("mMedicineUnit", selectedMedicineUnit);
                startActivityForResult(intentMedicine, RESULT_RECODE);
                break;
            case R.id.tv_medicine_unit:
                onSingleUnitPicker(v, tvMedicineUnit);
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
            case R.id.iv_add_icon:
                //重选
                finish();
                break;
            case R.id.tv_add_child:
                //重选
                finish();
                break;
            case R.id.tv_measure_time:
                onTimePicker(tvMeasureTime);
                break;
            case R.id.tv_temperature:
                onTemperaturePicker(tvTemperature);
                break;
            case R.id.tv_consumption:
                onConsumptionPicker();
                break;
            case R.id.btn_save:
                NurseItemBean bean = new NurseItemBean();
                bean.setNurseItem(mSelectTaskEntity.getNurseItem());
                bean.setId(mSelectTaskEntity.getItemId());
                bean.setNurseType(mSelectTaskEntity.getNurseType());
                bean.setNurseTime(mSelectTaskEntity.getNurseTime());
                bean.setTimeType(mSelectTaskEntity.getTimeType());
                bean.setNurseBeginTime(mSelectTaskEntity.getNurseBeginTime());
                bean.setNurseEndTime(mSelectTaskEntity.getNurseEndTime());
                for (ChildInfoEntity child : mChildren) {
                    child.setTaskId(mSelectTaskEntity.getId());
                }
                if (StringUtils.equals("喝水喂奶", mSelectTaskEntity.getNurseItem())) {
                    String createTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                    HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, bean, createTime, mDrinkType, new BigDecimal(tvConsumption.getText().toString()), "ml", null, mSpiritType);
                    submitNurseRecord(entity);

                } else if (StringUtils.equals("服药", mSelectTaskEntity.getNurseItem())) {
                    String createTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                    HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, bean, createTime, tvMedicine.getText().toString(), new BigDecimal(etDosage.getText().toString()), tvMedicineUnit.getText().toString(), null, mSpiritType);
                    submitNurseRecord(entity);
                } else if (StringUtils.equals("更换", mSelectTaskEntity.getNurseItem())) {
                    String changeTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " " + tvChangeTime.getText().toString().trim() + ":00";
                    HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, bean, changeTime, selectedClothesType.getName(), new BigDecimal(tvTimes.getText().toString()), null, null, mSpiritType);
                    submitNurseRecord(entity);
                } else if (StringUtils.equals("体温", mSelectTaskEntity.getNurseItem())) {
                    String measureTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " " + tvMeasureTime.getText().toString().trim() + ":00";
                    HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, bean, measureTime, bean.getNurseItem(), new BigDecimal(tvTemperature.getText().toString()), "℃", null, mSpiritType);
                    submitNurseRecord(entity);
                } else if (StringUtils.equals("水果点心", mSelectTaskEntity.getNurseItem())) {
                    if (selectedSnackLables.size() == 0 || selectedFruitLables.size() == 0) {
                        AppContext.showToast("请选择水果或点心");
                    } else {
                        String createTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                        String content = null;
                        List<String> fruitLables = new ArrayList<>();
                        for (DictionaryBean fruit : selectedFruitLables) {
                            fruitLables.add(fruit.getName());
                        }
                        List<String> snackLables = new ArrayList<>();
                        for (DictionaryBean snack : selectedSnackLables) {
                            snackLables.add(snack.getName());
                        }
                        if (selectedFruitLables.size() > 0) {
                            content = TextUtils.join(",", fruitLables);
                        }
                        if (selectedSnackLables.size() > 0) {
                            if (content != null) {
                                content += "_SPLIT_" + TextUtils.join(",", snackLables);
                            } else {
                                content = TextUtils.join(",", snackLables);
                            }
                        }
                        HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, bean, createTime, content, null, null, null, mSpiritType);
                        submitNurseRecord(entity);
                    }
                } else {
                    HttpEntity entity = mCommonUtil.noNeedfillHttpEntity(mChildren, bean, mSpiritType);
                    submitNurseRecord(entity);
                }
                break;
        }

    }

    public void onTemperaturePicker(TextView tv) {
        NumberPicker picker = new NumberPicker(this);
        picker.setWidth(picker.getScreenWidthPixels());
        picker.setCycleDisable(false);
        picker.setAnimationStyle(R.style.popupwindow_anim_style);
        picker.setDividerVisible(false);
        picker.setOffset(3);//偏移量
        picker.setRange(33.0, 42.0, 0.1);//数字范围
        picker.setSelectedItem(36.8);
        picker.setLabel("℃");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                tv.setText(String.valueOf(item.floatValue()));
            }
        });
        picker.show();
    }

    private void nurseItemFinish(SelectTaskEntity entity) {
        mSelectTaskEntity = entity;
        mTvSelectType.setText(entity.getNurseItem() + (entity.getNurseTime() == null ? "" : "," + entity.getNurseTime()));
        setTitle(entity.getNurseItem());
        showLinearLayout(entity);
    }

    public void onTimePicker(TextView tvTime) {
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
                tvTime.setText(hour + ":" + minute);
            }
        });
        picker.show();
    }

    public void showLinearLayout(SelectTaskEntity entity) {
        if (StringUtils.equals("喝水喂奶", entity.getNurseItem())) {
            if (ll_temperature.getVisibility() == View.VISIBLE) {
                ll_temperature.setVisibility(View.GONE);
            }
            if (ll_snack.getVisibility() == View.VISIBLE) {
                ll_snack.setVisibility(View.GONE);
            }
            if (ll_fruit.getVisibility() == View.VISIBLE) {
                ll_fruit.setVisibility(View.GONE);
            }
            if (rl_snack.getVisibility() == View.VISIBLE) {
                rl_snack.setVisibility(View.GONE);
            }
            if (rl_fruit.getVisibility() == View.VISIBLE) {
                rl_fruit.setVisibility(View.GONE);
            }
            if (ll_clothes.getVisibility() == View.VISIBLE) {
                ll_clothes.setVisibility(View.GONE);
            }
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
            if (ll_medicine.getVisibility() == View.VISIBLE) {
                ll_medicine.setVisibility(View.GONE);
            }
            ll_water.setVisibility(View.VISIBLE);
        } else if (StringUtils.equals("服药", entity.getNurseItem())) {
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_temperature.getVisibility() == View.VISIBLE) {
                ll_temperature.setVisibility(View.GONE);
            }
            if (ll_snack.getVisibility() == View.VISIBLE) {
                ll_snack.setVisibility(View.GONE);
            }
            if (ll_fruit.getVisibility() == View.VISIBLE) {
                ll_fruit.setVisibility(View.GONE);
            }
            if (rl_snack.getVisibility() == View.VISIBLE) {
                rl_snack.setVisibility(View.GONE);
            }
            if (rl_fruit.getVisibility() == View.VISIBLE) {
                rl_fruit.setVisibility(View.GONE);
            }
            if (ll_clothes.getVisibility() == View.VISIBLE) {
                ll_clothes.setVisibility(View.GONE);
            }
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
            ll_medicine.setVisibility(View.VISIBLE);
        } else if (StringUtils.equals("更换", entity.getNurseItem())) {
            if (ll_medicine.getVisibility() == View.VISIBLE) {
                ll_medicine.setVisibility(View.GONE);
            }
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_temperature.getVisibility() == View.VISIBLE) {
                ll_temperature.setVisibility(View.GONE);
            }
            if (ll_snack.getVisibility() == View.VISIBLE) {
                ll_snack.setVisibility(View.GONE);
            }
            if (ll_fruit.getVisibility() == View.VISIBLE) {
                ll_fruit.setVisibility(View.GONE);
            }
            if (rl_snack.getVisibility() == View.VISIBLE) {
                rl_snack.setVisibility(View.GONE);
            }
            if (rl_fruit.getVisibility() == View.VISIBLE) {
                rl_fruit.setVisibility(View.GONE);
            }
            ll_clothes.setVisibility(View.VISIBLE);
            String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            tvChangeTime.setText(dateStr.substring(11, 16));
            tvTimes.setText("1");
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
        } else if (StringUtils.equals("体温", entity.getNurseItem())) {
            if (ll_medicine.getVisibility() == View.VISIBLE) {
                ll_medicine.setVisibility(View.GONE);
            }
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_clothes.getVisibility() == View.VISIBLE) {
                ll_clothes.setVisibility(View.GONE);
            }
            if (ll_snack.getVisibility() == View.VISIBLE) {
                ll_snack.setVisibility(View.GONE);
            }
            if (ll_fruit.getVisibility() == View.VISIBLE) {
                ll_fruit.setVisibility(View.GONE);
            }
            if (rl_snack.getVisibility() == View.VISIBLE) {
                rl_snack.setVisibility(View.GONE);
            }
            if (rl_fruit.getVisibility() == View.VISIBLE) {
                rl_fruit.setVisibility(View.GONE);
            }
            ll_temperature.setVisibility(View.VISIBLE);
            String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            tvMeasureTime.setText(dateStr.substring(11, 16));
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
            tvTemperature.setText("36.8");
        } else if (StringUtils.equals("水果点心", entity.getNurseItem())) {
            if (ll_medicine.getVisibility() == View.VISIBLE) {
                ll_medicine.setVisibility(View.GONE);
            }
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_clothes.getVisibility() == View.VISIBLE) {
                ll_clothes.setVisibility(View.GONE);
            }
            if (ll_temperature.getVisibility() == View.VISIBLE) {
                ll_temperature.setVisibility(View.GONE);
            }
            ll_snack.setVisibility(View.VISIBLE);
            ll_fruit.setVisibility(View.VISIBLE);
            rl_snack.setVisibility(View.VISIBLE);
            rl_fruit.setVisibility(View.VISIBLE);
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
        } else {
            if (ll_medicine.getVisibility() == View.VISIBLE) {
                ll_medicine.setVisibility(View.GONE);
            }
            if (ll_water.getVisibility() == View.VISIBLE) {
                ll_water.setVisibility(View.GONE);
            }
            if (ll_clothes.getVisibility() == View.VISIBLE) {
                ll_clothes.setVisibility(View.GONE);
            }
            if (ll_temperature.getVisibility() == View.VISIBLE) {
                ll_temperature.setVisibility(View.GONE);
            }
            if (ll_snack.getVisibility() == View.VISIBLE) {
                ll_snack.setVisibility(View.GONE);
            }
            if (ll_fruit.getVisibility() == View.VISIBLE) {
                ll_fruit.setVisibility(View.GONE);
            }
            if (rl_snack.getVisibility() == View.VISIBLE) {
                rl_snack.setVisibility(View.GONE);
            }
            if (rl_fruit.getVisibility() == View.VISIBLE) {
                rl_fruit.setVisibility(View.GONE);
            }
            if (ll.getVisibility() == View.GONE) {
                ll.setVisibility(View.VISIBLE);
            }
        }
        mBtnSave.setVisibility(View.VISIBLE);
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
        //获取护理任务
        requestNurseItems();
        requestTypes(AppConfig.LIST_TYPES);
        requestMedTypes();
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
                        ArrayList<DictionaryBean> refreshmentsTypes = new ArrayList<>();
                        for (DictionaryBean bean : mTypes) {
                            if (StringUtils.equals("ReplaceProject", bean.getParentId())) {
                                clothesTypes.add(bean);
                            } else if (StringUtils.equals("PatrolStatus", bean.getParentId())) {
                            } else {
                                if (StringUtils.equals("Fruit", bean.getParentId())) {
                                    if (!StringUtils.equals("无", bean.getName())) {
                                        fruitList.add(bean);
                                    }
                                } else {
                                    if (StringUtils.equals("Dessert", bean.getParentId())) {
                                        if (!StringUtils.equals("无", bean.getName())) {
                                            snackList.add(bean);
                                        }
                                    }
                                }
                                refreshmentsTypes.add(bean);
                            }
                        }
                        //初始化衣服类别，默认选择第一项
                        if (clothesTypes.size() > 0) {
                            selectedClothesType = clothesTypes.get(0);
                            tvClothes.setText(selectedClothesType.getName());
                        }
                        lbl_snack.setLables(snackList, true);
                        selectedSnackLables = lbl_snack.getSelectedLables();
                        lbl_fruit.setLables(fruitList, true);
                        selectedFruitLables = lbl_fruit.getSelectedLables();
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

    private void requestMedTypes() {
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
                            if (selectedMedicine == null) {
                                selectedMedicine = medicineList.get(0);
                                tvMedicine.setText(selectedMedicine.getName());
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

    private void requestNurseItems() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            obj.put("childId", mChildren.get(0).getId());
            obj.put("taskDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.LIST_NURSE_TASK, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_NURSE_TASK + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<NurseTaskBean>>() {
                    }.getType();
                    BaseListData<NurseTaskBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        mAllItems = datas.getData();
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

    private List<SelectTaskEntity> getInitSelecList() {
        List<SelectTaskEntity> selectInfoList = new ArrayList<>();
        SelectTaskEntity selectInfo;
        for (int i = 0; i < mAllItems.size(); i++) {
            NurseTaskBean itemBean = mAllItems.get(i);
            selectInfo = new SelectTaskEntity();
            selectInfo.setId(itemBean.getId());
            selectInfo.setItemId(itemBean.getItemId());
            selectInfo.setTimeType(itemBean.getTimeType());
            selectInfo.setNurseTime(itemBean.getNurseTime());
            selectInfo.setNurseType(itemBean.getNurseType());
            selectInfo.setNurseItem(itemBean.getNurseItem());
            selectInfo.setNurseBeginTime(itemBean.getNurseBeginTime());
            selectInfo.setNurseEndTime(itemBean.getNurseEndTime());
            selectInfo.setSelect(false);
            selectInfoList.add(selectInfo);
        }
        return selectInfoList;
    }

    private void submitNurseRecord(HttpEntity entity) {
        if (mChildren.size() == 0) {
            AppContext.showToast("请添加儿童");
            return;
        } else {
            MainApi.requestCommon(this, AppConfig.INSERT_NURSE_NOTE, entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                    try {
                        String httpResult = new String(bytes);
                        TLog.log(AppConfig.INSERT_NURSE_NOTE + "-->" + httpResult);
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

    }
}
