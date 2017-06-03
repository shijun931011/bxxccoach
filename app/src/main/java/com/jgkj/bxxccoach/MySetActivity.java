package com.jgkj.bxxccoach;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.bean.Version;
import com.jgkj.bxxccoach.tools.GetVersion;
import com.jgkj.bxxccoach.tools.GlideCacheUtil;
import com.jgkj.bxxccoach.tools.UpdateManger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/13.
 * <p>
 * 我的设置
 */

public class MySetActivity extends Activity implements View.OnClickListener {
    private Button back;
    private TextView title,name,phone,cleanUp,versionCode,changepwd;
    private Button exit;
    private String coachName;
    private String coachPhone;
    private boolean isLogin;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Dialog sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    //版本更新，点击版本号，与后台进行版本信息的交互
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/versionAndroidCoach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysetting);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("设置");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        phone = (TextView) findViewById(R.id.phone);
        name = (TextView) findViewById(R.id.name);
        cleanUp = (TextView) findViewById(R.id.cleanUp);
        changepwd = (TextView) findViewById(R.id.changepwd);
        changepwd.setOnClickListener(this);

        cleanUp.setOnClickListener(this);
        exit = (Button) findViewById(R.id.exit);
        versionCode = (TextView) findViewById(R.id.versionCode);
        versionCode.setText(GetVersion.getVersion(MySetActivity.this));
        String str = GlideCacheUtil.getInstance().getCacheSize(MySetActivity.this);
        cleanUp.setText(str);
        Intent intent = getIntent();
        coachName = intent.getStringExtra("name");
        isLogin = intent.getBooleanExtra("islogin",false);
        coachPhone = intent.getStringExtra("phone");

        name.setText(coachName);
        if(coachPhone.equals("")||coachPhone==null){
            phone.setText("暂未填写");
        }else{
            phone.setText(coachPhone);
        }
        if (isLogin) {
            exit.setVisibility(View.VISIBLE);
            exit.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.exit:
                sp = getSharedPreferences("Coach",Activity.MODE_PRIVATE);
                editor = sp.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent();
                intent.setClass(MySetActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.cleanUp:
                createSureDialog();
                break;
            case R.id.changepwd:
                Intent intent1 = new Intent();
                intent1.setClass(MySetActivity.this,CallbackActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
    }
    //缓存清理dialog
    private void createSureDialog() {
        sureDialog = new Dialog(MySetActivity.this, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        sureView = LayoutInflater.from(MySetActivity.this).inflate(
                R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) sureView.findViewById(R.id.dialog_textView);
        dialog_textView.setText("确定清理缓存吗？");
        dialog_sure = (TextView) sureView.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) sureView.findViewById(R.id.dialog_cancel);
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlideCacheUtil.getInstance().clearImageAllCache(MySetActivity.this);
                String str = GlideCacheUtil.getInstance().getCacheSize(MySetActivity.this);
                cleanUp.setText(str);
                sureDialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sureDialog.dismiss();
            }
        });
        // 将布局设置给Dialog
        sureDialog.setContentView(sureView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = sureDialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        sureDialog.show();
    }
    /**
     * 检查更新
     */
    public  void checkSoftInfo(View view) {
        OkHttpUtils
                .get()
                .url(versionUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(MySetActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Version version = gson.fromJson(s, Version.class);
                        if (version.getCode() == 200) {
                            if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(MySetActivity.this)) {
                                UpdateManger updateManger = new UpdateManger(MySetActivity.this,
                                        version.getResult().get(0).getPath(),version.getResult().get(0).getVersionName());
                                updateManger.checkUpdateInfo();
                            } else {
                                Toast.makeText(MySetActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
