package com.taikesoft.fly.business.base;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.context.AppManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

	public RelativeLayout mBackLayout;
	public TextView mTitle;
	private LinearLayout llLoading;
	private TextView tvLoading;
	private ProgressBar pbLoading;
	public RelativeLayout rlTitleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();// setContentView
		initView();// 初始化界面
		initCache();//初始化缓存
		resumeInstanceState(savedInstanceState);
		initWebData();// 初始化网络数据
		setListener();// 设置监听事件
		setBack();// 设置titlebar返回按钮事件
		setStatusBar();
		AppManager.getAppManager().addActivity(this);
	}


	protected void setStatusBar() {
		//StatusBarUtil.setColor(this, getResources().getColor(R.color.colorBlue2));
	}

	protected void initCache() {
	}

	public abstract void setView();

	protected void initView() {
		rlTitleBar = (RelativeLayout) findViewById(R.id.rl_title_bar);
	}

	public abstract void setListener();

	protected void resumeInstanceState(Bundle savedInstanceState) {
	}

	protected void setBack() {
		try {
			mBackLayout = (RelativeLayout) findViewById(R.id.rl_back);
			mBackLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} catch (Exception e) {
		}
	}

	protected void setTitle(String title) {
		try {
			mTitle = (TextView) findViewById(R.id.tv_title);
			mTitle.setText(title);
		} catch (Exception e) {

		}
	}

	protected void hideTitle() {
		try {
			rlTitleBar = (RelativeLayout) findViewById(R.id.rl_title_bar);
			rlTitleBar.setVisibility(View.GONE);
		} catch (Exception e) {

		}
	}

	protected void showTitle() {
		try {
			rlTitleBar = (RelativeLayout) findViewById(R.id.rl_title_bar);
			rlTitleBar.setVisibility(View.VISIBLE);
		} catch (Exception e) {

		}
	}

	protected void initWebData() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}

	protected boolean isCanPtr() {
		return false;
	}

	protected void refreshWebData() {
	}

	protected boolean hasLoading() {
		return false;
	}
	
	protected int getLoadingStatus() {
		if (llLoading != null) {
			return llLoading.getVisibility();
		} else {
			return View.GONE;
		}
		
	}

	protected void initLoading(String msg) {
		try {
			if (hasLoading()) {
				llLoading = (LinearLayout) findViewById(R.id.ll_loading);
				llLoading.setVisibility(View.VISIBLE);
				tvLoading = (TextView) findViewById(R.id.tv_loading);
				tvLoading.setText(msg);
				pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
				llLoading.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void hideLoading() {
		if (llLoading != null && llLoading.getVisibility() == View.VISIBLE)
			System.out.println("hide");
		llLoading.setVisibility(View.GONE);
	}

	protected void loadError(String msg, boolean doAgain) {
		if (llLoading != null && llLoading.getVisibility() == View.VISIBLE) {
			pbLoading.setVisibility(View.GONE);
			tvLoading.setText(msg);
			if (doAgain) {
				llLoading.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadAgain();
					}
				});
			}
		}
	}
	
	public void requestFinish() {
		
	}

	public boolean onTouchEvent(MotionEvent event) {
		if(null != this.getCurrentFocus()){
			/**
			 * 点击空白位置 隐藏软键盘
			 */
			InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		}
		return super .onTouchEvent(event);
	}

	protected void loadAgain() {
		if (llLoading != null) {
			pbLoading.setVisibility(View.VISIBLE);
			tvLoading.setText("正在加载...");
			llLoading.setClickable(false);
			refreshWebData();
		}
	}

	protected void showNoRes(String msg, int visibility) {
		
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		super.addContentView(view, params);
	}
}
