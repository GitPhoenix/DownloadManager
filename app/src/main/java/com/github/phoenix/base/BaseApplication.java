package com.github.phoenix.base;

import android.app.Application;
import android.content.Context;

import com.github.phoenix.widget.DisplayToast;


public class BaseApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //初始化Toast
        DisplayToast.getInstance().init(getApplicationContext());
    }

    /**
     * 获取上下文
     * @return Context
     */
    public static Context getContext() {
        return context;
    }
}
