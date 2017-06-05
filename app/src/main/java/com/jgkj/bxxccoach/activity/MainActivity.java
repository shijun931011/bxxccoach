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
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.NoResultAction;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.Version;
import com.jgkj.bxxccoach.tools.CallDialog;
import com.jgkj.bxxccoach.tools.GetVersion;
import com.jgkj.bxxccoach.tools.JPushDataUitl;
import com.jgkj.bxxccoach.tools.RefreshLayout;
import com.jgkj.bxxccoach.tools.UpdateManger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MainActivity extends Activity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private TextView myAccount;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    //刷新控件
    private RefreshLayout swipeLayout;

    private LinearLayout myStu, credit, teach, wait;
    private TextView mySubject, stuAppraise, goodpraise, pass;
    private TextView mySet, name, maxCurrentSet, total_num, customer;
    private ImageView head;
    private SharedPreferences sp;
    private String token;
    private SharedPreferences.Editor editor;
    private UserInfo user;
    private UserInfo.Result userInfo;
    private String userPhone;
    private LinearLayout.LayoutParams wrapParams;
    private boolean isLogin = false;
    private TextView mCurrentNum;

    //jPush推送
    private EditText msgText;
    public static boolean isForeground = false;

    //版本更新，进入App后自动检测，如果有新版本会自动显示弹框
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/versionAndroidCoach";
    private String refreashUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/coachRefresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setData();
        checkSoftInfo();
    }

    /**
     * 填充数据
     */
    private void setData() {
        sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        SharedPreferences sp1 = getSharedPreferences("CoachToken", Activity.MODE_PRIVATE);
        token = sp1.getString("CoachToken", null);

        editor = sp.edit();
        Gson gson = new Gson();
        if (sp.getString("CoachInfo", null) != null) {
            isLogin = true;
            user = gson.fromJson(sp.getString("CoachInfo", null), UserInfo.class);
            if (user.getCode() == 200) {
                userInfo = user.getResult();
                String path = userInfo.getCoafile();
                if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                        !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
                    Glide.with(MainActivity.this).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(head);
                } else {
                    Glide.with(MainActivity.this).load(path).placeholder(R.drawable.defaultimg).error(R.drawable.defaultimg).into(head);
                }
                name.setText(userInfo.getCname());
                pass.setText("通过率：" + userInfo.getPass() + "%");
                goodpraise.setText("好评率：" + userInfo.getPass() + "%");
                maxCurrentSet.setText(userInfo.getNowStuNum() + "/" + userInfo.getMaxnum() + "人");
                mCurrentNum.setHint("当前学员数： " + userInfo.getNowStuNum() + "/" +
                        userInfo.getMaxnum() + "  累计学员数为： " + userInfo.getCount_stu() + " 人");

                credit.removeAllViews();
                teach.removeAllViews();
                wait.removeAllViews();
                int creditnum = Integer.parseInt(userInfo.getCredit());
                for (int i = 0; i < creditnum; i++) {
                    ImageView image = new ImageView(this);
                    image.setBackgroundResource(R.drawable.xin_1);
                    wrapParams = new LinearLayout.LayoutParams(25, 25);
                    image.setLayoutParams(wrapParams);
                    credit.addView(image);
                }
                int teachnum = Integer.parseInt(userInfo.getTeach());
                for (int i = 0; i < teachnum; i++) {
                    ImageView image = new ImageView(this);
                    image.setBackgroundResource(R.drawable.star1);
                    wrapParams = new LinearLayout.LayoutParams(25, 25);
                    image.setLayoutParams(wrapParams);
                    teach.addView(image);
                }
                int waitnum = Integer.parseInt(userInfo.getWait());
                for (int i = 0; i < waitnum; i++) {
                    ImageView image = new ImageView(this);
                    image.setBackgroundResource(R.drawable.star1);
                    wrapParams = new LinearLayout.LayoutParams(25, 25);
                    image.setLayoutParams(wrapParams);
                    wait.addView(image);
                }
            } else {
                Toast.makeText(MainActivity.this, user.getReason(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {
        myAccount = (TextView) findViewById(R.id.myAccount);
        myAccount.setOnClickListener(this);
        myStu = (LinearLayout) findViewById(R.id.myStu);
        myStu.setOnClickListener(this);
        mySubject = (TextView) findViewById(R.id.mySubject);
        mySubject.setOnClickListener(this);
        stuAppraise = (TextView) findViewById(R.id.stuAppraise);
        stuAppraise.setOnClickListener(this);
        mySet = (TextView) findViewById(R.id.mySet);
        mySet.setOnClickListener(this);
        customer = (TextView) findViewById(R.id.customer);
        customer.setOnClickListener(this);
        mCurrentNum = (TextView) findViewById(R.id.mCurrentNum);
        credit = (LinearLayout) findViewById(R.id.credit);
        teach = (LinearLayout) findViewById(R.id.teach);
        goodpraise = (TextView) findViewById(R.id.goodpraise);
        pass = (TextView) findViewById(R.id.pass);
        maxCurrentSet = (TextView) findViewById(R.id.maxCurrentSet);
        name = (TextView) findViewById(R.id.name);
        wait = (LinearLayout) findViewById(R.id.wait);
        total_num = (TextView) findViewById(R.id.total_num);
        head = (ImageView) findViewById(R.id.head);
        //下拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.refresh);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.myAccount:
                if (isLogin) {
                    Toast.makeText(MainActivity.this, "该功能暂时没有上线，请耐心等待！", Toast.LENGTH_SHORT).show();
//                    intent.setClass(MainActivity.this,MyAccountActivity.class);
//                    startActivity(intent);
                } else {
                    Intent login = new Intent();
                    login.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                }

                break;
            case R.id.myStu:
                if (isLogin) {
                    intent.setClass(MainActivity.this, StuCenterActivity.class);
                    intent.putExtra("maxNum", userInfo.getMaxnum());
                    intent.putExtra("token", token);
                    startActivity(intent);
                } else {
                    Intent login = new Intent();
                    login.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                break;
            case R.id.mySubject:
                if (isLogin) {
                    intent.setClass(MainActivity.this, MySubjectActivity.class);
                    intent.putExtra("pid", userInfo.getPid());
                    intent.putExtra("token", token);
                    startActivity(intent);
                } else {
                    Intent login = new Intent();
                    login.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                break;
            case R.id.stuAppraise:
                if (isLogin) {
                    intent.setClass(MainActivity.this, StuAppraiseActivity.class);
                    intent.putExtra("pid", userInfo.getPid());
                    startActivity(intent);
                } else {
                    Intent login = new Intent();
                    login.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                break;
            case R.id.mySet:
                intent.setClass(MainActivity.this, MySetActivity.class);
                if (isLogin) {
                    intent.putExtra("name", userInfo.getCname());
                    intent.putExtra("phone", userInfo.getPhonenum());
                    intent.putExtra("islogin", isLogin);
                } else {
                    intent.putExtra("name", "");
                    intent.putExtra("phone", "");
                    intent.putExtra("islogin", isLogin);
                }
                startActivity(intent);
                break;
            case R.id.customer:
                new CallDialog(MainActivity.this, "055165555744").call();
                break;
        }
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
        if (isLogin) {
            refresh(user.getResult().getPid());
        } else {
            Toast.makeText(MainActivity.this, "暂未登录", Toast.LENGTH_SHORT).show();
        }
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
    //页面刷新，下拉刷新和重新启动Activity刷新
    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh(user.getResult().getPid());
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
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
                        Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Version version = gson.fromJson(s, Version.class);
                        if (version.getCode() == 200) {
                            if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(MainActivity.this)) {
                                UpdateManger updateManger = new UpdateManger(MainActivity.this,
                                        version.getResult().get(0).getPath(), version.getResult().get(0).getVersionName());
                                updateManger.checkUpdateInfo();
                            }
                        }
                    }
                });
    }

    /**
     * 刷新
     */
    private void refresh(String pid) {
        OkHttpUtils
                .post()
                .url(refreashUrl)
                .addParams("pid", pid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        NoResultAction res = gson.fromJson(s, NoResultAction.class);
                        Toast.makeText(MainActivity.this, res.getReason(), Toast.LENGTH_SHORT).show();
                        if (res.getCode() == 200) {
                            SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("CoachInfo", s);
                            editor.commit();
                            setData();
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
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.jgkj.bxxc.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
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
