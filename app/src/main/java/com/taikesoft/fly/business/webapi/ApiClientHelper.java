package com.taikesoft.fly.business.webapi;


import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.context.AppContext;

public class ApiClientHelper {
	public static String getUserAgent(AppContext appContext) {
		StringBuilder ua = new StringBuilder(AppConfig.WEBSERVICE_URL);
		return ua.toString();
	}
}
