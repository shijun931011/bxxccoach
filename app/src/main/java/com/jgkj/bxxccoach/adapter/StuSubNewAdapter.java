package com.jgkj.bxxccoach.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.jgkj.bxxccoach.tools.SetSubDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/4.
 * 教练排课
 */

public class StuSubNewAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private LayoutInflater inflater;
    private CreateDay_Time createday;
    private List<CreateDay_Time> list;
    private String pid;
    private String token;
    private ProgressDialog dialog;
    private String cancelUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/coachRemoveCourse";
    private String paikeUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/coachApplyCourseAgain";
    public StuSubNewAdapter(Context context, List<CreateDay_Time> list, String pid, String token) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
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
            view = inflater.inflate(R.layout.stusubject_newlistview,
                    viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            viewHolder.imageView.setTag("down");
            viewHolder.btn1 = (Button) view.findViewById(R.id.radio_button_01);
            viewHolder.btn2 = (Button) view.findViewById(R.id.radio_button_02);
            viewHolder.btn3 = (Button) view.findViewById(R.id.radio_button_03);
            viewHolder.btn4 = (Button) view.findViewById(R.id.radio_button_04);
            viewHolder.btn5 = (Button) view.findViewById(R.id.radio_button_05);

            viewHolder.save = (Button) view.findViewById(R.id.save);
            viewHolder.day = (TextView) view.findViewById(R.id.day);
            viewHolder.isApp = (TextView) view.findViewById(R.id.isApp);
            viewHolder.linear = (LinearLayout) view.findViewById(R.id.linear);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        createday = list.get(i);
        if (createday.isApp1()) {
            viewHolder.btn1.setSelected(true);
        } else {
            viewHolder.btn1.setSelected(false);
        }
        if (createday.isApp2()) {
            viewHolder.btn2.setSelected(true);
        } else {
            viewHolder.btn2.setSelected(false);
        }
        if (createday.isApp3()) {
            viewHolder.btn3.setSelected(true);
        } else {
            viewHolder.btn3.setSelected(false);
        }
        if (createday.isApp4()) {
            viewHolder.btn4.setSelected(true);
        } else {
            viewHolder.btn4.setSelected(false);
        }
        if (createday.isApp5()) {
            viewHolder.btn5.setSelected(true);
        } else {
            viewHolder.btn5.setSelected(false);
        }
        if (viewHolder.btn1.isSelected() || viewHolder.btn4.isSelected() ||
                viewHolder.btn2.isSelected() || viewHolder.btn5.isSelected() ||
                viewHolder.btn3.isSelected()) {
            viewHolder.isApp.setText("已排课");
            viewHolder.isApp.setTextColor(context.getResources().getColor(R.color.green));
        }
        viewHolder.day.setText(createday.getDay());
        viewHolder.btn1.setOnClickListener(this);
        viewHolder.btn2.setOnClickListener(this);
        viewHolder.btn3.setOnClickListener(this);
        viewHolder.btn4.setOnClickListener(this);
        viewHolder.btn5.setOnClickListener(this);
        viewHolder.save.setTag(createday.getDay());

        viewHolder.save.setOnClickListener(new save(viewHolder.btn1, viewHolder.btn2,
                viewHolder.btn3, viewHolder.btn4, viewHolder.btn5, viewHolder.isApp));
        viewHolder.imageView.setOnClickListener(new OnClick(viewHolder.linear));


        return view;
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
        } else {
            btn.setSelected(true);
        }
    }

    /**
     * 保存提交
     */
    public class save implements View.OnClickListener {
        private Button btn1;
        private Button btn2;
        private Button btn3;
        private Button btn4;
        private Button btn5;
        private TextView isApp;

        public save(Button btn1, Button btn2, Button btn3, Button btn4, Button btn5, TextView isApp) {
            this.btn1 = btn1;
            this.btn2 = btn2;
            this.btn3 = btn3;
            this.btn4 = btn4;
            this.btn5 = btn5;
            this.isApp = isApp;
        }
        @Override
        public void onClick(View view) {
            String time = "";
            Button btn = (Button) view;
            if (btn1.isSelected()) {
                time = time + btn1.getText().toString() + ",";
            }
            if (btn2.isSelected()) {
                time = time + btn2.getText().toString() + ",";
            }
            if (btn3.isSelected()) {
                time = time + btn3.getText().toString() + ",";
            }
            if (btn4.isSelected()) {
                time = time + btn4.getText().toString() + ",";
            }
            if (btn5.isSelected()) {
                time = time + btn5.getText().toString() + ",";
            }
                if (!time.equals("")) {
                    time = time.substring(0, time.length() - 1);
                    SetSubDialog setSubDialog = new SetSubDialog(context,"确定此排课时间段吗？",token,
                            pid,paikeUrl,btn.getTag().toString(),time,btn,isApp);
                    setSubDialog.call();
                } else {
                    SetSubDialog setSubDialog = new SetSubDialog(context,"确定取消排课吗？",token,
                            pid,cancelUrl,btn.getTag().toString(),"",btn,isApp);
                    setSubDialog.call();
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
        private Button btn1, btn2, btn3, btn4, btn5, save;
        private TextView day, isApp;
    }

    /**
     * 确定排课
     *
     * @param day  天数
     * @param time 时间
     * @param url  请求地址
     */
    private void saveTime(String day, String time, String url) {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .addParams("day", day)
                .addParams("time_slot", time)
                .addParams("token", token)
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
