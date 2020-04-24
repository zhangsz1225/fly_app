package com.taikesoft.fly.ui.homepage.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.CommonUtil;
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.DefineBAGRefreshWithLoadView;
import com.taikesoft.fly.business.common.view.NoticeView;
import com.taikesoft.fly.business.common.view.RecyclerViewDivider;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.business.widget.lineChart.LineChartLayout;
import com.taikesoft.fly.ui.homepage.HomeNightPatrolActivity;
import com.taikesoft.fly.ui.homepage.MainActivity;
import com.taikesoft.fly.ui.homepage.adapter.HomeWarnMessageAdapter;
import com.taikesoft.fly.ui.homepage.adapter.TaskSituationAdapter;
import com.taikesoft.fly.ui.homepage.entity.ChildSituationStaticEntity;
import com.taikesoft.fly.ui.homepage.entity.ChildSituationStaticListEntity;
import com.taikesoft.fly.ui.homepage.entity.LineEntity;
import com.taikesoft.fly.ui.homepage.entity.TaskSituationEntity;
import com.taikesoft.fly.ui.myinfo.NoticeDetailActivity;
import com.taikesoft.fly.ui.myinfo.WarnmsgHistoryActivity;
import com.taikesoft.fly.ui.myinfo.bean.NoticeBean;
import com.taikesoft.fly.ui.myinfo.bean.NoticePageBean;
import com.taikesoft.fly.ui.myinfo.entity.WarnMsgEntity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 首页
 */

public class HomeNurseSituationFragment extends HomeBaseListFragment implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {
    public static final String TAG = "HomeNurseSituationFragment";
    private GridView gridView;
    private SimpleAdapter adapter;
    private boolean isHided = false;
    private boolean mHasLoadedOnce = false;
    private TextView tvNurse;
    private TextView tvWarn;
    private TextView tvWarnHistory;
    private ScrollView scrollView;
    private ListView mWarnMsgListView;
    private List<WarnMsgEntity> mWarnMsgList = new ArrayList<>();
    private HomeWarnMessageAdapter mWarnAdapter;
    //消息的
    private int curPage = 1;
    private int maxPage = 1;
    private int totalNums = 0;
    private TextView tvNotice;
    private NoticeView noticeView;
    private List<NoticeBean> mNoticeList = new ArrayList<>();
    private RelativeLayout rlTitleBar;
    private TextView mTvTitle;
    private LineChartLayout mInclude;
    private LinearLayout llParent;
    private CommonUtil mCommon;
    private ArrayList<ChildSituationStaticEntity> mChildrenSituation;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView tvUpdateTime;
    private TextView tvChildToday;
    private TextView tvTaskSta;
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;
    private TaskSituationAdapter mAdapter;
    private TextView tvChildSta;
    private TextView tvChildStaToday;
    List<TaskSituationEntity> mTaskNumInfos = new ArrayList<>();
    //护理室任务
    private int currentPage = 0;
    private int totalPage;
    //图标下的文字
    private final String name[] = {"个人卫生", "巡夜", "服药", "更换", "体温", "饮食", "养康教"};

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_nurse_situation;
    }

    @Override
    protected void initView(View view) {
        rlTitleBar = view.findViewById(R.id.rl_title_bar);
        rlTitleBar.setVisibility(View.VISIBLE);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.title_home);
        tvNotice = view.findViewById(R.id.tv_notice);
        tvNurse = view.findViewById(R.id.tv_nurse);
        Drawable drawable = AppContext.mResource.getDrawable(R.drawable.line);
        drawable.setBounds(0, 0, 45, 45);
        tvNotice.setCompoundDrawables(drawable, null, null, null);//只放左边
        tvNurse.setCompoundDrawables(drawable, null, null, null);//只放左边
        gridView = view.findViewById(R.id.gridview);
        llParent = view.findViewById(R.id.ll_parent);
        mInclude = view.findViewById(R.id.include);
        tvWarn = view.findViewById(R.id.tvWarn);
        tvWarn.setCompoundDrawables(drawable, null, null, null);
        tvWarnHistory = view.findViewById(R.id.tvWarnHistory);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        tvUpdateTime = view.findViewById(R.id.tv_update_time);
        tvTaskSta = view.findViewById(R.id.tv_task_sta);
        tvTaskSta.setCompoundDrawables(drawable, null, null, null);//只放左边
        tvTaskSta.setText(generateTaskSpannableText());
        tvChildSta = view.findViewById(R.id.tv_child_sta);
        tvChildSta.setText(generateSpannableText());
        tvChildSta.setCompoundDrawables(drawable, null, null, null);//
        tvChildStaToday = view.findViewById(R.id.tv_child_sta_today);
        tvChildStaToday.setCompoundDrawables(drawable, null, null, null);//只放左边
        tvChildToday = view.findViewById(R.id.tv_child_today);
        TextView tvMore = view.findViewById(R.id.tv_more);
        tvMore.setOnClickListener(this);
        tvWarnHistory.setOnClickListener(this);
        noticeView = view.findViewById(R.id.notice_view);
        rlNoData = view.findViewById(R.id.rl_no_data);
        ivNoData = view.findViewById(R.id.iv_no_data);
        tvNoData = view.findViewById(R.id.tv_no_data);
        mCommon = CommonUtil.getInstance();
        //图标
        int icno[] = {R.drawable.wash, R.drawable.nfc, R.drawable.pill,
                R.drawable.change, R.drawable.temperature, R.drawable.diet, R.drawable.nurse_more};

        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icno.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text", name[i]);
            dataList.add(map);
        }
        String[] from = {"img", "text"};

        int[] to = {R.id.img, R.id.text};

        //每个表格宽度:和xml的item保持一致
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.grid_view_item_height);
        //所需要展示的个数,一般接口返回，这里array表示一个数组
        int count = dataList.size();
        //这里行是关键，设置mGridview的width为itemWidth * count
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                itemWidth * count, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params);
        gridView.setColumnWidth(itemWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        //设置表格个数
        gridView.setNumColumns(count);
        //去掉默认的状态选择器
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //设置Adapter
        adapter = new SimpleAdapter(getContext(), dataList, R.layout.item_grid, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                String quickText = dataList.get(arg2).get("text").toString();
                if (StringUtils.equals(quickText, "巡夜")) {
                    Intent intent = new Intent(getContext(), HomeNightPatrolActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("quickText", quickText);
                    intent.putExtra("flag", "NurseFragmemt");
                    startActivity(intent);
                }
            }
        });
        initBGARefreshLayout();
        //获取今日护理概况
        refreshGroupSituationData();
        tvUpdateTime.setText("更新于" + DateUtils.formatDate(new Date(), "HH:mm"));
        //获取儿童状况
        requestStatics();
        //通知消息
        scrollView = view.findViewById(R.id.slView);
        mWarnMsgListView = view.findViewById(R.id.lv_content);
        mWarnAdapter = new HomeWarnMessageAdapter();
        mWarnAdapter.setData(mWarnMsgList);
        mWarnMsgListView.setAdapter(mWarnAdapter);
        initNoDataView();
        getWarnMessageList(1);
        mWarnAdapter.setOnIDoListener(new HomeWarnMessageAdapter.onIDoListener() {

            @Override
            public void onIDoClick(int position) {
                readWarnMsg(mWarnMsgList.get(position), position);
            }
        });
        mWarnMsgListView.setFocusable(false);
        getNoticeList();
        scrollView.setOnClickListener(this);
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(AppContext.mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mAdapter = new TaskSituationAdapter(getContext(), mTaskNumInfos);
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.layout_group_situation_header, null);
        mAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(mAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(
                getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.txt_gray_divider)));
        mAdapter.setOnItemClickListener(new TaskSituationAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {

            }
        });
        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                intent.putExtra("mNoticeBean", mNoticeList.get(noticeView.getIndex()));
                startActivity(intent);
            }
        });
    }

    private SpannableString generateTaskSpannableText() {
        SpannableString s = new SpannableString("今日护理（任务）");
        s.setSpan(new RelativeSizeSpan(1.6f), 0, 4, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 4, s.length() - 4, 0);
        s.setSpan(new ForegroundColorSpan(AppContext.mResource.getColor(R.color.txt_gray_shallow)), 4, s.length() - 4, 0);
        return s;
    }

    private SpannableString generateSpannableText() {
        SpannableString s = new SpannableString("儿童状况（近一周）");
        s.setSpan(new RelativeSizeSpan(1.6f), 0, 4, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 4, s.length() - 5, 0);
        s.setSpan(new ForegroundColorSpan(AppContext.mResource.getColor(R.color.txt_gray_shallow)), 4, s.length() - 5, 0);
        return s;
    }

    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(getActivity(), R.color.white));
        ivNoData.setImageResource(R.drawable.yj_message_blank_pages_icon);
        tvNoData.setText("当前没有通知消息哟");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void refreshGroupSituationData() {
        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        currentPage = 1;
        requestGroupSituation();
    }

    private void requestGroupSituation() {
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            pData.put("date", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(getContext(), AppConfig.LIST_SITUAITON, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_SITUAITON + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<TaskSituationEntity>>() {
                    }.getType();
                    BaseListData<TaskSituationEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        List<TaskSituationEntity> results = datas.getData();
                        if (datas.getData() != null) {
                            if (results != null && results.size() != 0) {
                                if (mRecyclerView != null && mRecyclerView.getVisibility() != View.VISIBLE) {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }
                                mTaskNumInfos.clear();
                                mTaskNumInfos.addAll(results);
                                mAdapter.notifyDataSetChanged();
                                mRefreshLayout.endRefreshing();
                            }
                        }
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    private void readWarnMsg(WarnMsgEntity msgEntity, final int position) {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            pData.put("id", msgEntity.getMsgId());
            pData.put("userName", SharedPreferencesManager.getString(ResultStatus.CHANNEL_ID));
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(getActivity(), AppConfig.WARN_MSG_READ, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if (StringUtils.equals(obj.getString("state"), SysCode.SUCCESS)) {
                        /*totalNums --;
                        mWarnMsgList.remove(position);
                        mWarnAdapter.notifyDataSetChanged();
                        if(totalNums == 0){
                            rlNoData.setVisibility(View.VISIBLE);
                            initNoData();
                        }else{

                        }*/
                        maxPage = 0;
                        totalNums = 0;
                        getWarnMessageList(1);

                    } else {
                        AppContext.showToast("操作失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


    private void getWarnMessageList(int currutPage) {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("receiver", SharedPreferencesManager.getString(ResultStatus.CHANNEL_ID));
            paramObj.put("isRead", "0");
            paramObj.put("date", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
            pData.put("params", paramObj);
            pData.put("pageNo", currutPage);
            pData.put("pageSize", 100);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(getActivity(), AppConfig.PAGE_MESSAGE, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {

                    JSONObject obj = new JSONObject(httpResult);
                    if (StringUtils.equals(obj.getString("state"), SysCode.SUCCESS)) {
                        JSONArray tempList = obj.getJSONObject("data").getJSONArray("results");
                        String tempMax = obj.getJSONObject("data").getString("totalPage");
                        totalNums = Integer.parseInt(obj.getJSONObject("data").getString("totalRecord"));
                        maxPage = Integer.parseInt(tempMax);
                        WarnMsgEntity bean;
                        mWarnMsgList.clear();
                        if (maxPage > 0) {
                            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                rlNoData.setVisibility(View.GONE);

                            }
                            if (curPage == 1) {
                                mWarnMsgList.clear();
                            }
                            for (int j = 0; j < tempList.length(); j++) {
                                JSONObject itemObj = tempList.getJSONObject(j);
                                bean = new WarnMsgEntity();
                                if (!itemObj.isNull("warnType")) {
                                    bean.setWarnType(itemObj.getString("warnType"));
                                }
                                if (!itemObj.isNull("warnContent")) {
                                    bean.setWarnContent(itemObj.getString("warnContent"));
                                }
                                if (!itemObj.isNull("warnDate")) {
                                    bean.setWarnDate(itemObj.getString("warnDate"));
                                }
                                bean.setMsgId(itemObj.getString("msgId"));
                                bean.setIsRead(0);
                                mWarnMsgList.add(bean);
                            }
                            mWarnAdapter.notifyDataSetChanged();
                        } else {
                            mWarnMsgList.clear();
                            mWarnAdapter.notifyDataSetChanged();
                            rlNoData.setVisibility(View.VISIBLE);
                            initNoData();
                        }
                    } else {
                        mWarnMsgList.clear();
                        mWarnAdapter.notifyDataSetChanged();
                        rlNoData.setVisibility(View.VISIBLE);
                        initNoData();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mWarnMsgList.clear();
                    mWarnAdapter.notifyDataSetChanged();
                    rlNoData.setVisibility(View.VISIBLE);
                    initNoData();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                mWarnMsgList.clear();
                mWarnAdapter.notifyDataSetChanged();
                rlNoData.setVisibility(View.VISIBLE);
                initNoData();
            }
        });
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        /*if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }*/
    }

    private void requestStatics() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            obj.put("startTime", mCommon.initBeforeSix());
            obj.put("endTime", mCommon.initTime());
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(getActivity(), AppConfig.LIST_CHILDREN_SITUATION, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_CHILDREN_SITUATION + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<ChildSituationStaticEntity>>() {
                    }.getType();
                    BaseListData<ChildSituationStaticEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        mChildrenSituation = datas.getData();
                        initSituation();
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

            }
        });
    }

    public HomeNurseSituationFragment() {
    }

    public static HomeNurseSituationFragment newInstance() {
        HomeNurseSituationFragment fragment = new HomeNurseSituationFragment();
        return fragment;
    }

    private void initSituation() {
        List<LineEntity> listEntities = new ArrayList<>();
        listEntities.addAll(mCommon.initObjectTime());
        String todayDateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        int illCnt = 0;
        int hospitalCnt = 0;
        if (mChildrenSituation != null && mChildrenSituation.size() != 0) {
            //生病
            if (mChildrenSituation.get(0) != null) {
                //七天数据
                List<ChildSituationStaticListEntity> listTimes = mChildrenSituation.get(0).getList();
                for (int i = 0; i < listEntities.size(); i++) {
                    LineEntity listEntityAll = listEntities.get(i);
                    for (int j = 0; j < listTimes.size(); j++) {
                        ChildSituationStaticListEntity listEntityLack = listTimes.get(j);
                        if (StringUtils.equals(listEntityAll.getTime(), listEntityLack.getStaticDate())) {
                            listEntityAll.setIll(listEntityLack.getChildCount());
                            if (StringUtils.equals(listEntityAll.getTime(), todayDateStr)) {
                                illCnt = listEntityLack.getChildCount();
                            }
                        }
                    }
                }
            }
            //住院
            if (mChildrenSituation.get(1) != null) {
                List<ChildSituationStaticListEntity> listTimes = mChildrenSituation.get(1).getList();
                for (int i = 0; i < listEntities.size(); i++) {
                    LineEntity listEntityAll = listEntities.get(i);
                    for (int j = 0; j < listTimes.size(); j++) {
                        ChildSituationStaticListEntity listEntityLack = listTimes.get(j);
                        //
                        if (StringUtils.equals(listEntityAll.getTime(), listEntityLack.getStaticDate())) {
                            listEntityAll.setInHospital(listEntityLack.getChildCount());
                            if (StringUtils.equals(listEntityAll.getTime(), todayDateStr)) {
                                hospitalCnt = listEntityLack.getChildCount();
                            }
                        }
                    }
                }
            }
            String sta = AppContext.mResource.getString(R.string.number_sisution, String.valueOf(illCnt), String.valueOf(hospitalCnt));
            tvChildToday.setText(sta);
        }
        mInclude.removeAllViews();
        mInclude.refreshLineView(listEntities);
    }

    @Override
    public void initWebData() {

    }

    private View view;

    @Override
    protected void updateView(boolean isHideForceOpen) {
        try {
            /*if (isNoData) {
                if (view == null || view != null && !view.equals(rlNoData)) {
                    isHided = true;
                }
                view = rlNoData;
            } else {
                view = lvContent;
            }*/
            view = llParent;
            if (view == null) return;
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
            ObjectAnimator oa = null;
            if (isHideForceOpen) {
                if (isHided) {
                    oa = ObjectAnimator.ofFloat(view, "translationY", -1.2f * view.getHeight(), 0f);
                }
            } else {
                if (!isHided) {
                    oa = ObjectAnimator.ofFloat(view, "translationY", 0f, -1.2f * view.getHeight());
                } else {
                    oa = ObjectAnimator.ofFloat(view, "translationY", -1.2f * view.getHeight(), 0f);
                }
            }
            if (oa != null) {
                oa.setDuration(500);
                oa.start();
                isHided = !isHided;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && this.isVisible()) {
            if (isVisibleToUser && !mHasLoadedOnce) {
                mHasLoadedOnce = true;
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected View getAnimatorView() {
        return llParent;
    }

    @Override
    protected boolean isAutoInit() {
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            //儿童统计更多
            case R.id.tv_more:
                break;
            case R.id.tvWarn:
                getWarnMessageList(1);
                break;
            case R.id.tvWarnHistory:
                Intent intenth = new Intent(getActivity(), WarnmsgHistoryActivity.class);
                startActivity(intenth);
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    //上拉加载更多数据
                    currentPage = 1;
                    mTaskNumInfos.clear();
                    requestGroupSituation();
                    mRefreshLayout.endLoadingMore();
                    break;
                case 1:
                    //上拉加载更多数据
                    currentPage++;
                    requestGroupSituation();
                    mRefreshLayout.endLoadingMore();
                    break;
                case 2:
                    mRefreshLayout.endLoadingMore();
                    break;
            }
        }
    };

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        mDefineRefreshWithLoadView.updateLoadingMoreText("任务数据刷新中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        currentPage = 1;
        handler.sendEmptyMessageDelayed(0, 1500);
        return true;
    }

    private void getNoticeList() {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            pData.put("pageNo", 1);
            pData.put("pageSize", 3);
            pData.put("params", obj0);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this.getContext(), AppConfig.PAGE_NOTICE, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log(AppConfig.PAGE_NOTICE + "-->" + httpResult);
                try {
                    Type type = new TypeToken<BaseData<NoticePageBean>>() {
                    }.getType();
                    BaseData<NoticePageBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        if (datas.getData() != null) {
                            int maxPage = datas.getData().totalPage;
                            if (maxPage > 0) {
                                List<NoticeBean> results = datas.getData().results;
                                List<String> strNoticeList = new ArrayList();
                                if (results != null && results.size() != 0) {
                                    for (NoticeBean notice : results) {
                                        String oldHtml = notice.getContent();
                                        Document doc = Jsoup.parseBodyFragment(oldHtml);
                                        Elements imgs = doc.select("img");
                                        for (Element img : imgs) {
                                            String linkSrc = img.attr("src");
                                            if (linkSrc.startsWith("/web/runtime")) {
                                                linkSrc = AppConfig.RICH_IMAGE__URL + linkSrc;
                                                img.attr("src", linkSrc);
                                            }
                                        }
                                        String docStartBody = doc.body().toString();
                                        notice.setContent(docStartBody.substring(6, docStartBody.length() - 7));
                                        mNoticeList.add(notice);
                                        strNoticeList.add(notice.getTitle());
                                    }
                                    noticeView.start(strNoticeList);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }
}
