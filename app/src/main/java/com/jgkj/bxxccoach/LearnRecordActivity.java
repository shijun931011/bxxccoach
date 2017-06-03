package com.jgkj.bxxccoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.adapter.LearnRecordAdapter;
import com.jgkj.bxxccoach.bean.StuRecord;
import com.jgkj.bxxccoach.tools.CallDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/12.
 * 某个学员的学车记录
 */

public class LearnRecordActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private Button back;
    private TextView title;
    private LearnRecordAdapter adapter;
    private String name,uid,file,state,phone,time,count,pid;
    private ImageView head;
    private TextView stuName,stuTime,stuPhone,stuCount,stuState;
    private String getStuRecordUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/applyComment";
    private StuRecord stuRecord;
    private List<StuRecord.Result> list;
    private Button connectStu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learnrecord);
        initView();
        getData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("学车记录");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        head = (ImageView) findViewById(R.id.head);
        stuName = (TextView) findViewById(R.id.stuName);
        stuTime = (TextView) findViewById(R.id.stuTime);
        stuPhone = (TextView) findViewById(R.id.stuPhone);
        stuCount = (TextView) findViewById(R.id.stuCount);
        stuState = (TextView) findViewById(R.id.stuState);
        connectStu = (Button) findViewById(R.id.connectStu);
        connectStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CallDialog(LearnRecordActivity.this,phone).call();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
    }

    /**
     * 接受上个页面的传值
     */
    private void getData(){
        Intent intent = getIntent();
        String str = intent.getStringExtra("name");
        name = str.split(",")[2];
        file = str.split(",")[1];
        uid = str.split(",")[0];
        state = intent.getStringExtra("state");
        phone = intent.getStringExtra("phone");
        time = intent.getStringExtra("time");
        count = intent.getStringExtra("countTime");
        pid = intent.getStringExtra("pid");
        String path = file;
        if(!path.endsWith(".jpg")&&!path.endsWith(".jpeg")&&!path.endsWith(".png")&&
                !path.endsWith(".GIF")&&!path.endsWith(".PNG") &&!path.endsWith(".JPG")&&!path.endsWith(".gif")){
            Glide.with(this).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(head);
            head.setTag("http://www.baixinxueche.com/Public/Home/img/default.png");
        }else{
            Glide.with(this).load(path).into(head);
        }
        stuName.setText(name);
        stuState.setText(state);
        stuTime.setText(time);
        stuPhone.setText(phone);
        stuCount.setText(count+"次");
        getStuRecord("1");
    }

    /**
     * gson解析返回数据，并配置adapter
     */
    private void getList(){
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        stuRecord = gson.fromJson(str,StuRecord.class);
        if(stuRecord.getCode()==200){
            list = stuRecord.getResult();
            adapter = new LearnRecordAdapter(LearnRecordActivity.this,list);
            listView.setAdapter(adapter);
        }else{
            Toast.makeText(LearnRecordActivity.this,stuRecord.getReason(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
        }
    }
    /**
     * 获取学员学车记录
     */
    private void getStuRecord(String page) {
        OkHttpUtils
                .post()
                .url(getStuRecordUrl)
                .addParams("pid", pid)
                .addParams("page",page)
                .addParams("uid",uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
//                        dialog.dismiss();
                        Toast.makeText(LearnRecordActivity.this, "网络状态不佳，请稍后再试！", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            getList();
                        }
                    }
                });
    }

}
