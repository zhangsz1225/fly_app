package com.taikesoft.fly.ui.nurse;

import android.content.Intent;
import android.text.TextUtils;
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
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.LineBreakLayout;
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 水果点心
 */
public class HaveSnackActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.lbl_snack)
    LineBreakLayout lbl_snack;
    @BindView(R.id.lbl_fruit)
    LineBreakLayout lbl_fruit;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    private List<DictionaryBean> snackList = new ArrayList<>();
    private List<DictionaryBean> fruitList = new ArrayList<>();
    private List<DictionaryBean> selectedSnackLables;
    private List<DictionaryBean> selectedFruitLables;

    private NurseItemBean mNurseItemBean;
    private List<ChildInfoEntity> mChildren = new ArrayList<>();
    private SearchHeaderAdapter searchHeaderAdapter;
    private CommonUtil mCommonUtil;
    private int width;
    private static final int RESULT_RECODE = 0;
    private String mSpiritType = "好";

    @Override
    public void setView() {
        setContentView(R.layout.activity_have_snack);
        //绑定控件
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
        rbOk.setChecked(true);
        String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        tvNurseDate.setText(dateStr.substring(0, 10));
        tvNurse.setText(SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
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
    public void setListener() {
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
                intent.putExtra("mTagId", "refreshments");
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
            case R.id.btn_save:
                if (mChildren.size() == 0) {
                    AppContext.showToast("请添加儿童");
                } else {
                    if (selectedFruitLables.size() == 0 && selectedSnackLables.size() == 0) {
                        AppContext.showToast("请选择点心或水果");
                    } else {
                        String createTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                        String content = null;
                        String mark = null;
                        List<String> fruitLables = new ArrayList<>();
                        for (DictionaryBean fruit : selectedFruitLables) {
                            fruitLables.add(fruit.getName());
                        }
                        List<String> snackLables = new ArrayList<>();
                        for (DictionaryBean snack : selectedSnackLables) {
                            snackLables.add(snack.getName());
                        }
                        if(selectedFruitLables.size() > 0){
                            mark = "水果";
                            content = TextUtils.join(",", fruitLables);
                        }
                        if(selectedSnackLables.size() > 0){
                            if(content != null){
                                mark = "水果_SPLIT_点心";
                                content += "_SPLIT_" + TextUtils.join(",", snackLables);
                            }else{
                                mark = "点心";
                                content = TextUtils.join(",", snackLables);
                            }
                        }
                        HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, mNurseItemBean, createTime, content, null, null, null, mSpiritType);
                        submitNurseRecord(entity, AppConfig.INSERT_NURSE_NOTE);
                    }
                }
                break;
        }
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
        MainApi.requestCommon(this, AppConfig.LIST_REFRESHMENTS_TYPES, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_REFRESHMENTS_TYPES + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<DictionaryBean>>() {
                    }.getType();
                    BaseListData<DictionaryBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        ArrayList<DictionaryBean> allLables = datas.getData();
                        for (DictionaryBean bean : allLables) {
                            if (StringUtils.equals("Fruit", bean.getParentId())) {
                                if(!StringUtils.equals("无", bean.getName())){
                                    fruitList.add(bean);
                                }
                            }else{
                                if (StringUtils.equals("Dessert", bean.getParentId())) {
                                    if(!StringUtils.equals("无", bean.getName())){
                                        snackList.add(bean);
                                    }
                                }
                            }
                        }
                        //设置标签
                        lbl_snack.setLables(snackList, true);
                        //获取选中的标签
                        selectedSnackLables = lbl_snack.getSelectedLables();
                        //设置标签
                        lbl_fruit.setLables(fruitList, true);
                        //获取选中的标签
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
                    TLog.log(url + "-->" + httpResult);
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
