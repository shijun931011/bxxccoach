package com.jgkj.bxxccoach.tools;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.activity.CreateActivity;
import com.jgkj.bxxccoach.bean.entity.UserRouteEntity.UserRouteEntity;
import com.jgkj.bxxccoach.bean.entity.UserRouteEntity.UserRouteResult;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by tongshoujun on 2017/1/14.
 * 拨号dialog
 */

public class ConfirmRouteDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private ProgressDialog dialogs;
    private String pid;
    private String id;
    private UserRouteResult userRouteResult;
    public static List<UserRouteEntity> userRouteList = null;

    public ConfirmRouteDialog(Context context, String content,String pid,String id){
        this.content = content;
        this.context = context;
        this.pid = pid;
        this.id = id;
    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_cancel_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_sure.setOnClickListener(this);
        dialog_cancel.setOnClickListener(this);
        dialog_textView.setText(content);

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
                dialogs = ProgressDialog.show(context, null, "请求中...");
                userRoute(pid,id);
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;

        }
    }

    /**
     * 使用路线
     */
    private void userRoute(String pid,String id) {
        Log.i("百信学车","使用路线参数" + " pid=" + pid + " id=" + id);
        OkHttpUtils
                .post()
                .url(Urls.sendRoute)
                .addParams("pid", pid)
                .addParams("id", id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialogs.dismiss();
                        Toast.makeText(context, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialogs.dismiss();
                        Log.i("百信学车","使用路线结果" + s);
                        Gson gson = new Gson();
                        userRouteResult = gson.fromJson(s,UserRouteResult.class);
                        if(userRouteResult.getCode() == 200){
                            userRouteList = userRouteResult.getResult();
                        }else{
                            Toast.makeText(context, userRouteResult.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
