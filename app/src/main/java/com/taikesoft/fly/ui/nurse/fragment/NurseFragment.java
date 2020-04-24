package com.taikesoft.fly.ui.nurse.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.taikesoft.fly.R;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 护理界面
 */
public class NurseFragment extends Fragment implements OnTabSelectListener {
    public static final String TAG = "NurseFragment";
    @BindView(R.id.stl_nurse)
    SlidingTabLayout stlNurse;
    @BindView(R.id.vp_nurse)
    ViewPager vpNurse;

    private ArrayList<ItemFragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private List<NurseItemBean> amList = new ArrayList<>();
    private List<NurseItemBean> pmList = new ArrayList<>();
    private List<NurseItemBean> eveList = new ArrayList<>();
    private List<NurseItemBean> nightList = new ArrayList<>();
    private final String[] mTitles = {"全部", "上午", "下午", "晚上", "凌晨"};
    private String quickText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nurse, container, false);
        ButterKnife.bind(this, view);
        quickText = getArguments().getString("quickText");
        mFragments.add(ItemFragment.newInstance(new ArrayList<>(), "全部",quickText));
        mFragments.add(ItemFragment.newInstance(amList, "上午",quickText));
        mFragments.add(ItemFragment.newInstance(pmList, "下午",quickText));
        mFragments.add(ItemFragment.newInstance(eveList, "晚上",quickText));
        mFragments.add(ItemFragment.newInstance(nightList, "凌晨",quickText));
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        vpNurse.setAdapter(mAdapter);
        stlNurse.setViewPager(vpNurse, mTitles);
        vpNurse.setCurrentItem(0);
        return view;
    }

    public NurseFragment() {
    }

    public static NurseFragment newInstance(String quickText) {
        NurseFragment fragment = new NurseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("quickText", quickText);
        fragment.setArguments(bundle);
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
}
