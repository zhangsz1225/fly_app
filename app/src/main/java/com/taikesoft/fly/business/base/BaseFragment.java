package com.taikesoft.fly.business.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.utils.TLog;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

	private LinearLayout llLoading;
	private TextView tvLoading;
	private ProgressBar pbLoading;
	private ImageView ivNoData;

	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutRes(), container, false);
		TLog.log("fragment");
		initView(view);
		if (isAutoInit()) {
			initCache();
			initWebData();
		}
		setListener();
		return view;
	}

	protected abstract void initView(View view);

	protected abstract int getLayoutRes();

	protected void saveCache() {

	}

	public void initWebData() {

	}

	protected void initCache() {

	}

	protected void setListener() {

	}

	protected boolean isAutoInit() {
		return true;
	}

	protected boolean isCanPtr() {
		return false;
	}


	public void refreshWebData() {

	}
	
	public void requestFinish() {
		
	}

	public void showHeader() {

	}

	protected boolean hasLoading() {
		return false;
	}

	protected void initLoading(String msg, View v) {
		try {
			if (hasLoading()) {
				llLoading = (LinearLayout) v.findViewById(R.id.ll_loading);
				llLoading.setVisibility(View.VISIBLE);
				tvLoading = (TextView) v.findViewById(R.id.tv_loading);
				tvLoading.setText(msg);
				ivNoData = (ImageView)v.findViewById(R.id.iv_no_data);
				//ivNoData.setVisibility(View.GONE);
				pbLoading = (ProgressBar) v.findViewById(R.id.pb_loading);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void loadError(String msg, boolean doAgain){
		if(llLoading != null 
				&& llLoading.getVisibility() == View.VISIBLE){
			pbLoading.setVisibility(View.GONE);
			tvLoading.setText(msg);
			if(doAgain){
				llLoading.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadAgain();
					}
				});
			}
		}
	}

	protected void loadAgain(){
		if(llLoading != null){
			pbLoading.setVisibility(View.VISIBLE);
			tvLoading.setText("正在加载...");
			llLoading.setClickable(false);
			refreshWebData();
		}
	}
	
	protected void hideLoading() {
		if (llLoading != null && llLoading.getVisibility() == View.VISIBLE) {
			System.out.println("hide");
			llLoading.setVisibility(View.GONE);
		}

	}
	
	/*protected void initLoading(String msg) {
		try {
			if (hasLoading()) {
				llLoading = (LinearLayout) findViewById(R.id.ll_loading);
				llLoading.setVisibility(View.VISIBLE);
				tvLoading = (TextView) findViewById(R.id.tv_loading);
				tvLoading.setText(msg);
				pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
		super.dump(prefix, fd, writer, args);
	}
}
