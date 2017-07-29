package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.tools.JPushDataUitl;
import com.jgkj.bxxccoach.tools.Md5;
import com.jgkj.bxxccoach.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

/**
 * Created by fangzhou on 2016/10/25.
 * 登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button login_btn;
    private EditText username, password;
    private TextView changePwd_id;
    private TextView callback;
    private ProgressDialog dialog;
    private UserInfo userInfo;
    private static final String TAG = "JPush";
    private Button btn_clear_phone_text,btn_clear_pwd_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }
    private void init() {
        callback = (TextView) findViewById(R.id.callback_id);
        callback.setOnClickListener(this);
        changePwd_id = (TextView) findViewById(R.id.changePwd_id);
        changePwd_id.setOnClickListener(this);
        username = (EditText) findViewById(R.id.coachName);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_button_id);
        login_btn.setOnClickListener(this);

        btn_clear_phone_text = (Button) findViewById(R.id.btn_clear_phone_text);
        btn_clear_phone_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.getText().clear();
            }
        });
        btn_clear_pwd_text = (Button) findViewById(R.id.btn_clear_pwd_text);
        btn_clear_pwd_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.getText().clear();
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = username.getText().length();
                if (textLength > 0){
                    btn_clear_phone_text.setVisibility(View.VISIBLE);
                    btn_clear_pwd_text.setVisibility(View.GONE);
                }else{
                    btn_clear_phone_text.setVisibility(View.GONE);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = password.getText().length();
                if (textLength > 0){
                    btn_clear_pwd_text.setVisibility(View.VISIBLE);
                    btn_clear_phone_text.setVisibility(View.GONE);
                }else{
                    btn_clear_pwd_text.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_id:
                String name = username.getText().toString();
                String pwd = password.getText().toString();
                String passmd5 = Md5.md5(pwd);
                String encryptmd5 = Md5.md5(passmd5+name);

                if (name.equals("") || name == null) {
                    Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                } else if (pwd.equals("") || pwd == null) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = ProgressDialog.show(LoginActivity.this, null, "登录中...");
                    login(name,encryptmd5);
                }

                break;
            case R.id.changePwd_id:
                Toast.makeText(LoginActivity.this, "百信学车教练端暂时不提供注册方式，详情请登录百信学车官方网站", Toast.LENGTH_SHORT).show();
                break;
            case R.id.callback_id:
                Intent intent2 = new Intent();
                intent2.setClass(LoginActivity.this,CallbackActivity.class);
                startActivity(intent2);
                break;
        }
    }

    /**
     * 登录请求
     */
    private void login(String phone,String encryptmd5) {
            OkHttpUtils
                    .post()
                    .url(Urls.coachLogin)
                    .addParams("phone", phone)
                    .addParams("passwd",encryptmd5)                //
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(String s, int i) {
                            Log.i("百信学车","登录结果" + s);
                            login_btn.setTag(s);
                            if(login_btn.getTag()!=null){
                                setData();
                            }
                        }
                    });
    }

    /**
     * 解析数据
     */
    private void setData() {
        String str = login_btn.getTag().toString();
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);
        dialog.dismiss();
        if(userInfo.getCode()==200){
            SharedPreferences sp = getSharedPreferences("Coach",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("CoachInfo",str);
            editor.commit();
            SharedPreferences sp2 = getSharedPreferences("LoginSession",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sp2.edit();
            editor2.putLong("sessionTime",new Date().getTime());
            editor2.commit();

            login_btn.setTag(userInfo.getResult().getPid());
            if(login_btn.getTag()!=null){
                setTag();
                setAlias();
            }

            SharedPreferences sp1 = getSharedPreferences("CoachToken",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sp1.edit();
            editor1.putString("CoachToken",userInfo.getResult().getToken());
            editor1.commit();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(LoginActivity.this,userInfo.getReason(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * JPush设置tag
     */
    private void setTag() {
        String tag = login_btn.getTag().toString();

        if (TextUtils.isEmpty(tag)) {
            return;
        }
        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!JPushDataUitl.isValidTagAndAlias(sTagItme)) {
                return;
            }
            tagSet.add(sTagItme);
        }
        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));
    }

    /**
     * JPush设置alias
     */
    private void setAlias() {
        String alias = login_btn.getTag().toString();
        if (TextUtils.isEmpty(alias)) {
            return;
        }
        if (!JPushDataUitl.isValidTagAndAlias(alias)) {
            return;
        }
        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    /**
     * JPush设置handler
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    /**
     * JPush  tag和alias回调接口，alias回调
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JPushDataUitl.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }

    };
    /**
     * JPush  tag和alias回调接口，tag回调
     */
    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JPushDataUitl.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

        }

    };
    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(LoginActivity.this);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
        Toast.makeText(LoginActivity.this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }


    /**
     * 设置通知栏样式 - 定义通知栏Layout
     */
    private void setStyleCustom() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(LoginActivity.this, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_launcher;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
        Toast.makeText(LoginActivity.this, "Custom Builder - 2", Toast.LENGTH_SHORT).show();
    }




}
