package com.jgkj.bxxccoach.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.jgkj.bxxccoach.bean.entity.TiXianEntity.WithDrawalResult;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/8.
 */

public class WithDrawalDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private String token;
    private String pid;
    private String totalMoney;
    private String toAccount;
    private TextView dialog_card_imfo;

    private TextView dialog_textView, dialog_sure, dialog_cancel;

    /**
     * 教练余额提现
     *
     * @param savedInstanceState
     * pid, token
     */
    private String tixianUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/balanceTixian";

    public WithDrawalDialog(Context context,String content, String pid, String token, String totalMoney, String toAccount){
        this.content = content;
        this.context = context;
        this.token = token;
        this.pid = pid;
        this.totalMoney = totalMoney;
        this.toAccount = toAccount;
    }
    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(R.layout.sure_idcard_dialog, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_card_imfo = (TextView) inflate.findViewById(R.id.dialog_card_imfo);
        dialog_sure.setOnClickListener(this);
        dialog_cancel.setOnClickListener(this);
        dialog_card_imfo.setText("是否将账户余额：" + totalMoney+"元，提现至账号：" + toAccount +"中？");
        dialog_textView.setText(content);
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();// 显示对话框
    }


    private void getWithDrawal(String pid, String token){
        OkHttpUtils
                .post()
                .url(tixianUrl)
                .addParams("pid",pid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(context, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("BXXC", "提现结果"+s);
                        Gson gson = new Gson();
                        WithDrawalResult withDrawalResult  = gson.fromJson(s, WithDrawalResult.class);
                        if (withDrawalResult.getCode() == 200){
                            Toast.makeText(context, withDrawalResult.getReason(), Toast.LENGTH_SHORT).show();
                            updata();
                        }else{
                            Toast.makeText(context,  withDrawalResult.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_sure:
                getWithDrawal(pid,token);
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;

        }
    }


    //广播更新数据
    public void updata() {
        Intent intent = new Intent();
        intent.setAction("updataTianApp");
        context.sendBroadcast(intent);
    }



}
