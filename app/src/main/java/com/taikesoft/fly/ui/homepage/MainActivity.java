package com.taikesoft.fly.ui.homepage;


import android.os.Bundle;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.ui.child.fragment.ChildFragment;
import com.taikesoft.fly.ui.homepage.fragment.HomeNurseSituationFragment;
import com.taikesoft.fly.ui.myinfo.MineFragment;
import com.taikesoft.fly.ui.nurse.fragment.NurseFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/**
 * 首页activity
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.tab)
    PageNavigationView pageNavigationView;
    private Fragment[] fragments;
    // private HomeFragment homeFragment;
    private HomeNurseSituationFragment homeFragment;
    private NurseFragment nurseFragment;
    // private ChildrenFragment childFragment;
    private ChildFragment childFragment;
    private MineFragment mineFragment;
    //默认选择第一个fragment
    private int lastSelectedPosition = 0;
    private String flag;
    private String quickText;
    public static MainActivity mMainActivity;

    @Override
    public void setView() {
        setContentView(R.layout.activity_main);
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
        //绑定控件
        ButterKnife.bind(this);
        mMainActivity = this;
    }

    @Override
    protected void initView() {
        flag = getIntent().getStringExtra("flag");
        quickText = getIntent().getStringExtra("quickText");
        initFragments();
        //从首页跳转到护理
        if (StringUtils.equals("NurseFragmemt", flag)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[0]);
            if (!fragments[1].isAdded()) {
                transaction.add(R.id.content, fragments[1], "NurseFragmemt");
            }
            transaction.show(fragments[1]).commit();
        }
        //初始化底部导航
        NavigationController navigationController = pageNavigationView.custom().addItem(newItem(R.drawable.ic_home_unselected, R.drawable.ic_home_selected, "首页"))
                .addItem(newItem(R.drawable.ic_nurse_unselected, R.drawable.ic_nurse_selected, "护理"))
                .addItem(newItem(R.drawable.ic_child_unselected, R.drawable.ic_child_selected, "儿童"))
                .addItem(newItem(R.drawable.ic_mine_unselected, R.drawable.ic_mine_selected, "我的")).build();

        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                changeFragment(lastSelectedPosition, index);
                lastSelectedPosition = index;
            }

            @Override
            public void onRepeat(int index) {
                //重复选中
            }
        });
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void initWebData() {

    }

    //创建一个item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        NormalItemView normalItemView = new NormalItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        normalItemView.setTextDefaultColor(getResources().getColor(R.color.app_color_gray));
        normalItemView.setTextCheckedColor(getResources().getColor(R.color.app_color_blue));
        return normalItemView;
    }

    private void initFragments() {
        //  homeFragment = HomeFragment.newInstance();
        homeFragment = HomeNurseSituationFragment.newInstance();
        nurseFragment = NurseFragment.newInstance(quickText);
        // childFragment = ChildrenFragment.newInstance();
        childFragment = ChildFragment.newInstance();
        mineFragment = MineFragment.newInstance();
        fragments = new Fragment[]{homeFragment, nurseFragment, childFragment, mineFragment};
        lastSelectedPosition = 0;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.content, homeFragment).show(homeFragment).commit();//默认显示第一个
    }

    private void changeFragment(int lastIndex, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastIndex]);
        if (!fragments[index].isAdded()) {
            if (index == 1) {
                transaction.add(R.id.content, fragments[index], "NurseFragmemt");
            } else {
                transaction.add(R.id.content, fragments[index]);
            }
        }
        transaction.show(fragments[index]).commit();
    }
}
