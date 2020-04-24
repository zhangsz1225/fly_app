package com.taikesoft.fly.business.webapi;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.common.utils.TLog;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.util.Locale;

public class ApiHttpClient {

	private final static String HOST = "192.168.0.252";
	private static String DEV_API_URL = AppConfig.WEBSERVICE_URL + "/%s";
	private static String API_URL;
	private static AsyncHttpClient client;
	private final static int TIME_OUT = 0 * 1000;
	private final static int CONNECT_TIME_OUT = 5 * 1000;

	static {
		API_URL = DEV_API_URL;
	}

	public ApiHttpClient() {

	}

	public static AsyncHttpClient getHttpClient() {
		return client;
	}

	public static void setHttpClient(AsyncHttpClient c) {
		client = c;

		client.addHeader("Accept-Language", Locale.getDefault().toString());
		client.addHeader("Accept-Charset", "application/json;Charset=UTF-8");//application/json;
		client.addHeader("Host", HOST);
		client.addHeader("Connection", "Keep-Alive");
		client.setConnectTimeout(CONNECT_TIME_OUT);
		client.setResponseTimeout(TIME_OUT);

		setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
	}

	public static void resetHttpClient(/*AsyncHttpClient c*/) {
		/*if (SharedPreferencesManager.getBoolean(RequestStatus.HEADER_ADDED)) {
			return;
		}*/
		//client = c;
		client.addHeader("Accept-Language", Locale.getDefault().toString());
		client.addHeader("Accept-Charset", "UTF-8");//application/json;
		client.addHeader("Host", HOST);
		client.addHeader("Connection", "Keep-Alive");
		client.addHeader("application1","android-v1.0");
		client.addHeader("application2",SharedPreferencesManager.getString(ResultStatus.USER_ID));
		client.addHeader("application3", SharedPreferencesManager.getString(ResultStatus.TOKEN));
		client.addHeader("Content-Type","application/json");
		client.setConnectTimeout(CONNECT_TIME_OUT);
		client.setResponseTimeout(TIME_OUT);

		setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
		//SharedPreferencesManager.putBoolean(RequestStatus.HEADER_ADDED,true);
	}

	public static void resetHttpClientUploadImg(/*AsyncHttpClient c*/) {
		client.addHeader("Accept-Language", Locale.getDefault().toString());
		client.addHeader("Accept-Charset", "UTF-8");//application/json;
		client.addHeader("Host", HOST);
		client.addHeader("Connection", "Keep-Alive");
		client.addHeader("application1","android-v1.0");
		client.addHeader("application2",SharedPreferencesManager.getString(ResultStatus.USER_ID));
		client.addHeader("application3", SharedPreferencesManager.getString(ResultStatus.TOKEN));
		client.setConnectTimeout(CONNECT_TIME_OUT);
		client.setResponseTimeout(TIME_OUT);

		setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
	}

	public static void setUserAgent(String userAgent) {
		client.setUserAgent(userAgent);
	}

	public static void cancelAll(Context context) {
		client.cancelRequests(context, true);
	}

	public static void setApiUrl(String apiUrl) {
		API_URL = apiUrl;
	}

	public static void get(String partUrl, AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteApiUrl(partUrl), handler);
		TLog.log(new StringBuilder("GET ").append(partUrl).toString());
	}

	public static void get(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.get(getAbsoluteApiUrl(partUrl), params, handler);
		TLog.log(new StringBuilder("GET-URL:")
				.append(getAbsoluteApiUrl(getAbsoluteApiUrl(partUrl))).append("?").append(params)
				.toString());
	}

	public static void post(String partUrl, AsyncHttpResponseHandler handler) {
		client.post(getAbsoluteApiUrl(partUrl), handler);
		TLog.log(new StringBuilder("POST-URL ").append(getAbsoluteApiUrl(partUrl)).toString());
	}

	public static void post(String partUrl, RequestParams params,
			AsyncHttpResponseHandler handler) {
		client.post(getAbsoluteApiUrl(partUrl), params, handler);
		TLog.log(new StringBuilder("POST-URL:")
				.append(getAbsoluteApiUrl(partUrl)).append("?").append(params)
				.toString());
	}

	/*public static void postDebug(Context context, String partUrl,
			HttpEntity entity, String contentType,
			AsyncHttpResponseHandler handler) {
		if (context.getClass() == HomePageActivity.class) {
			 HttpContext httpContext = client.getHttpContext();
			 CookieStore cookies = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
			 if (cookies != null) {
				 for (Cookie c : cookies.getCookies()) {
					TLog.log("beforelogin "+c.getName()+":"+c.getValue());
				 }
			 } else {
				 TLog.log("info", "cookies is null");
			 }
			 PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
			 client.setCookieStore(myCookieStore);
			 TLog.log("info", "cookie" + myCookieStore);
		} else {
			 HttpContext myhttpContext = client.getHttpContext();
			 CookieStore myCookies = (CookieStore)myhttpContext .getAttribute(ClientContext.COOKIE_STORE);
			 if (myCookies != null) {
				 for (Cookie c : myCookies.getCookies()) {
		 			TLog.log(""+c.getName()+":"+c.getValue());
				 }
			 } else {
		 			TLog.log("info", "cookies is null");
			 }
		}
		try {
			client.post(context, getDebugAbsoluteApiUrl(partUrl), entity,
					contentType, handler);
			TLog.log(new StringBuilder("POST-URL:")
					.append(getDebugAbsoluteApiUrl(partUrl)).append("?")
					.append(entity.toString()).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/

	public static void post(Context context, String partUrl, HttpEntity entity,
							String contentType, AsyncHttpResponseHandler handler) {
		/*
		 * if (context.getClass() == LoginActivity.class) { HttpContext
		 * httpContext = client.getHttpContext(); CookieStore cookies =
		 * (CookieStore) httpContext .getAttribute(ClientContext.COOKIE_STORE);
		 * if (cookies != null) { for (Cookie c : cookies.getCookies()) {
		 * TLog.log("beforelogin "+c.getName()+":"+c.getValue()); } } else {
		 * TLog.log("info", "cookies is null"); } PersistentCookieStore
		 * myCookieStore = new PersistentCookieStore(context);
		 * client.setCookieStore(myCookieStore); TLog.log("info", "cookie" +
		 * myCookieStore); }else{ HttpContext myhttpContext =
		 * client.getHttpContext(); CookieStore myCookies = (CookieStore)
		 * myhttpContext .getAttribute(ClientContext.COOKIE_STORE); if
		 * (myCookies != null) { for (Cookie c : myCookies.getCookies()) {
		 * TLog.log(""+c.getName()+":"+c.getValue()); } } else {
		 * TLog.log("info", "cookies is null"); } }
		 */
		try {
			client.post(context, getAbsoluteApiUrl(partUrl), entity,
					contentType, handler);
			TLog.log(new StringBuilder("POST-URL:")
					.append(getAbsoluteApiUrl(partUrl)).append("?")
					.append(entity.toString()).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void postNull(Context context, String partUrl, HttpEntity entity,
							String contentType, AsyncHttpResponseHandler handler) {
		client.post(context, getAbsoluteApiUrl(partUrl), entity,
				contentType, handler);
	}

	public static void getDirect(String url, AsyncHttpResponseHandler handler) {
		new AsyncHttpClient().get(url, handler);
		TLog.log(new StringBuilder("GET ").append(url).toString());
	}

	public static String getAbsoluteApiUrl(String partUrl) {
		String url = String.format(API_URL, partUrl);
		TLog.log("URL:" + url);
		return url;
	}

	public static String getDebugAbsoluteApiUrl(String partUrl) {
		String url = String.format(AppConfig.WEBSERVICE_URL, partUrl);
		TLog.log("URL:" + url);
		return url;
	}

	/*
	 * public static void postData(){ AsyncHttpClient client=new
	 * AsyncHttpClient(); Request request = client.preparePost(your host URL).
	 * setHeader("Content-Type","application/json"). setHeader("Content-Length",
	 * ""
	 * +jo.toString().length()).setHeader("Authorization","Basic fgfgfgfhfhtetet="
	 * ). setBody(jo.toString()).build(); ListenableFuture<Response> r = null;
	 * //ListenableFuture<Integer> f= null; try{ r =
	 * client.executeRequest(request);
	 * System.out.println(r.get().getResponseBody()); }catch(IOException e){
	 * 
	 * } catch (InterruptedException e) { e.printStackTrace(); } catch
	 * (ExecutionException e) { e.printStackTrace(); } }
	 */

	private static BufferedReader in = null;


}
