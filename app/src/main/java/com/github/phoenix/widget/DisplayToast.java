package com.github.phoenix.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.phoenix.R;


/**
 * 自定义toast, 使用的时候请在Application 中进行初始化
 *
 * @author Phoenix
 * @date 2016-8-3 15:30
 */
public class DisplayToast {
	private Toast toast;
	private TextView tvToast;

	/**
	 * 不能实例化
	 */
	private DisplayToast() {}
	
	
	public static DisplayToast getInstance(){
		return DisplayToastHolder.INSTANCE;
	}

	/**
	 * 用静态内部类实现单列
	 */
	private static class DisplayToastHolder {
		private static final DisplayToast INSTANCE = new DisplayToast();
	}


	/**
	 * 在应用Application中初始化
	 *
	 * @param context 上下文用Context
     */
	public void init(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
		tvToast = (TextView) view.findViewById(R.id.tv_toast);
		//初始化Toast并把View设置给它
		toast = new Toast(context);
		toast.setView(view);
	}
	
	public void display(CharSequence content, int duration) {
		if (TextUtils.isEmpty(content)) {
			return;
		}
		tvToast.setText(content);
		//设置显示时间
		toast.setDuration(duration);
		toast.show();
	}
	
	public void display(int resId, int duration) {
		tvToast.setText(resId);
		toast.setDuration(duration);
		toast.show();
	}

	/**
	 * 取消Toast
	 */
	public void dismiss() {
		toast.cancel();
	}
}
