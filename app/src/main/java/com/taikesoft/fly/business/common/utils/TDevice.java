package com.taikesoft.fly.business.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.text.InputType;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;

import com.taikesoft.fly.business.context.AppContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TDevice {

	public static int getWindowsWidth(Activity activity) {
		//获取屏幕高度
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	public static boolean hasInternet() {
		return true;
	}

	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	public static int getWidthPixels(Context context) {
		//return context.getResources().getDisplayMetrics().widthPixels;
		return AppContext.mResource.getDisplayMetrics().widthPixels;
	}

	public static int getHeightPixels(Context context) {
		//return context.getResources().getDisplayMetrics().heightPixels;
		return AppContext.mResource.getDisplayMetrics().heightPixels;
	}

	public static float getDensity(Context context) {
		//return context.getResources().getDisplayMetrics().density;
		return AppContext.mResource.getDisplayMetrics().density;
	}

	public static boolean setWifiEnabled(Context context, boolean enabled) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);
		return wifiManager.isWifiEnabled();
	}

	public static void hideSoftInputMethod(Activity activity, EditText et){

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		String methodName=null;
		int currentVersion=android.os.Build.VERSION.SDK_INT;
		if(currentVersion>=16){
			//4.2
			methodName="setShowSoftInputOnFocus";//
		}else if(currentVersion>=14){
			//4.0
			methodName="setSoftInputShownOnFocus";
		}

		if(methodName==null){
			et.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls=EditText.class;
			Method setShowSoftInputOnFocus;
			try{
				setShowSoftInputOnFocus=cls.getMethod(methodName,boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(et,false);
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//得到状态栏的高度
	public static int getStatusBarHeight(Context context){
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public static int getOSVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}
}
