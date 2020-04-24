package com.taikesoft.fly.ui.nurse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.DefineBAGRefreshWithLoadView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.ItemPageBean;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.ChooseChildrenActivity;
import com.taikesoft.fly.ui.nurse.NightPatrolActivity;
import com.taikesoft.fly.ui.nurse.adapter.NurseItemAdapter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 单个tab的护理界面
 */
public class ItemFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    public static final String TAG = "ItemFragment";
    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<NurseItemBean> mItemList = new ArrayList<>();
    private NurseItemAdapter mItemAdapter;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private String timeType;
    private int curPage = 1;
    private int maxPage;
    private String quickText;
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        rlNoData = view.findViewById(R.id.rl_no_data);
        ivNoData = view.findViewById(R.id.iv_no_data);
        tvNoData = view.findViewById(R.id.tv_no_data);
        mItemList = (List<NurseItemBean>) getArguments().getSerializable("itemList");
        timeType = getArguments().getString("timeType");
        quickText = getArguments().getString("quickText");
        initNoDataView();
        initBGARefreshLayout();
        return view;
    }

    public ItemFragment() {
    }

    public static ItemFragment newInstance(List<NurseItemBean> list, String timeType, String quickText) {
        ItemFragment fragment = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString("quickText", quickText);
        bundle.putString("timeType", timeType);
        bundle.putSerializable("itemList", (Serializable) list);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(AppContext.mResource.getDrawable(R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText("暂无护理");
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(AppContext.mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mItemAdapter = new NurseItemAdapter(this.getContext(), mItemList,quickText);
        mRecyclerView.setAdapter(mItemAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mItemAdapter.setOnItemClickListener(new NurseItemAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                String text = mItemList.get(position).getNurseItem();
                NurseItemBean mNurseItemBean = mItemList.get(position);
                if ("巡夜".equals(text)) {
                    Intent intent = new Intent(getContext(), NightPatrolActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), ChooseChildrenActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mTagId", "NurseFragment");
                    intent.putExtra("childData", new ArrayList<ChildInfoEntity>());
                    startActivity(intent);
                }
            }
        });
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    curPage = 1;
                    getNurseItemList();
                    break;
                case 1:
                    curPage++;
                    getNurseItemList();
                    break;
                case 2:
                    mRefreshLayout.endLoadingMore();
                    break;
            }
        }
    };

    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉加载最新数据
        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        curPage = 1;
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (curPage >= maxPage) {
            mDefineRefreshWithLoadView.updateLoadingMoreText("没有更多数据啦");
            mDefineRefreshWithLoadView.hideLoadingMoreImg();
            handler.sendEmptyMessageDelayed(2, 1000);
            return true;
        }
        //上拉加载更多数据
        handler.sendEmptyMessageDelayed(1, 1000);
        return true;
    }

    private void getNurseItemList() {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj0.put("taskDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
            obj0.put("timeType", timeType);
            pData.put("pageNo", curPage);
            pData.put("pageSize", 15);
            pData.put("params", obj0);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(this.getContext(), AppConfig.PAGE_NURSE_ITEM, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log(AppConfig.PAGE_NURSE_ITEM + "-->" + httpResult);
                try {
                    Type type = new TypeToken<BaseData<ItemPageBean>>() {
                    }.getType();
                    BaseData<ItemPageBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        if (datas.getData() != null) {
                            maxPage = datas.getData().totalPage;
                            if (maxPage > 0) {
                                if (curPage == 1) {
                                    mItemList.clear();
                                }
                                List<NurseItemBean> results = datas.getData().results;
                                if (results != null && results.size() != 0) {
                                    mItemList.addAll(results);
                                    mItemAdapter.notifyDataSetChanged();
                                    if (curPage == 1) {
                                        mRefreshLayout.endRefreshing();
                                    } else {
                                        mRefreshLayout.endLoadingMore();
                                    }
                                } else {
                                    initNoDataView();
                                    initNoData();
                                }
                            }
                        } else {
                            initNoDataView();
                            initNoData();
                        }

                    } else {
                        initNoDataView();
                        initNoData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                initNoData();
            }
        });
    }
    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
    }
}
