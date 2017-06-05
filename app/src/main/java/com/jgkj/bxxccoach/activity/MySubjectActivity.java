package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.MySubjectAdapter;
import com.jgkj.bxxccoach.bean.SubjectDetail;
import com.jgkj.bxxccoach.tools.CountListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/12.
 * 我的学员及确定时候来学车
 */

public class MySubjectActivity extends Activity implements View.OnClickListener{
    private ListView listView;
    private MySubjectAdapter adapter;
    private TextView title;
    private Button back;
    private Button subSet;
    private TextView up,next,day;
    private String pid,token;
    private String today;
    private ProgressDialog dialog ;
    private ScrollView scrollView;
    private TextView textView;
    private List<SubjectDetail.Result> list = new ArrayList<>();
    private View headView;
    private String getSubjectUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/showCourseStu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysubject);
        initView();
        dialog = ProgressDialog.show(MySubjectActivity.this,null,"加载中");
        getData(pid);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        token = intent.getStringExtra("token");

        up = (TextView) findViewById(R.id.up);
        next = (TextView)findViewById(R.id.next);
        up.setOnClickListener(this);
        next.setOnClickListener(this);
        day = (TextView) findViewById(R.id.day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        today = sdf.format(new Date());
        day.setText(today);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textView = (TextView) findViewById(R.id.textView);

        title = (TextView) findViewById(R.id.text_title);
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("我的课程");
        subSet = (Button) findViewById(R.id.button_forward);
        subSet.setVisibility(View.VISIBLE);
        subSet.setText("排课");
        subSet.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listView);
    }

    private void setListView(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        SubjectDetail sub = gson.fromJson(str,SubjectDetail.class);
        if(sub.getCode()==200){
            Toast.makeText(MySubjectActivity.this,sub.getReason(),Toast.LENGTH_SHORT).show();
            list.addAll(sub.getResult());
            adapter = new MySubjectAdapter(MySubjectActivity.this,list,today,pid,token);
            listView.setAdapter(adapter);
            new CountListView(MySubjectActivity.this).setListViewHeightBasedOnChildren(listView);
        }else{
            Toast.makeText(MySubjectActivity.this,sub.getReason(),Toast.LENGTH_SHORT).show();
            textView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;
            case R.id.up:
                textView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                list.clear();
                adapter = new MySubjectAdapter(MySubjectActivity.this,list,"",pid,token);
                listView.setAdapter(adapter);
                dialog = ProgressDialog.show(MySubjectActivity.this,null,"加载中");
                Date date = null;
                try {
                    date = (new SimpleDateFormat("yyyy年MM月dd日")).parse(today);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
                today = new SimpleDateFormat("yyyy年MM月dd日").format(cal.getTime());
                day.setText(today);
                getData(pid);
                break;
            case R.id.next:
                textView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                list.clear();
                adapter = new MySubjectAdapter(MySubjectActivity.this,list,"",pid,token);
                listView.setAdapter(adapter);
                dialog = ProgressDialog.show(MySubjectActivity.this,null,"加载中");
                Date date1 = null;
                try {
                    date1 = (new SimpleDateFormat("yyyy年MM月dd日")).parse(today);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                cal1.add(Calendar.DATE, 1);
                today = new SimpleDateFormat("yyyy年MM月dd日").format(cal1.getTime());
                day.setText(today);
                getData(pid);
                break;
            case R.id.button_forward:
                Intent intent = new Intent();
                intent.setClass(MySubjectActivity.this,SetSubjectActivity.class);
                intent.putExtra("pid",pid);
                intent.putExtra("token",token);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sp = getSharedPreferences("CoachSubjectSet",Activity.MODE_PRIVATE);
        boolean isfresh = sp.getBoolean("isChange",false);
        if(isfresh){
            Intent intent = new Intent();
            intent.setClass(MySubjectActivity.this,SetSubjectActivity.class);
            intent.putExtra("pid",pid);
            intent.putExtra("token",token);
            startActivity(intent);
        }
    }

    /**
     * 刷新
     */
    private void getData(String pid) {
        OkHttpUtils
                .post()
                .url(getSubjectUrl)
                .addParams("pid", pid)
                .addParams("day",today)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(MySubjectActivity.this, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            setListView();
                        }
                    }
                });
    }

}
