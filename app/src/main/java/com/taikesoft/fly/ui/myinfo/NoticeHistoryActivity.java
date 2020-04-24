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
import com.taikesoft.fly.business.common.view.DefineBAGRefreshWithLoadView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.adapter.NoticeAdapter;
import com.taikesoft.fly.ui.myinfo.bean.NoticeBean;
import com.taikesoft.fly.ui.myinfo.bean.NoticePageBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 通知公告
 */

public class NoticeHistoryActivity extends BaseActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private TextView tvTitle;

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<NoticeBean> mNoticeList = new ArrayList<>();
    private NoticeAdapter mNoticeAdapter;
    private int curPage = 1;
    private int maxPage = 1;
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(AppContext.mResource.getString(R.string.notice_title));

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
        tvNoData.setText("暂无通知");
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(AppContext.mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mNoticeAdapter = new NoticeAdapter(this, mNoticeList);
        mRecyclerView.setAdapter(mNoticeAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoticeAdapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                NoticeBean bean = mNoticeList.get(position);
                Intent intent = new Intent(NoticeHistoryActivity.this, NoticeDetailActivity.class);
                intent.putExtra("mNoticeBean", bean);
                startActivity(intent);
            }
        });
        getNoticeList();
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
                    getNoticeList();
                    break;
                case 1:
                    curPage++;
                    getNoticeList();
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

    private void getNoticeList() {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            pData.put("pageNo", 1);
            pData.put("pageSize", 15);
            pData.put("params", obj0);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(this, AppConfig.PAGE_NOTICE, entity, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log(AppConfig.PAGE_NOTICE + "-->" + httpResult);
                try {
                    Type type = new TypeToken<BaseData<NoticePageBean>>() {
                    }.getType();
                    BaseData<NoticePageBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        List<NoticeBean> results = datas.getData().results;
                        if (datas.getData() != null) {
                            maxPage = datas.getData().totalPage;
                            if (maxPage > 0) {
                                if (curPage == 1) {
                                    mNoticeList.clear();
                                }
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
                                    }
                                }
                                mNoticeList.addAll(results);
                                mNoticeAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_notice_history);
    }

    @Override
    public void setListener() {

    }
}
