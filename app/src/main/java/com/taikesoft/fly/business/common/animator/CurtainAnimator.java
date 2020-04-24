package com.taikesoft.fly.business.common.animator;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by ${YGP} on 2017/3/31.
 */

public class CurtainAnimator {
    private boolean isHided = false;
    private View viewAnimator;
    private ObjectAnimator oa = null;
    public void init() {
        isHided = false;
    }

    public void setViewAnimator(View viewAnimator) {
        this.viewAnimator = viewAnimator;
    }

    public void start(boolean isHideForceOpen) {
        if (viewAnimator == null) return;

        if (isHideForceOpen) {
            if (isHided) {
                oa = ObjectAnimator.ofFloat(viewAnimator, "translationY", -1.2f*viewAnimator.getHeight(),0f);
            } else {
                return;
            }
        } else {
            if (!isHided) {
                oa = ObjectAnimator.ofFloat(viewAnimator, "translationY", 0f,-1.2f*viewAnimator.getHeight());
            } else {
                oa = ObjectAnimator.ofFloat(viewAnimator, "translationY", -1.2f*viewAnimator.getHeight(),0f);
            }
        }
        if (oa != null) {
            oa.setDuration(500);
            oa.start();
            isHided = !isHided;
        }
    }

    public void unwind() {
        isHided = false;
        oa = ObjectAnimator.ofFloat(viewAnimator, "translationY", -1.2f*viewAnimator.getHeight(),0f);
        oa.setDuration(500);
        oa.start();
    }

    public void wind() {
        isHided = true;
        oa = ObjectAnimator.ofFloat(viewAnimator, "translationY",0f,-1.2f*viewAnimator.getHeight());
        oa.setDuration(500);
        oa.start();
    }

    public void unwind(int time) {
        isHided = false;
        oa = ObjectAnimator.ofFloat(viewAnimator, "translationY", -1.2f*viewAnimator.getHeight(),0f);
        oa.setDuration(time);
        oa.start();
    }

    public void wind(int time) {
        isHided = true;
        oa = ObjectAnimator.ofFloat(viewAnimator, "translationY",0f,-1.2f*viewAnimator.getHeight());
        oa.setDuration(time);
        oa.start();
    }
}
