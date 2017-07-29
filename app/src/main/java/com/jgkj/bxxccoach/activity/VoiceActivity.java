package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jgkj.bxxccoach.R;

public class VoiceActivity extends Activity implements View.OnClickListener {

    private Button back;
    private TextView title;

    private ImageView im_moni;
    private ImageView im_bobao;

    //dialog
    private Dialog voiceDialog;
    private View dialogView;
    private TextView tv_create;
    private TextView tv_exist;
    private TextView tv_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        title = (TextView) findViewById(R.id.text_title);
        title.setText("科目三");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        im_moni = (ImageView)findViewById(R.id.im_moni);
        im_bobao = (ImageView)findViewById(R.id.im_bobao);
        im_moni.setOnClickListener(this);
        im_bobao.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.im_moni:
                showDialog();
                break;
            case R.id.im_bobao:
                Intent intentss = new Intent();
                intentss.setClass(VoiceActivity.this,BroadcastActivity.class);
                startActivity(intentss);
                break;
            case R.id.tv_create://创建路线
                Intent intent = new Intent();
                intent.setClass(VoiceActivity.this,CreateActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_exist:
                Intent intents = new Intent();
                intents.setClass(VoiceActivity.this,ExistActivity.class);
                startActivity(intents);
                break;
            case R.id.tv_cancel:
                voiceDialog.dismiss();
                break;
        }
    }

    //dialog
    public void showDialog() {
        voiceDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        dialogView = LayoutInflater.from(this).inflate(R.layout.voice_dialog, null);
        // 初始化控件
        tv_create = (TextView) dialogView.findViewById(R.id.tv_create);
        tv_exist = (TextView) dialogView.findViewById(R.id.tv_exist);
        tv_cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        tv_create.setOnClickListener(this);
        tv_exist.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        // 将布局设置给Dialog
        voiceDialog.setContentView(dialogView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = voiceDialog.getWindow();
        // 设置dialog横向充满
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.y = 20;// 设置Dialog距离底部的距离
//        dialogWindow.setAttributes(lp);
        voiceDialog.show();// 显示对话框
    }
}
