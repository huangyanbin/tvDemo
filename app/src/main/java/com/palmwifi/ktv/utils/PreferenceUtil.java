package com.palmwifi.ktv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.palmwifi.ktv.comm.KApplication;


/**
 * @author huangyanbin
 * 
 */

public class PreferenceUtil {


	public static final String PREFERENCE_NAME = "ylwx";
	
	private static SharedPreferences mSharedPreferences  = KApplication.getInstance()
			.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);




	/**
	 * 打印所有
	 */
	public static void print() {
		System.out.println(mSharedPreferences.getAll());
	}

	/**
	 * 清空保存在默认SharePreference下的所有数据
	 */
	public static void clear() {
		mSharedPreferences.edit().clear().commit();
	}

	/**
	 * 保存字符串
	 * 
	 * @return
	 */
	public static void putString(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}

	/**
	 * 读取字符串
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		return mSharedPreferences.getString(key, null);

	}

	/**
	 * 保存整型值
	 * 
	 * @return
	 */
	public static void putInt(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}

	/**
	 * 读取整型值
	 * 
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		return mSharedPreferences.getInt(key, 0);
	}
	public static int getInt(String key,int defaultValue) {
		return mSharedPreferences.getInt(key, defaultValue);
	}



	/**
	 * 保存布尔值
	 * 
	 * @return
	 */
	public static void putBoolean(String key, Boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static void clear(String key){
		mSharedPreferences.edit().remove(key).commit();
	}

	public static void putLong(String key, long value) {
		mSharedPreferences.edit().putLong(key, value).commit();
	}

	public static long getLong(String key) {
		return mSharedPreferences.getLong(key, 0);
	}

	/**
	 * t 读取布尔值
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key, boolean defValue) {
		return mSharedPreferences.getBoolean(key, defValue);

	}

	/**
	 * 移除字段
	 * 
	 * @return
	 */
	public static void removeString(String key) {
		mSharedPreferences.edit().remove(key).commit();
	}

}
