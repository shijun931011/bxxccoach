package com.jgkj.bxxccoach.tools;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.NoResultAction;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/14.
 * 确定排课结果dialog
 */

public class SetSubDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private String token;
    private String pid;
    private String url,day,time_slot;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private ProgressDialog proDialog;
    private Button btn;
    private TextView isApp;

    public SetSubDialog(Context context, String content, String token, String pid, String url,
                        String day, String time_slot, Button btn,TextView isApp){
        this.content = content;
        this.context = context;
        this.token = token;
        this.pid = pid;
        this.url = url;
        this.day = day;
        this.time_slot = time_slot;
        this.btn = btn;
        this.isApp = isApp;
    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(
                R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_sure.setOnClickListener(this);
        dialog_cancel.setOnClickListener(this);

        dialog_textView.setText("确定排课？");

        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();// 显示对话框
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_sure:
                proDialog = ProgressDialog.show(context, null, "提交中,请耐心等待...");
                saveTime();
                dialog.hide();
                break;
            case R.id.dialog_cancel:
                dialog.hide();
                break;
        }
    }
    /**
     * 确定排课
     *
     */
    private void saveTime() {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .addParams("day", day)
                .addParams("time_slot", time_slot)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (proDialog.isShowing()) {
                            proDialog.dismiss();
                        }
                        Toast.makeText(context, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        if (proDialog.isShowing()) {
                            proDialog.dismiss();
                        }
                        dialog_sure.setTag(s);
                        if(dialog_sure.getTag()!=null){
                            getRes();
                        }
                    }
                });
    }
    private void getRes(){
        String strTag = dialog_sure.getTag().toString();
        Gson gson = new Gson();
        NoResultAction action = gson.fromJson(strTag, NoResultAction.class);
        Toast.makeText(context, action.getReason(), Toast.LENGTH_SHORT).show();
        if(action.getCode()==200){
            if(time_slot.equals("")||time_slot==null){
                isApp.setText("未选课");
                isApp.setTextColor(context.getResources().getColor(R.color.right_bg));
            }else if(btn.getText().toString().equals("保存")){
                isApp.setText("已排课");
                isApp.setTextColor(context.getResources().getColor(R.color.green));
            }
        }
    }
}
