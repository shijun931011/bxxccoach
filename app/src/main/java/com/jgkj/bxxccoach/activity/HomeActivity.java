package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.Version;
import com.jgkj.bxxccoach.fragment.BxxcFragment;
import com.jgkj.bxxccoach.fragment.PersonalFragment;
import com.jgkj.bxxccoach.fragment.ScheduleFragment;
import com.jgkj.bxxccoach.tools.CallDialog;
import com.jgkj.bxxccoach.tools.GetVersion;
import com.jgkj.bxxccoach.tools.JPushDataUitl;
import com.jgkj.bxxccoach.tools.UpdateManger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class HomeActivity extends FragmentActivity implements View.OnClickListener{
    private ViewPager mviewPager;
    private FragmentPagerAdapter madapter;
    private List<Fragment> mfragments;
    private LinearLayout mtabBxxc;
    private LinearLayout mtabSchedule;
    private LinearLayout mtabPersonal;
    private ImageView mtabBxxcImg;
    private ImageView mtabScheduleImg;
    private ImageView mtabPersonalImg;
    private TextView  mtabBxxcTxt;
    private TextView mtabScheduleTxt;
    private TextView mtabPersonalTxt;
    private TextView title;
    private Button button_forward;
    private ImageView im_phone;
    private UserInfo userInfo;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserInfo user;

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    //jPush推送
    private EditText msgText;
    public static boolean isForeground = false;

    //版本更新，进入App后自动检测，如果有新版本会自动显示弹框
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/versionAndroidCoach";
    private String refreashUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/coachRefresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        registerMessageReceiver();
        initEvent();
        //默认初始化显示第一个Tab标签
        setSelect(0);
        checkSoftInfo();
    }
    private void initEvent(){
        mtabBxxc.setOnClickListener(this);
        mtabSchedule.setOnClickListener(this);
        mtabPersonal.setOnClickListener(this);
        mviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当滑动到一个viewPager页时，切换到对应的Tab
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mtabBxxc = (LinearLayout) findViewById(R.id.tab_bxxc);
        mtabSchedule = (LinearLayout) findViewById(R.id.tab_schedule);
        mtabPersonal = (LinearLayout) findViewById(R.id.tab_personal);
        mtabBxxcImg = (ImageView) findViewById(R.id.tab_bxxc_img);
        mtabScheduleImg = (ImageView) findViewById(R.id.tab_schedule_img);
        mtabPersonalImg = (ImageView) findViewById(R.id.tab_personal_img);
        mtabBxxcTxt = (TextView) findViewById(R.id.tab_bxxc_txt);
        mtabScheduleTxt = (TextView) findViewById(R.id.tab_schedule_txt);
        mtabPersonalTxt = (TextView) findViewById(R.id.tab_personal_txt);
        mviewPager = (ViewPager) findViewById(R.id.viewPager);
        title = ((TextView) findViewById(R.id.text_title));
        button_forward = (Button)findViewById(R.id.button_forward);
        im_phone = (ImageView)findViewById(R.id.im_phone);
        im_phone.setOnClickListener(this);

        //初始化三个Tab对应的Fragment
        mfragments = new ArrayList<Fragment>();
        Fragment msportsFragment = new BxxcFragment();
        Fragment mbarrageFragment = new ScheduleFragment();
        Fragment mPersonalCenterFragment = new PersonalFragment();
        mfragments.add(msportsFragment);
        mfragments.add(mbarrageFragment);
        mfragments.add(mPersonalCenterFragment);
        madapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mfragments.get(position);
            }

            @Override
            public int getCount() {
                return mfragments.size();
            }
        };
        mviewPager.setAdapter(madapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_bxxc:
                setSelect(0);
                break;
            case R.id.tab_schedule:
                setSelect(1);
                break;
            case R.id.tab_personal:
                setSelect(2);
                break;
            case R.id.im_phone:
                new CallDialog(HomeActivity.this, "0551-65555744").call();
                break;

        }
    }
    /**
     * 当点击Tab时，将对应的Tab图片变为亮色并显示对应的viewPager页
     *
     * @param i 选中的Tab对应的索引
     */
    private void setSelect(int i) {
        setTab(i);
        mviewPager.setCurrentItem(i);
    }

    /**
     * 将选中的Tab对应的图片变亮
     *
     * @param i 选中的Tab对应的索引
     */
    private void setTab(int i) {
        resertImg();
        switch (i) {
            case 0:
                mtabBxxcImg.setImageResource(R.drawable.main_selected);
                mtabBxxcTxt.setTextColor(this.getResources().getColor(R.color.themeColor));
                title.setText("首页");
                button_forward.setVisibility(View.GONE);
                im_phone.setVisibility(View.VISIBLE);
                break;
            case 1:
                mtabScheduleImg.setImageResource(R.drawable.class_selected);
                mtabScheduleTxt.setTextColor(this.getResources().getColor(R.color.themeColor));
                title.setText("我的课表");
                im_phone.setVisibility(View.GONE);

                sp = getApplication().getSharedPreferences("Coach", Activity.MODE_PRIVATE);
                editor = sp.edit();
                Gson gson = new Gson();

                if(sp.getString("CoachInfo",null)!=null){
                    user = gson.fromJson(sp.getString("CoachInfo",null),UserInfo.class);
                }

                //中心或者学校管理者显示
                if("2".equals(user.getResult().getRoles()) || "3".equals(user.getResult().getRoles())){
                    button_forward.setText("团队成员");
                    button_forward.setVisibility(View.VISIBLE);
                    button_forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(HomeActivity.this,TeamMemberActivity.class);
                            intent.putExtra("flag","HomeActivity");//设个标识
                            startActivity(intent);
                        }
                    });
                }else{
                    button_forward.setVisibility(View.GONE);
                }
                break;
            case 2:
                im_phone.setVisibility(View.GONE);
                button_forward.setVisibility(View.GONE);
                mtabPersonalImg.setImageResource(R.drawable.me_selected);
                mtabPersonalTxt.setTextColor(this.getResources().getColor(R.color.themeColor));
                title.setText("教练");
                break;
        }
    }

    /**
     * 将所有Tab图片重置为暗色
     */
    private void resertImg() {
        mtabBxxcImg.setImageResource(R.drawable.main_unselected);
        mtabBxxcTxt.setTextColor(this.getResources().getColor(R.color.gray));
        mtabScheduleImg.setImageResource(R.drawable.class_unselect);
        mtabScheduleTxt.setTextColor(this.getResources().getColor(R.color.gray));
        mtabPersonalImg.setImageResource(R.drawable.me_unselect);
        mtabPersonalTxt.setTextColor(this.getResources().getColor(R.color.gray));
    }

    /**
     * 下面几个方法为监听手机的返回键，
     * 点击一次后会提示在点击一次退出应用
     */
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    //当Activity重新启动后
    @Override
    protected void onRestart() {
//        if (isLogin) {
//            refresh(user.getResult().getPid());
//        } else {
//            Toast.makeText(HomeActivity.this, "暂未登录", Toast.LENGTH_SHORT).show();
//        }
        super.onRestart();
    }

    //按键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 检查更新
     */
    public void checkSoftInfo() {
        OkHttpUtils
                .get()
                .url(versionUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(HomeActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Version version = gson.fromJson(s, Version.class);
                        if (version.getCode() == 200) {
                            if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(HomeActivity.this)) {
                                UpdateManger updateManger = new UpdateManger(HomeActivity.this,
                                        version.getResult().get(0).getPath(), version.getResult().get(0).getVersionName());
                                updateManger.checkUpdateInfo();
                            }
                        }
                    }
                });
    }

    //生命周期用来配置JPush推送
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    //for receive customer msg from jpush server
    private HomeActivity.MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.jgkj.bxxc.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new HomeActivity.MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!JPushDataUitl.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg) {
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }
    }

}
