package com.taikesoft.fly.ui.myinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.CommonUtil;
import com.taikesoft.fly.business.common.utils.ConvertUtils;
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.DefineBAGRefreshWithLoadView;
import com.taikesoft.fly.business.common.view.RecyclerViewDivider;
import com.taikesoft.fly.business.common.view.SingleDialog;
import com.taikesoft.fly.business.common.view.picker.DatePicker;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.entity.SelectItemEntity;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.adapter.RecordAdapter;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;
import com.taikesoft.fly.ui.myinfo.bean.RecordPageBean;
import com.taikesoft.fly.ui.myinfo.bean.SelectBean;
import com.taikesoft.fly.ui.myinfo.bean.SelectParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 护理记录查询
 */

public class NurseRecordActivity extends BaseActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private EditText mEtName;
    private TextView mTvSelectType;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private TextView tvLoad;
    private RecordAdapter mAdapter;
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;
    private RelativeLayout mRlBack;
    private SingleDialog mSingleDialog;
    private CommonUtil mCommonUtil;
    private List<RecordBean> mRecordInfos = new ArrayList<>();
    private ArrayList<NurseItemBean> mAllItems;
    private SelectItemEntity mSelectNurseType;
    private ChildInfoEntity mChild;
    private int currentPage = 0;
    private int totalPage;
    private String mMode = "";

    @Override
    public void setView() {
        setContentView(R.layout.activity_nurse_record);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void setBack() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.nurse_record_title));
        mRlBack = findViewById(R.id.rl_back);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerview);
        rlNoData = findViewById(R.id.rl_no_data);
        ivNoData = findViewById(R.id.iv_no_data);
        tvNoData = findViewById(R.id.tv_no_data);
        tvLoad = findViewById(R.id.tv_again_loading);
        tvLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWebData();
            }
        });
        initNoDataView();
        initBGARefreshLayout();
        mEtName = findViewById(R.id.et_please_name);
        LinearLayout llSelectEqu = findViewById(R.id.ll_select_nurse_type);
        mTvSelectType = findViewById(R.id.tv_selected_nurse_type);
        LinearLayout llStartTime = findViewById(R.id.ll_select_start_time);
        mTvStartTime = findViewById(R.id.tv_selected_start_time);
        LinearLayout llEndTime = findViewById(R.id.ll_select_end_time);
        mTvEndTime = findViewById(R.id.tv_selected_end_time);
        LinearLayout llFilter = findViewById(R.id.ll_filter);
        llSelectEqu.setOnClickListener(this);
        llStartTime.setOnClickListener(this);
        llEndTime.setOnClickListener(this);
        llFilter.setOnClickListener(this);

        //初始化开始结束时间
        mCommonUtil = CommonUtil.getInstance();
        mTvStartTime.setText(mCommonUtil.initTime());
        mTvEndTime.setText(mCommonUtil.initTime());
        //
        mChild = (ChildInfoEntity) getIntent().getSerializableExtra("child");
        if (mChild != null) {
            mEtName.setText(mChild.getName());
        }
        refreshWebData();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(AppContext.mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mAdapter = new RecordAdapter(this, mRecordInfos);
        View view = LayoutInflater.from(NurseRecordActivity.this).inflate(R.layout.layout_nurse_record_header, null);
        mAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(mAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.txt_gray_divider)));
        mAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                RecordBean record = mRecordInfos.get(position);
                if (StringUtils.equals("服药", record.getNurseItem())) {
                    Intent intent = new Intent(NurseRecordActivity.this, TakeMedicineModifyActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                } else if (StringUtils.equals("更换", record.getNurseItem())) {
                    Intent intent = new Intent(NurseRecordActivity.this, ChangeClothesModifyActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                } else if (StringUtils.equals("体温", record.getNurseItem())) {
                    Intent intent = new Intent(NurseRecordActivity.this, MeasureTemperatureModifyActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                } else if (StringUtils.equals("喝水喂奶", record.getNurseItem())) {
                    Intent intent = new Intent(NurseRecordActivity.this, DrinkWaterModifyActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                } else if (StringUtils.equals("水果点心", record.getNurseItem())) {
                    Intent intent = new Intent(NurseRecordActivity.this, HaveSnackModifyActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                } else if (StringUtils.equals("巡夜", record.getNurseItem())) {
                    Intent intent = new Intent(NurseRecordActivity.this, NightPatrolModifyActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(NurseRecordActivity.this, NoNeedFillInDelActivity.class);
                    intent.putExtra("mRecordBean", record);
                    startActivity(intent);
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Calendar selectedDate;
        switch (id) {
            //选择类别
            case R.id.ll_select_nurse_type:
                mSingleDialog = new SingleDialog(this, mSelectNurseType, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectItemEntity entity) {
                        nurseItemChoose(entity);
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
            case R.id.ll_select_start_time:
                mMode = "start";
                String startTime = mTvStartTime.getText().toString();
                selectedDate = mCommonUtil.initCustomeTimer(startTime);
                onYearMonthDayPicker(selectedDate.getTime());
                break;
            case R.id.ll_select_end_time:
                mMode = "end";
                String endTime = mTvEndTime.getText().toString();
                selectedDate = mCommonUtil.initCustomeTimer(endTime);
                onYearMonthDayPicker(selectedDate.getTime());
                break;
            case R.id.ll_filter:
                refreshWebData();
                break;
        }
    }

    private void nurseItemChoose(SelectItemEntity entity) {
        mSelectNurseType = entity;
        mTvSelectType.setText(entity.getNurseItem() + (entity.getNurseTime() == null ? "" : "," + entity.getNurseTime()));
        refreshWebData();
    }

    @Override
    protected void initWebData() {
        initLoading("");
        //获取护理类别
        requestNurseItems();
    }

    private void requestNurseItems() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            obj.put("timeType", "全部");
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.LIST_NURSE_ITEM, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_NURSE_ITEM + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<NurseItemBean>>() {
                    }.getType();
                    BaseListData<NurseItemBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        mAllItems = datas.getData();
                        for (NurseItemBean bean : mAllItems) {
                            if (StringUtils.equals("巡夜", bean.getNurseItem())) {
                                bean.setId(bean.getId() + "_" + bean.getNurseTime());
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

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(AppContext.mResource.getString(R.string.operate_record_no_data));
        tvLoad.setVisibility(View.GONE);
    }

    //网络异常
    private void initNoInternetView() {
        if (ivNoData == null) return;
        ivNoData.setImageResource(R.drawable.no_internet_icon);
        tvNoData.setText(AppContext.mResource.getString(R.string.please_check_network));
        tvLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshWebData() {

        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();

        currentPage = 1;
        requestNurseRecord();
    }

    private void requestNurseRecord() {
        SelectParams param = new SelectParams();
        if (mSelectNurseType != null) {
            String id = mSelectNurseType.getId();
            if (StringUtils.equals("巡夜", mSelectNurseType.getNurseItem())) {
                param.setItemId(id.split("_")[0]);
            } else {
                param.setItemId(id);
            }
        }
        if (!StringUtils.isEmpty(mEtName.getText().toString())) {
            param.setChildName(mEtName.getText().toString());
        }
        param.setStartTime(mTvStartTime.getText().toString() + " 00:00:00");
        param.setEndTime(mTvEndTime.getText().toString() + " 23:59:59");
        if (mChild != null) {
            param.setChildId(mChild.getId());
        }
        SelectBean operation = new SelectBean();
        operation.setPageNo(currentPage);
        operation.setPageSize(10);
        operation.setParams(param);
        //请求护理记录
        HttpEntity entity = null;
        try {
            String params = new Gson().toJson(operation);
            entity = new StringEntity(params, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this, AppConfig.PAGE_NURSE_RECORD, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.PAGE_NURSE_RECORD + "-->" + httpResult);
                    Type type = new TypeToken<BaseData<RecordPageBean>>() {
                    }.getType();
                    BaseData<RecordPageBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        List<RecordBean> results = datas.getData().results;
                        if (datas.getData() != null) {
                            totalPage = datas.getData().totalPage;
                            if (totalPage > 0) {
                                if (results != null && results.size() != 0) {
                                    if (mRecyclerView != null && mRecyclerView.getVisibility() != View.VISIBLE) {
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                    }
                                    if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                        rlNoData.setVisibility(View.GONE);
                                    }
                                    if (currentPage == 1) {
                                        mRecordInfos.clear();
                                    }
                                    mRecordInfos.addAll(results);
                                    mAdapter.notifyDataSetChanged();
                                    if (currentPage == 1) {
                                        mRefreshLayout.endRefreshing();
                                    } else {
                                        mRefreshLayout.endLoadingMore();
                                    }
                                } else {
                                    initNoDataView();
                                    initNoData();
                                }
                            } else {
                                initNoDataView();
                                initNoData();
                            }
                        } else {
                            if (currentPage == 1) {
                                initNoDataView();
                                initNoData();
                            }
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initNoDataView();
                initNoData();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                initNoData();
                initNoInternetView();
            }
        });
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
        }
        if (currentPage == 1) {
            mRefreshLayout.endRefreshing();
        } else {
            mRefreshLayout.endLoadingMore();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //下拉加载最新数据
                    currentPage = 1;
                    requestNurseRecord();
                    break;
                case 1:
                    //上拉加载更多数据
                    currentPage++;
                    requestNurseRecord();
                    break;
                case 2:
                    mRefreshLayout.endLoadingMore();
                    break;
                default:
                    break;

            }
        }
    };

    //为了修改和删除后返回自动刷新
    @Override
    public void onResume() {
        super.onResume();
        refreshWebData();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉加载最新数据
        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        currentPage = 1;
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (currentPage >= totalPage) {
            mDefineRefreshWithLoadView.updateLoadingMoreText("没有更多数据啦");
            mDefineRefreshWithLoadView.hideLoadingMoreImg();
            handler.sendEmptyMessageDelayed(2, 1000);
            return true;
        }
        //上拉加载更多数据
        handler.sendEmptyMessageDelayed(1, 1000);
        return true;
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private List<SelectItemEntity> getInitSelecList() {
        List<SelectItemEntity> selectInfoList = new ArrayList<>();
        SelectItemEntity selectInfo;
        for (int i = 0; i < mAllItems.size(); i++) {
            NurseItemBean itemBean = mAllItems.get(i);
            selectInfo = new SelectItemEntity();
            selectInfo.setId(itemBean.getId());
            selectInfo.setNurseItem(itemBean.getNurseItem());
            selectInfo.setNurseType(itemBean.getNurseType());
            selectInfo.setNurseTime(itemBean.getNurseTime());
            selectInfo.setSelect(false);
            selectInfoList.add(selectInfo);
        }
        return selectInfoList;
    }

    public void onYearMonthDayPicker(Date selectedDate) {
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(2100, 1, 11);
        picker.setRangeStart(2019, 10, 29);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        int month = calendar.get(Calendar.MONTH);
        picker.setSelectedItem(calendar.get(Calendar.YEAR), (month + 1), calendar.get(Calendar.DAY_OF_MONTH));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                Date date = DateUtils.parseDate(year + "-" + month + "-" + day, "yyyy-MM-dd");
                DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                String time;
                if (StringUtils.equals(mMode, "start")) {
                    time = mTvEndTime.getText().toString();
                } else {
                    time = mTvStartTime.getText().toString();
                }
                Date date2 = null;
                try {
                    date2 = fmt.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean startAfter = date2.after(date);
                if (StringUtils.equals(mMode, "start")) {
                    if (!startAfter) {
                        mTvEndTime.setText(getTime(date));
                    }
                } else {
                    if (startAfter) {
                        mTvStartTime.setText(getTime(date));
                    }
                }
                if (StringUtils.equals(mMode, "start")) {
                    mTvStartTime.setText(getTime(date));
                } else if (StringUtils.equals(mMode, "end")) {
                    mTvEndTime.setText(getTime(date));
                }
                refreshWebData();
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
