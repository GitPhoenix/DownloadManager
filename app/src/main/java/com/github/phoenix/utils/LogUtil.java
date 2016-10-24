package com.github.phoenix.utils;

import android.util.Log;

/**
 * 对Log的封装
 *
 * @author Phoenix
 * @date 2016-8-3 15:08
 */
public final class LogUtil {
	private static final Boolean IS_SHOW = true;
	private static final String TAG = "LOG";
	
	private LogUtil(){}
	
	/**
	 * 默认tag 为LOG
	 * @param text
	 */
	public static void i(String text){
		if (IS_SHOW) {
			Log.i(TAG, text);
		}
	}
	
	/**
	 * 默认tag 为LOG
	 * @param text
	 */
	public static void e(String text){
		if (IS_SHOW) {
			Log.e(TAG, text);
		}
	}
	
	/**
	 * 默认tag 为LOG
	 * @param text
	 */
	public static void d(String text){
		if (IS_SHOW) {
			Log.d(TAG, text);
		}
	}
	
	/**
	 * 默认tag 为LOG
	 * @param text
	 */
	public static void v(String text){
		if (IS_SHOW) {
			Log.v(TAG, text);
		}
	}
	
	/**
	 * 自定义tag
	 * @param tag
	 * @param text
	 */
	public static void i(String tag, String text){
		if (IS_SHOW) {
			Log.i(tag, text);
		}
	}
	
	/**
	 * 自定义tag
	 * @param tag
	 * @param text
	 */
	public static void e(String tag, String text){
		if (IS_SHOW) {
			Log.e(tag, text);
		}
	}
	
	/**
	 * 自定义tag
	 * @param tag
	 * @param text
	 */
	public static void d(String tag, String text){
		if (IS_SHOW) {
			Log.d(tag, text);
		}
	}
	
	/**
	 * 自定义tag
	 * @param tag
	 * @param text
	 */
	public static void v(String tag, String text){
		if (IS_SHOW) {
			Log.v(tag, text);
		}
	}
	
}
