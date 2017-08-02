package com.jgkj.bxxccoach.tools;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.jgkj.bxxccoach.xfutils.SpeechSynthesizerUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fangzhou on 2017/2/14.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppUtility.setContext(this);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        SpeechSynthesizerUtil.initSpeech(this);            //科大讯飞语音合成功能，初始化

        // 置入一个不设防的VmPolicy（不设置的话 7.0以上一调用拍照功能就崩溃了）
        // 还有一种方式：manifest中加入provider然后修改intent代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
}
