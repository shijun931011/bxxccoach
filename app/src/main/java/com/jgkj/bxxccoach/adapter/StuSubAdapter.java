package com.jgkj.bxxccoach.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.CreateDay_Time;
import com.jgkj.bxxccoach.bean.NoResultAction;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/4.
 * 教练排课
 */

public class StuSubAdapter extends BaseAdapter implements View.OnClickListener{
    private Context context;
    private LayoutInflater inflater;
    private CreateDay_Time createday;
    private List<CreateDay_Time> list;
    private String pid;
    private ProgressDialog dialog;
    private  Drawable draw1,draw2;
    private String cancelUrl = "http://www.baixinxueche.com/index.php/Home/Apicoach/coachRemoveCourse";
    private String paikeUrl = "http://www.baixinxueche.com/index.php/Home/Apicoach/coachApplyCourse";

    public StuSubAdapter(Context context, List<CreateDay_Time> list,String pid) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.pid = pid;
        draw1 = context.getResources().getDrawable(R.drawable.checkboximg);
        draw2 = context.getResources().getDrawable(R.drawable.checkboximgempty);
        draw1.setBounds(0, 0, 30, 30);
        draw2.setBounds(0, 0, 30, 30);
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
            view = inflater.inflate(R.layout.stusubject_listview,
                    viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            viewHolder.imageView.setTag("down");
            viewHolder.btn1 = (Button) view.findViewById(R.id.btn1);
            viewHolder.btn2 = (Button) view.findViewById(R.id.btn2);
            viewHolder.btn3 = (Button) view.findViewById(R.id.btn3);
            viewHolder.btn1.setTag("uncheck");
            viewHolder.btn2.setTag("uncheck");
            viewHolder.btn3.setTag("uncheck");
            viewHolder.save = (Button) view.findViewById(R.id.save);
            viewHolder.day = (TextView) view.findViewById(R.id.day);
            viewHolder.isApp = (TextView) view.findViewById(R.id.isApp);

            viewHolder.linear = (LinearLayout) view.findViewById(R.id.linear);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        createday = list.get(i);
        if (createday.isApp1()) {
            viewHolder.btn1.setTag("checked");
            viewHolder.btn1.setCompoundDrawables(draw1, null, null, null);
        }else{
            viewHolder.btn1.setCompoundDrawables(draw2, null, null, null);
        }
        if (createday.isApp3()) {
            viewHolder.btn3.setTag("checked");
            viewHolder.btn3.setCompoundDrawables(draw1, null, null, null);
        }else{
            viewHolder.btn3.setCompoundDrawables(draw2, null, null, null);
        }
        if (createday.isApp2()) {
            viewHolder.btn2.setTag("checked");
            viewHolder.btn2.setCompoundDrawables(draw1, null, null, null);

        }else{
            viewHolder.btn2.setCompoundDrawables(draw2, null, null, null);
        }
        if(viewHolder.btn1.getTag().toString().equals("checked")||
                viewHolder.btn2.getTag().toString().equals("checked")||
                viewHolder.btn3.getTag().toString().equals("checked")){
            viewHolder.btn1.setEnabled(false);
            viewHolder.btn2.setEnabled(false);
            viewHolder.btn3.setEnabled(false);
            viewHolder.save.setText("取消排课");
            viewHolder.isApp.setText("已排课");
            viewHolder.isApp.setTextColor(context.getResources().getColor(R.color.green));
        }

        viewHolder.day.setText(createday.getDay());
        viewHolder.btn1.setOnClickListener(this);
        viewHolder.btn2.setOnClickListener(this);
        viewHolder.btn3.setOnClickListener(this);
        viewHolder.save.setTag(createday.getDay());

        viewHolder.save.setOnClickListener(new save(viewHolder.btn1,viewHolder.btn2,
                viewHolder.btn3,viewHolder.isApp));
        viewHolder.imageView.setOnClickListener(new OnClick(viewHolder.linear));
        return view;
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        Drawable draw1 = context.getResources().getDrawable(R.drawable.checkboximg);
        Drawable draw2 = context.getResources().getDrawable(R.drawable.checkboximgempty);
        draw1.setBounds(0, 0, 30, 30);
        draw2.setBounds(0, 0, 30, 30);
        if (btn.getTag().equals("uncheck")) {
            btn.setTag("checked");
            btn.setCompoundDrawables(draw1, null, null, null);
        } else if (btn.getTag().equals("checked")) {
            btn.setTag("uncheck");
            btn.setCompoundDrawables(draw2, null, null, null);
        }
    }

    /**
     * 保存提交
     */
    public class save implements View.OnClickListener{
        private Button btn1;
        private Button btn2;
        private Button btn3;
        private TextView isApp;

        public save(Button btn1,Button btn2,Button btn3,TextView isApp){
            this.btn1 = btn1;
            this.btn2 = btn2;
            this.btn3 = btn3;
            this.isApp = isApp;
        }
        @Override
        public void onClick(View view) {
            dialog = ProgressDialog.show(context,null,"提交中,请耐心等待...");
            String time  ="";
            Button btn = (Button) view;
            if(btn.getText().toString().equals("取消排课")){
                saveTime(btn.getTag().toString(),"",cancelUrl);
                btn1.setEnabled(true);
                btn2.setEnabled(true);
                btn3.setEnabled(true);
                btn1.setTag("uncheck");
                btn2.setTag("uncheck");
                btn3.setTag("uncheck");
                btn.setText("保存");
                isApp.setText("未排课");
                isApp.setTextColor(context.getResources().getColor(R.color.gray));
            }else{
                if(btn1.getTag().toString().equals("checked")){
                    time = time + btn1.getText().toString()+",";
                }
                if(btn2.getTag().toString().equals("checked")){
                    time = time + btn2.getText().toString()+",";
                }
                if(btn3.getTag().toString().equals("checked")){
                    time = time + btn3.getText().toString()+",";
                }
                if(!time.equals("")){
                    time = time.substring(0,time.length()-1);
                    saveTime(btn.getTag().toString(),time,paikeUrl);
                    btn1.setEnabled(false);
                    btn2.setEnabled(false);
                    btn3.setEnabled(false);
                    isApp.setText("已排课");
                    btn.setText("取消排课");
                    isApp.setTextColor(context.getResources().getColor(R.color.green));
                }else{
                    Toast.makeText(context,"请至少选择一个时间段在保存",Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    private class OnClick implements View.OnClickListener {
        private LinearLayout linear;

        public OnClick(LinearLayout linear) {
            this.linear = linear;
        }

        @Override
        public void onClick(View view) {
            ImageView img = (ImageView) view;
            switch (img.getTag().toString()) {
                case "down":
                    img.setImageResource(R.drawable.up);
                    img.setTag("up");
                    linear.setVisibility(View.VISIBLE);
                    break;
                case "up":
                    img.setImageResource(R.drawable.down);
                    img.setTag("down");
                    linear.setVisibility(View.GONE);
                    break;
            }
        }
    }


    static class ViewHolder {
        public ImageView imageView;
        private LinearLayout linear;
        private Button btn1, btn2, btn3, save;
        private TextView day, isApp;
    }
    private void saveTime(String day,String time,String url){
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .addParams("day", day)
                .addParams("time_slot", time)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(context, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Gson gson = new Gson();
                        NoResultAction action = gson.fromJson(s,NoResultAction.class);
                        Toast.makeText(context,action.getReason(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
