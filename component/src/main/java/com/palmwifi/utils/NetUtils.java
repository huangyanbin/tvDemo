package com.palmwifi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class NetUtils {

	// 判断网络连接状态
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断wifi状态
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断移动网络
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 获取连接类型
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 设置网络
	 * 
	 * @param paramContext
	 */
	public static void startToSettings(Context paramContext) {
		if (paramContext == null)
			return;
		try {
			if (Build.VERSION.SDK_INT > 10) {
				paramContext.startActivity(new Intent(
						"android.settings.SETTINGS"));
				return;
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			return;
		}
		paramContext.startActivity(new Intent(
				"android.settings.WIRELESS_SETTINGS"));
	}

	/**
	 * 获取当前网络状态
	 **/
	public static String getNetState(Context context){
		try {
			ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//mobile 3G Data Network
			NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			String mobilestate = mobile.toString(); //3G网络连接状态
			//wifi
			NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			String wifistate = wifi.toString(); //wifi连接状态
			if(mobilestate.equals("CONNECTED")) {
				return "mobile";
			} else if(wifistate.equals("CONNECTED")) {
				return "wifi";
			} else {
				return "null";
			}
		} catch (Exception e) {
			return "avd";
		}
	}
}
