package com.jgkj.bxxccoach;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.jgkj.bxxccoach.tools.PictureOptimization;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by fangzhou on 2016/10/5.
 * 启动页，3秒展示
 */
public class WelcomeActivity extends Activity {
    private ImageView welcome_imageview;
    private PictureOptimization po;

    //初始化handler，处理视图
    private Handler handler = new Handler() {
        public void handlerMassage(android.os.Message msg) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    po = new PictureOptimization();
                    welcome_imageview.setBackgroundDrawable(PictureOptimization.bitmapToDrawble(PictureOptimization.decodeSampledBitmapFromResource(getResources(),
                            R.mipmap.vistorimage, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT), WelcomeActivity.this));
                }
            };
            timer.schedule(task, 3000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        init();
    }
    /**
     * 初始化控件，并读取教练信息和token值
     */
    private void init() {
        welcome_imageview = (ImageView) findViewById(R.id.welcome_imageview);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("Coach",Activity.MODE_PRIVATE);
                Intent intent = new Intent();
                SharedPreferences sp2 = getSharedPreferences("LoginSession",Activity.MODE_PRIVATE);
                Long currentTime = new Date().getTime();
                Long loginTime = sp2.getLong("sessionTime",currentTime);
                if(sp.getString("CoachInfo",null)==null||((currentTime-loginTime)>259200000)){
                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                }else{
                    SharedPreferences.Editor editor3 = sp2.edit();
                    editor3.putLong("sessionTime",currentTime);
                    editor3.commit();
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 3000);
    }
}

//┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃ 　
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃  神兽保佑　　　　　　　　
//    ┃　　　┃  代码无忧！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
// 	    ┃┫┫　┃┫┫
//      ┗┻┛　┗┻┛

