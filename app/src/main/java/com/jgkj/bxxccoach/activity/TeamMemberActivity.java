package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.CoachStuAdapter;
import com.jgkj.bxxccoach.adapter.GroupMemberAdapter;
import com.jgkj.bxxccoach.bean.StuMsg;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.GroupMemberEntity.GroupMemberResult;
import com.jgkj.bxxccoach.tools.RefreshLayout;
import com.jgkj.bxxccoach.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 团队成员
 */
public class TeamMemberActivity extends Activity implements View.OnClickListener{
    private Button back;
    private TextView title;
    private ListView lv;
    private TextView tv_empter;
    private ProgressDialog dialogs;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserInfo user;
    private Gson gson;
    private GroupMemberAdapter adapter;
    private GroupMemberResult groupMemberResult;

    private String falg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogs = ProgressDialog.show(TeamMemberActivity.this, null, "请求中...");
        setContentView(R.layout.activity_team_member);
        initView();
    }

    private void initView(){
        title = (TextView) findViewById(R.id.text_title);
        title.setText("团队成员");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        lv = (ListView)findViewById(R.id.lv);
        tv_empter = (TextView)findViewById(R.id.tv_empter);

        //接受标识，判断是否从HomeActivity跳转过来
        falg = getIntent().getStringExtra("flag");

        //获取center_id
        sp = getApplication().getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        editor = sp.edit();
        gson = new Gson();
        if(sp.getString("CoachInfo",null)!=null){
            user = gson.fromJson(sp.getString("CoachInfo",null),UserInfo.class);
            //请求接口
            saveRoute(user.getResult().getTid());
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if(falg == null){
                    intent.setClass(TeamMemberActivity.this,SubordinateActivity.class);
                    intent.putExtra("pid",groupMemberResult.getResult().get(position).getPid());
                }else{
                    intent.setClass(TeamMemberActivity.this,ScheduleActivity.class);
                    intent.putExtra("pid",groupMemberResult.getResult().get(position).getPid());
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.button_backward:
                finish();
                break;
        }
    }

    /**
     * 团队教练列表
     */
    private void saveRoute(String center_id) {
        Log.i("百信学车","教练列表" + "tid" + center_id);
        OkHttpUtils
                .post()
                .url(Urls.centerList)
                .addParams("tid", center_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialogs.dismiss();
                        Toast.makeText(TeamMemberActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialogs.dismiss();
                        Log.i("百信学车","教练列表结果" + s);

                        groupMemberResult = gson.fromJson(s,GroupMemberResult.class);
                        if(groupMemberResult.getCode() == 200){
                            if(groupMemberResult.getResult() != null){
                                adapter = new GroupMemberAdapter(TeamMemberActivity.this,groupMemberResult.getResult());
                                lv.setAdapter(adapter);
                            }else{
                                lv.setEmptyView(tv_empter);
                            }
                        }else{
                            Toast.makeText(TeamMemberActivity.this, groupMemberResult.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
