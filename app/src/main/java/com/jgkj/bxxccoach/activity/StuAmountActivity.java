package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.NoResultAction;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/12.
 * 设置最大可带学员数量，最大不可超过驾校所设定的最高人数
 */

public class StuAmountActivity extends Activity implements View.OnClickListener {
    //导航栏
    private TextView title;
    private Button back;
    //设置最大人数上限
    private EditText edit_amount;
    private TextView text_amount;
    private Button button_forward;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserInfo user;
    //当前学员数
    private int nowStuNum;
    private ProgressDialog dialog;
    //接受上个activity的传值
    private String maxNum;
    private String token;
    private String changeMaxUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/modifyMaxnum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stu_setting);
        initView();
        getNowCount();
    }

    /**
     * 获取登录状态个人信息
     */
    private void getNowCount() {
        sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        editor = sp.edit();
        Gson gson = new Gson();
        if (sp.getString("CoachInfo", null) != null) {
            user = gson.fromJson(sp.getString("CoachInfo", null), UserInfo.class);
            if (user.getCode() == 200) {
                nowStuNum = Integer.parseInt(user.getResult().getNowStuNum());
            }
        }
    }

    private void initView() {

        title = (TextView) findViewById(R.id.text_title);
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setVisibility(View.VISIBLE);
        edit_amount = (EditText) findViewById(R.id.maxNum_edit);
        text_amount = (TextView) findViewById(R.id.maxNum_text);

        Intent intent = getIntent();
        maxNum = intent.getStringExtra("maxNum");
        token = intent.getStringExtra("token");
        text_amount.setText(maxNum);

        title.setText("学员设置");
        back.setOnClickListener(this);

    }

    @Nullable
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                String str = button_forward.getText().toString();
                if (str.equals("编辑")) {
                    button_forward.setText("提交");
                    edit_amount.setVisibility(View.VISIBLE);
                    text_amount.setVisibility(View.GONE);
                    edit_amount.setText(text_amount.getText().toString());
                } else if (str.equals("提交")) {
                    button_forward.setText("编辑");
                    int text = Integer.parseInt(edit_amount.getText().toString());
                    if (text >= nowStuNum) {
                        dialog = ProgressDialog.show(StuAmountActivity.this, null, "修改中...");
                        text_amount.setVisibility(View.VISIBLE);
                        edit_amount.setVisibility(View.GONE);
                        text_amount.setText(text + "");
                        changeMax(text + "");
                    } else {
                        Toast.makeText(StuAmountActivity.this, "抱歉，您填写的学员不能低于当前所带人数！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * 修改最大人数上限
     */
    private void changeMax(String maxnum) {
        OkHttpUtils
                .post()
                .url(changeMaxUrl)
                .addParams("pid", user.getResult().getPid())
                .addParams("maxnum", maxnum)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(StuAmountActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        NoResultAction res = gson.fromJson(s, NoResultAction.class);
                        Toast.makeText(StuAmountActivity.this, res.getReason(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

}
