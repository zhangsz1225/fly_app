package com.taikesoft.fly.business.common.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taikesoft.fly.R;

/**
 * 修改头像弹出框
 */
public class SelectPhotoPopupWindow extends PopupWindow {
    /**
     * view
     */
    private View mMenuView;
    /**
     * 拍照上传
     */
    private TextView takePhone;
    /**
     * 选择照片上传
     */
    private TextView selectPhone;
    /**
     * 取消
     */
    private TextView cancel;
    private RelativeLayout relativeLayout;

    /**
     * 构造方法
     *
     * @param context
     */
    public SelectPhotoPopupWindow(Context context) {
        super(context);
        //解析view
        mMenuView = LayoutInflater.from(context).inflate(R.layout.popupwindow_select_photo, null);
        this.setContentView(mMenuView);
        //初始化控件
        initWidget(mMenuView);
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public SelectPhotoPopupWindow(Context context, View.OnClickListener onClickListener) {
        this(context);
        takePhone.setOnClickListener(onClickListener);
        selectPhone.setOnClickListener(onClickListener);
    }

    /**
     * 初始化控件
     */
    private void initWidget(View view) {
        //初始化控件
        takePhone = (TextView) view.findViewById(R.id.take_photo);
        selectPhone = (TextView) view.findViewById(R.id.select_photo);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_main_content);
        cancel = (TextView) view.findViewById(R.id.cancel);
        //设置取消点击事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置背景为半透明
        this.setBackgroundDrawable(new ColorDrawable(0xb0999999));
    }
}
