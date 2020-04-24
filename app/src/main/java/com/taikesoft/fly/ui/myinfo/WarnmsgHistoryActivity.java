package com.taikesoft.fly.ui.myinfo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.DefineBAGRefreshView;
import com.taikesoft.fly.business.common.view.DefineBAGRefreshWithLoadView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.adapter.WarnmsgAdapter;
import com.taikesoft.fly.ui.myinfo.bean.ItemPageBean;
import com.taikesoft.fly.ui.myinfo.bean.MsgPageBean;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.myinfo.entity.WarnMsgEntity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 *我的消息
 */

public class WarnmsgHistoryActivity extends BaseActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private TextView tvTitle;

    private RelativeLayout rlRealtime;

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<WarnMsgEntity> mWarnMsgList = new ArrayList<>();
    private WarnmsgAdapter mWarnAdapter;
    private int curPage = 1;
    private int maxPage = 1;
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(AppContext.mResource.getString(R.string.message_record_title));

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRecyclerView = findViewById(R.id.recyclerview);

        rlNoData = findViewById(R.id.rl_no_data);
        ivNoData = findViewById(R.id.iv_no_data);
        tvNoData = findViewById(R.id.tv_no_data);

        initNoDataView();
        initBGARefreshLayout();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText("暂无消息");
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(AppContext.mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mWarnAdapter = new WarnmsgAdapter(this, mWarnMsgList);
        mRecyclerView.setAdapter(mWarnAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWarnAdapter.setOnItemClickListener(new WarnmsgAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                WarnMsgEntity mWarnmsgEntity = mWarnMsgList.get(position);
                Intent intent = new Intent(WarnmsgHistoryActivity.this, WarnmsgDetailActivity.class);
                intent.putExtra("type", mWarnmsgEntity.getWarnType());
                intent.putExtra("content", mWarnmsgEntity.getWarnContent());
                intent.putExtra("time", mWarnmsgEntity.getWarnDate());
                startActivity(intent);

            }
        });
        getWarnMessageList();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉加载最新数据
        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        curPage = 1;
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    curPage = 1;
                    getWarnMessageList();
                    break;
                case 1:
                    curPage++;
                    getWarnMessageList();
                    break;
                case 2:
                    mRefreshLayout.endLoadingMore();
                    break;
            }
        }
    };

    @Override
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

    private void getWarnMessageList() {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("receiver", SharedPreferencesManager.getString(ResultStatus.CHANNEL_ID));
            paramObj.put("userName", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            pData.put("params", paramObj);
            pData.put("pageNo", curPage);
            pData.put("pageSize", 10);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(this, AppConfig.PAGE_MESSAGE, entity, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log(AppConfig.PAGE_NURSE_ITEM + "-->" + httpResult);
                try {
                    Type type = new TypeToken<BaseData<MsgPageBean>>() {
                    }.getType();
                    BaseData<MsgPageBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        List<WarnMsgEntity> results = datas.getData().results;
                        if (datas.getData() != null) {
                            maxPage = datas.getData().totalPage;
                            if (maxPage > 0) {
                                if (curPage == 1) {
                                    mWarnMsgList.clear();
                                }
                                mWarnMsgList.addAll(results);
                                mWarnAdapter.notifyDataSetChanged();
                                if (curPage == 1) {
                                    mRefreshLayout.endRefreshing();
                                } else {
                                    mRefreshLayout.endLoadingMore();
                                }
                            } else {
                                initNoData();
                            }

                        } else {
                            initNoData();
                        }

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
        /*if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_warnmsg_history);
    }

    @Override
    public void setListener() {

    }
}
