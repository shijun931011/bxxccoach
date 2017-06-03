package com.jgkj.bxxccoach.tools;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fangzhou on 2017/2/14.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
}
