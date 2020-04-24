package com.taikesoft.fly.ui.child.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 儿童tab界面
 */
public class ChildFragment extends Fragment implements OnTabSelectListener {
    public static final String TAG = "ChildFragment";
    @BindView(R.id.stl_child)
    SlidingTabLayout stlChild;
    @BindView(R.id.vp_child)
    ViewPager vpChild;
    private ArrayList<ChildSearchFragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private ArrayList<ChildInfoEntity> sunList = new ArrayList<>();
    private ArrayList<ChildInfoEntity> moonList = new ArrayList<>();
    private ArrayList<ChildInfoEntity> starList = new ArrayList<>();
    private ArrayList<ChildInfoEntity> islList = new ArrayList<>();
    private final String[] mTitles = {"全部", "阳光室", "月光室", "星光室", "隔离室"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child, container, false);

        ButterKnife.bind(this, view);
        getChildren();
        return view;
    }

    public ChildFragment() {
    }

    public static ChildFragment newInstance() {
        ChildFragment fragment = new ChildFragment();
        return fragment;
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

    private void getChildren() {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            pData.put("nursingRoom", mTitles[0]);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this.getContext(), AppConfig.CHILDREN, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log(AppConfig.CHILDREN + "-->" + httpResult);
                Type type = new TypeToken<BaseListData<ChildInfoEntity>>() {
                }.getType();
                BaseListData<ChildInfoEntity> datas = new Gson().fromJson(httpResult, type);
                if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                    ArrayList<ChildInfoEntity> list = datas.getData();
                    int sunSize = 0;
                    int moonSize = 0;
                    int starSize = 0;
                    int islSize = 0;
                    for (ChildInfoEntity info : list) {
                        if (StringUtils.equals("阳光室", info.getNursingRoom())) {
                            sunSize++;
                        }
                        if (StringUtils.equals("月光室", info.getNursingRoom())) {
                            moonSize++;
                        }
                        if (StringUtils.equals("星光室", info.getNursingRoom())) {
                            starSize++;
                        }
                        if (StringUtils.equals("隔离室", info.getNursingRoom())) {
                            islSize++;
                        }
                    }
                    mFragments.add(ChildSearchFragment.newInstance(list, "全部"));
                    mFragments.add(ChildSearchFragment.newInstance(sunList, "阳光室"));
                    mFragments.add(ChildSearchFragment.newInstance(moonList, "月光室"));
                    mFragments.add(ChildSearchFragment.newInstance(starList, "星光室"));
                    mFragments.add(ChildSearchFragment.newInstance(islList, "隔离室"));
                    mAdapter = new MyPagerAdapter(getChildFragmentManager());
                    vpChild.setAdapter(mAdapter);
                    stlChild.setViewPager(vpChild, mTitles);
                    vpChild.setCurrentItem(0);
                    stlChild.showMsg(0, list.size());
                    stlChild.setMsgMargin(0, 0, 10);
                    if (sunSize > 0) {
                        stlChild.showMsg(1, sunSize);
                        stlChild.setMsgMargin(1, 0, 10);

                    }
                    if (moonSize > 0) {
                        stlChild.showMsg(2, moonSize);
                        stlChild.setMsgMargin(2, 0, 10);
                    }
                    if (starSize > 0) {
                        stlChild.showMsg(3, starSize);
                        stlChild.setMsgMargin(3, 0, 10);
                    }
                    if (islSize > 0) {
                        stlChild.showMsg(4, islSize);
                        stlChild.setMsgMargin(4, 0, 10);
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
