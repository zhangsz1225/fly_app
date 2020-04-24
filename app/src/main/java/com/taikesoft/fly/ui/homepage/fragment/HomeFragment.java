package com.taikesoft.fly.ui.homepage.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.SimpleBannerInfo;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页，暂未使用
 */

public class HomeFragment extends Fragment implements OnChartValueSelectedListener, OnTabSelectListener {
    public static final String TAG = "HomeFragment";
    private ArrayList<PieCharFragment> mFragments = new ArrayList<>();
    @BindView(R.id.rl_title_bar)
    RelativeLayout rlTitleBar;

    @BindView(R.id.xbanner_view)
    XBanner xbanner_view;
    @BindView(R.id.gridview)
    GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;
    private final String[] mTitles = {"上午", "下午", "夜间"};
    @BindView(R.id.stl_nurse)
    SlidingTabLayout stlNurse;
    @BindView(R.id.vp_nurse)
    ViewPager vpNurse;
    private MyPagerAdapter mAdapter;
    private ArrayList<NurseItemBean> amList = new ArrayList<>();
    private ArrayList<NurseItemBean> pmList = new ArrayList<>();
    private ArrayList<NurseItemBean> nightList = new ArrayList<>();
    //图标下的文字
    private final String name[] = {"服药", "更换", "体温", "水果点心", "喝水喂奶", "巡夜", "其他"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        rlTitleBar.setVisibility(View.GONE);
        //图片集合,模拟一下数据
        final List<SimpleBannerInfo> imgesUrl = new ArrayList<>();
        SimpleBannerInfo info = new SimpleBannerInfo() {
            @Override
            public Object getXBannerUrl() {
                return R.mipmap.about_logo;
            }

            @Override
            public String getXBannerTitle() {
                return "图片1";
            }
        };
        SimpleBannerInfo info1 = new SimpleBannerInfo() {
            @Override
            public Object getXBannerUrl() {
                return R.mipmap.about_logo;
            }

            @Override
            public String getXBannerTitle() {
                return "图片2";
            }
        };
        SimpleBannerInfo info2 = new SimpleBannerInfo() {
            @Override
            public Object getXBannerUrl() {
                return R.mipmap.about_logo;
            }

            @Override
            public String getXBannerTitle() {
                return "图片3";
            }
        };
        imgesUrl.add(info);
        imgesUrl.add(info1);
        imgesUrl.add(info2);
        //数据集合导入banner里
        xbanner_view.setBannerData(imgesUrl);
        xbanner_view.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(getActivity())
                        .load(imgesUrl.get(position).getXBannerUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into((ImageView) view);
            }
        });
        xbanner_view.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                AppContext.showToast("点击了第" + position + "图片");
            }
        });
        //图标
        int icno[] = {R.drawable.login_logo, R.drawable.login_logo, R.drawable.login_logo,
                R.drawable.login_logo, R.drawable.login_logo, R.drawable.login_logo, R.drawable.login_logo};

        dataList = new ArrayList<Map<String, Object>>();
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
            }
        });
        NurseItemBean bean1 = new NurseItemBean();
        bean1.setNurseItem("起床");
        bean1.setCount(50);
        amList.add(bean1);
        NurseItemBean bean2 = new NurseItemBean();
        bean2.setNurseItem("刷牙");
        bean2.setCount(50);
        amList.add(bean2);
        NurseItemBean bean3 = new NurseItemBean();
        bean3.setNurseItem("洗手");
        bean3.setCount(66);
        amList.add(bean3);
        NurseItemBean bean4 = new NurseItemBean();
        bean4.setNurseItem("洗脸");
        bean4.setCount(66);
        amList.add(bean4);
        NurseItemBean bean5 = new NurseItemBean();
        bean5.setNurseItem("洗臀");
        bean5.setCount(66);
        amList.add(bean5);
        NurseItemBean bean6 = new NurseItemBean();
        bean6.setNurseItem("洗脚");
        bean6.setCount(66);
        amList.add(bean6);

        NurseItemBean bean8 = new NurseItemBean();
        bean8.setNurseItem("擦嘴");
        bean8.setCount(56);
        amList.add(bean8);
        NurseItemBean bean9 = new NurseItemBean();
        bean9.setNurseItem("更换");
        bean9.setCount(5);
        amList.add(bean9);
        NurseItemBean bean10 = new NurseItemBean();
        bean10.setNurseItem("剪指甲");
        bean10.setCount(7);
        amList.add(bean10);
        NurseItemBean bean11 = new NurseItemBean();
        bean11.setNurseItem("早餐");
        bean11.setCount(66);
        amList.add(bean11);
        NurseItemBean bean12 = new NurseItemBean();
        bean12.setNurseItem("午饭");
        bean12.setCount(66);
        amList.add(bean12);

        NurseItemBean bean15 = new NurseItemBean();
        bean15.setNurseItem("服药");
        bean15.setCount(10);
        amList.add(bean15);
        NurseItemBean bean16 = new NurseItemBean();
        bean16.setNurseItem("喝水");
        bean16.setCount(55);
        amList.add(bean16);

        NurseItemBean bean18 = new NurseItemBean();
        bean18.setNurseItem("养育");
        bean18.setCount(12);
        amList.add(bean18);
        NurseItemBean bean19 = new NurseItemBean();
        bean19.setNurseItem("康复");
        bean19.setCount(9);
        amList.add(bean19);
        NurseItemBean bean20 = new NurseItemBean();
        bean20.setNurseItem("特教");
        bean20.setCount(8);
        amList.add(bean20);

        NurseItemBean bean22 = new NurseItemBean();
        bean22.setNurseItem("体温");
        bean22.setCount(5);
        amList.add(bean22);

        NurseItemBean bean25 = new NurseItemBean();
        bean25.setNurseItem("休息");
        bean25.setCount(66);
        amList.add(bean25);

        //下午
        NurseItemBean bean16p = new NurseItemBean();
        bean16p.setNurseItem("喝水");
        bean16p.setCount(55);
        pmList.add(bean16p);
        NurseItemBean bean3p = new NurseItemBean();
        bean3p.setNurseItem("洗手");
        bean3p.setCount(66);
        pmList.add(bean3p);
        NurseItemBean bean4p = new NurseItemBean();
        bean4p.setNurseItem("洗脸");
        bean4p.setCount(66);
        pmList.add(bean4p);
        NurseItemBean bean13 = new NurseItemBean();
        bean13.setNurseItem("午睡");
        bean13.setCount(66);
        pmList.add(bean13);
        NurseItemBean bean17p = new NurseItemBean();
        bean17p.setNurseItem("点心");
        bean17p.setCount(66);
        pmList.add(bean17p);
        NurseItemBean beanp20 = new NurseItemBean();
        beanp20.setNurseItem("特教");
        beanp20.setCount(8);
        pmList.add(beanp20);
        NurseItemBean bean14p = new NurseItemBean();
        bean14p.setNurseItem("晚餐");
        bean14p.setCount(66);
        pmList.add(bean14p);
        NurseItemBean beanp25 = new NurseItemBean();
        beanp25.setNurseItem("休息");
        beanp25.setCount(66);
        pmList.add(beanp25);
        NurseItemBean beanp = new NurseItemBean();
        beanp.setNurseItem("休息");
        beanp.setCount(66);
        pmList.add(beanp);

        NurseItemBean bean23 = new NurseItemBean();
        bean23.setNurseItem("休闲娱乐");
        bean23.setCount(66);
        pmList.add(bean23);

        //晚上

        NurseItemBean bean22n = new NurseItemBean();
        bean22n.setNurseItem("体温");
        bean22n.setCount(5);
        nightList.add(bean22n);

        NurseItemBean bean7 = new NurseItemBean();
        bean7.setNurseItem("洗澡");
        bean7.setCount(66);
        nightList.add(bean7);
        NurseItemBean bean24 = new NurseItemBean();
        bean24.setNurseItem("起夜");
        bean24.setCount(1);
        nightList.add(bean24);

        NurseItemBean bean21 = new NurseItemBean();
        bean21.setNurseItem("巡夜");
        bean21.setCount(66);
        nightList.add(bean21);
        mFragments.add(PieCharFragment.newInstance(amList));
        mFragments.add(PieCharFragment.newInstance(pmList));
        mFragments.add(PieCharFragment.newInstance(nightList));
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        vpNurse.setAdapter(mAdapter);
        stlNurse.setViewPager(vpNurse, mTitles);
        vpNurse.setCurrentItem(0);
        return view;
    }

    private void requestData() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            obj.put("userId", SharedPreferencesManager.getString(ResultStatus.USER_ID));
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(AppContext.mContext, AppConfig.CHILDREN, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    Type type = new TypeToken<BaseListData<NurseItemBean>>() {
                    }.getType();
                    BaseListData<NurseItemBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        List<NurseItemBean> list = datas.getData();
                        if (list != null && list.size() != 0) {
                        } else {
                        }
                        return;
                    } else {
                        return;
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

    @Override
    public void onResume() {
        super.onResume();
        xbanner_view.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        xbanner_view.stopAutoPlay();
    }

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        TLog.log("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}