package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.StuSubNewAdapter;
import com.jgkj.bxxccoach.bean.CreateDay_Time;
import com.jgkj.bxxccoach.bean.GetSetedTime;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/12.
 * 教练排课
 */

public class SetSubjectActivity extends Activity implements View.OnClickListener{
    private ListView listView;
    private Button back;
    private TextView title;
    private StuSubNewAdapter adapter;
    private CreateDay_Time createday;
    private List<CreateDay_Time> list;
    private Date date;
    private String token;
    private String pid;
    private String getAppTimeUrl = "http://www.baixinxueche.com/index.php/Home/Apicoach/makeCourse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setsubject);
        initView();
        for (int i = 0; i < 7; i++) {
            createTime(date);
        }
        getData();
    }

    /**
     * 创建天数和时间
     *
     * @param date1 当前时间
     */
    private void createTime(Date date1) {
        String day = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        createday = new CreateDay_Time(day, "08:00-10:00", "10:00-12:00", "13:30-15:30", "15:30-17:30",
                "19:00-21:00", false, false, false, false, false);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.add(Calendar.DATE, 1);
        date = cal.getTime();
        list.add(createday);
    }


    /**
     * 从服务器获取时间，并与本地进行比对，显示排课或者未排课
     */
    private void getMyTime() {
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        GetSetedTime getSetedTime = gson.fromJson(str, GetSetedTime.class);
        if (getSetedTime.getCode() == 200) {
            for (int k = 0; k < list.size(); k++) {
                for (int i = 0; i < getSetedTime.getResult().size(); i++) {
                    if (list.get(k).getDay().equals(getSetedTime.getResult().get(i).getDay())) {
                        for (int j = 0; j < getSetedTime.getResult().get(i).getTime_slot().size(); j++) {
                            if (getSetedTime.getResult().get(i).getTime_slot().get(j).equals("08:00-10:00")) {
                                list.get(k).setApp1(true);
                            }
                            if (getSetedTime.getResult().get(i).getTime_slot().get(j).equals("10:00-12:00")) {
                                list.get(k).setApp2(true);
                            }
                            if (getSetedTime.getResult().get(i).getTime_slot().get(j).equals("13:30-15:30")) {
                                list.get(k).setApp3(true);
                            }
                            if (getSetedTime.getResult().get(i).getTime_slot().get(j).equals("15:30-17:30")) {
                                list.get(k).setApp4(true);
                            }
                            if (getSetedTime.getResult().get(i).getTime_slot().get(j).equals("19:00-21:00")) {
                                list.get(k).setApp5(true);
                            }
                        }
                    }
                }
            }
        }
        adapter = new StuSubNewAdapter(SetSubjectActivity.this, list, pid, token);
        listView.setAdapter(adapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);
        date = cal.getTime();

        pid = getIntent().getStringExtra("pid");

        title = (TextView) findViewById(R.id.text_title);
        title.setText("安排课程");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("CoachSubjectSet",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isChange",false);
        editor.commit();
    }

    /**
     * 刷新
     */
    private void getData() {
        OkHttpUtils
                .post()
                .url(getAppTimeUrl)
                .addParams("pid", pid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(SetSubjectActivity.this, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            getMyTime();
                        }
                    }
                });
    }
    public void refreshData(){
        list.clear();
        for (int i = 0; i < 7; i++) {
            createTime(date);
        }
        getData();
    }

}
