package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxccoach.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by tongshoujun on 2017/6/6.
 */

public class ModifyLoginPasswordActivity extends Activity {

    //标题
    private TextView title,tv_account;
    private Button button_backward;
    private EditText et_old_password;
    private EditText et_new_password;
    private Button btn_confirm_modify;
    private UserInfo userInfo;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_login_password);

        SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("修改密码");
        tv_account = (TextView)findViewById(R.id.tv_account);
        tv_account.setText(userInfo.getResult().getPhonenum());
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_old_password = (EditText)findViewById(R.id.et_old_password);
        et_new_password = (EditText)findViewById(R.id.et_new_password);
        btn_confirm_modify = (Button)findViewById(R.id.btn_confirm_modify);

        btn_confirm_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_old_password.getText().toString() == null || "".equals(et_old_password.getText().toString())){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"旧密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_new_password.getText().toString() == null || "".equals(et_new_password.getText().toString())){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"新密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_old_password.length() < 6){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"旧密码位数小于6",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_new_password.length() < 6){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"新密码位数小于6",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_old_password.length() >16){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"旧密码位数大于16",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_new_password.length() >16){
                    Toast.makeText(ModifyLoginPasswordActivity.this,"新密码位数大于16",Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog = ProgressDialog.show(ModifyLoginPasswordActivity.this, null, "请求中...");
                getData(userInfo.getResult().getPid(),userInfo.getResult().getToken(),et_old_password.getText().toString(),et_new_password.getText().toString(),userInfo.getResult().getPhonenum(), Urls.modifyCoachPasswd);
            }
        });

    }

    //修改密码
    private void getData(String pid,String token,String password,String repassword,String phone,String url) {
        Log.i("百信学车","修改登录密码参数" + "pid=" + pid + "   token=" + token + "   password=" + password + "   repassword=" + repassword + "   phone=" + phone + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("passwd", password)
                .addParams("repasswd", repassword)
                .addParams("phone", phone)
                .addParams("token", token)
                .addParams("pid", pid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(ModifyLoginPasswordActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Log.i("百信学车","修改登录密码结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s, BaseEntity.class);
                        if (baseEntity.getCode() == 200) {
                            Toast.makeText(ModifyLoginPasswordActivity.this, "修改登录密码成功", Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(ModifyLoginPasswordActivity.this, baseEntity.getReason(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
