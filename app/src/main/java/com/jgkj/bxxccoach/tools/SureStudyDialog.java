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
 * 确定是否来学车dialog
 */

public class SureStudyDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private String token;
    private String pid;
    private String uid,day,time_slot,statement;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    private ProgressDialog proDialog;
    private Button button,button1;
    private String url = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/applyStudyCar";

    public SureStudyDialog(Context context, String uid, String pid,String statement,String day,
                           String time_slot, String token,Button button,Button button1){
        this.context = context;
        this.token = token;
        this.day = day;
        this.pid = pid;
        this.time_slot = time_slot;
        this.uid = uid;
        this.statement = statement;
        this.button = button;
        this.button1 = button1;

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

        if(statement.equals("1")){
            dialog_textView.setText("确定学员到场学车？");
        }else if(statement.equals("2")){
            dialog_textView.setText("确定学员未到场学车？");
        }
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
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
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
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
                .addParams("uid", uid)
                .addParams("pid", pid)
                .addParams("day", day)
                .addParams("time_slot", time_slot)
                .addParams("token", token)
                .addParams("statement", statement)
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
            if(button.getText().toString().equals("没有学车")){
                button.setEnabled(false);
                button.setText("已拒绝");
                button1.setVisibility(View.INVISIBLE);
                button.setTextColor(context.getResources().getColor(R.color.gray));
            }else if(button.getText().toString().equals("确定学车")){
                button.setEnabled(false);
                button.setText("已同意");
                button1.setVisibility(View.INVISIBLE);
                button.setTextColor(context.getResources().getColor(R.color.gray));
            }else if(button.getText().toString().equals("已同意")||button.getText().toString().equals("已拒绝")){
                Toast.makeText(context,"已选择，请勿再次选择",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
