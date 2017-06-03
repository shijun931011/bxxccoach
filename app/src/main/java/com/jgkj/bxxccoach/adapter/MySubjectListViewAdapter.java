package com.jgkj.bxxccoach.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.MainActivity;
import com.jgkj.bxxccoach.MySubjectActivity;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.NoResultAction;
import com.jgkj.bxxccoach.bean.SubjectDetail;
import com.jgkj.bxxccoach.tools.CallDialog;
import com.jgkj.bxxccoach.tools.SureStudyDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/4.
 */

public class MySubjectListViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<SubjectDetail.Result.Res> list;
    private SubjectDetail.Result.Res res;
    private String daytime;
    private String time;
    private String pid;
    private ProgressDialog dialog;
    private String token;
    private String sureUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/applyStudyCar";


    public MySubjectListViewAdapter(Context context, String pid, List<SubjectDetail.Result.Res> list,
                                    String daytime, String time, String token) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.daytime = daytime;
        this.time = time;
        this.pid = pid;
        this.token = token;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.mysubject_listview_item,
                    viewGroup, false);
            viewHolder.connectStu = (Button) view.findViewById(R.id.connectStu);
            viewHolder.notCome = (Button) view.findViewById(R.id.notCome);
            viewHolder.sureCome = (Button) view.findViewById(R.id.sureCome);
            viewHolder.headImg = (ImageView) view.findViewById(R.id.headImg);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.state = (TextView) view.findViewById(R.id.state);
            viewHolder.applyTime = (TextView) view.findViewById(R.id.applyTime);
            viewHolder.phone = (TextView) view.findViewById(R.id.phone);
            viewHolder.connectStu.setTag(i);
            viewHolder.notCome.setTag(i);
            viewHolder.sureCome.setTag(i);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        res = list.get(i);
        viewHolder.name.setText(res.getName());
        viewHolder.state.setText(res.getState());
        viewHolder.applyTime.setText(res.getNotify_time().substring(0, 11));
        viewHolder.phone.setText(res.getPhone());
        String path = res.getFile();
        if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
            Glide.with(context).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(viewHolder.headImg);
        } else {
            Glide.with(context).load(path).placeholder(R.drawable.defaultimg).error(R.drawable.defaultimg).into(viewHolder.headImg);
        }
        if (res.getStatement().equals("1")) {
            viewHolder.notCome.setVisibility(View.INVISIBLE);
            viewHolder.sureCome.setEnabled(false);
            viewHolder.sureCome.setText("已同意");
            viewHolder.sureCome.setTextColor(context.getResources().getColor(R.color.gray));
        } else if (res.getStatement().equals("2")) {
            viewHolder.notCome.setVisibility(View.INVISIBLE);
            viewHolder.sureCome.setEnabled(false);
            viewHolder.sureCome.setTextColor(context.getResources().getColor(R.color.gray));
            viewHolder.sureCome.setText("已拒绝");
        }
        viewHolder.notCome.setTag(res.getUid());
        viewHolder.sureCome.setTag(res.getUid());
        viewHolder.connectStu.setTag(res.getPhone());
        viewHolder.connectStu.setOnClickListener(new MyClick(viewHolder.connectStu));
        viewHolder.notCome.setOnClickListener(new MyClick(viewHolder.sureCome));
        viewHolder.sureCome.setOnClickListener(new MyClick(viewHolder.notCome));

        return view;
    }

    //点击事件
    class MyClick implements View.OnClickListener{
        private Button btn1;
        public MyClick(Button btn1){
            this.btn1 = btn1;
        }
        @Override
        public void onClick(View view) {
            Button btn = (Button) view;
            SureStudyDialog sureStudyDialog = null;
            switch (view.getId()) {
                case R.id.notCome:
                    sureStudyDialog = new SureStudyDialog(context,btn.getTag().toString(),pid,
                            "2",daytime,time,token,btn,btn1);
                    sureStudyDialog.call();
                    break;
                case R.id.sureCome:
                    sureStudyDialog = new SureStudyDialog(context,btn.getTag().toString(),pid,
                            "1",daytime,time,token,btn,btn1);
                    sureStudyDialog.call();
                    break;
                case R.id.connectStu:
                    new CallDialog(context, btn.getTag().toString()).call();
                    break;
            }
        }
    }

    static class ViewHolder {
        public Button notCome, sureCome, connectStu;
        public ImageView headImg;
        public TextView name, state, applyTime, phone;
    }

    /**
     * 确定是否学车
     */
    private void getData(String uid, String statement) {
        OkHttpUtils
                .post()
                .url(sureUrl)
                .addParams("uid", uid)
                .addParams("pid", pid)
                .addParams("day", daytime)
                .addParams("time_slot", time)
                .addParams("token", token)
                .addParams("statement", statement)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(context, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Gson gson = new Gson();
                        NoResultAction action = gson.fromJson(s, NoResultAction.class);
                        Toast.makeText(context, action.getReason(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
