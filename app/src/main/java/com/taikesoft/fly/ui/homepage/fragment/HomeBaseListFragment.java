package com.taikesoft.fly.ui.homepage.fragment;

import android.view.View;

import com.taikesoft.fly.business.base.BaseFragment;
import com.taikesoft.fly.business.common.animator.CurtainAnimator;

/**
 * 首页 通知的list的基类，点“知道了”动画效果
 */

public class HomeBaseListFragment extends BaseFragment {
    protected CurtainAnimator curtainAnimator;

    @Override
    protected void initView(View view) {
        if (curtainAnimator == null) {
            curtainAnimator = new CurtainAnimator();
        }
        curtainAnimator.init();
    }

    protected View getAnimatorView() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    /**
     * 窗帘式卷起和拉下
     *
     * @param isHideForceOpen
     */
    protected void updateView(boolean isHideForceOpen) {
        curtainAnimator.setViewAnimator(getAnimatorView());
        curtainAnimator.start(isHideForceOpen);
    }
}
