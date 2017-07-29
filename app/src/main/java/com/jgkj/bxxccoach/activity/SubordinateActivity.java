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
import com.jgkj.bxxccoach.bean.StuMsg;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.tools.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 下属学员
 */
public class SubordinateActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener,
        RefreshLayout.OnLoadListener,SwipeRefreshLayout.OnRefreshListener{
    private Button back;
    private Button button_forward;
    private TextView title;
    private TextView textView;
    private RefreshLayout swipeLayout;
    private ListView listView;
    private CoachStuAdapter adapter;
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private String pid;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserInfo user;
    private ProgressDialog dialog;
    private StuMsg stuMsg;
    private List<StuMsg.Result> StuMsglist = new ArrayList<>();
    private int currentPage = 1;

    private String MyTraineeUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/myStuPeople";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainee);
        initView();
        getStu(MyTraineeUrl, currentPage+"");
    }
    private void initView(){
        title = (TextView) findViewById(R.id.text_title);
        title.setText("下属学员");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        button_forward = (Button)findViewById(R.id.button_forward);
        button_forward.setOnClickListener(this);

        //下拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.refresh);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        swipeLayout.setTag("UNENABLE");

        listView = (ListView) findViewById(R.id.coach_Stu_ListView);
        textView = (TextView) findViewById(R.id.textView);
        listView.setOnItemClickListener(this);

        pid = getIntent().getStringExtra("pid");
    }

    /**
     * 学员获取
     */
    private void getStu(String Url,String page) {
        Log.d("bxxc","获取页数"+page);
        OkHttpUtils
                .post()
                .url(Url)
                .addParams("pid", pid)
                .addParams("page",page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(SubordinateActivity.this, "网络状态不佳，请稍后再试！", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("bxxc","获取学员"+s);
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            getList();
                        }
                    }
                });
    }

    //读取数据并且解析处理
    private void  getList(){
        String swiTag="";
        String tag = "";
        try {
            swiTag = swipeLayout.getTag().toString();
            tag = listView.getTag().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        swipeLayout.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        stuMsg = gson.fromJson(tag,StuMsg.class);
        switch (swiTag) {
            case "UNENABLE":
                StuMsglist.clear();
                setMyAdapter();
                break;
            case "REFRESH":
                StuMsglist.clear();
                setMyAdapter();
                break;
            case "ONLOAD":
                if (stuMsg.getCode() == 200) {
                    setMyAdapter();
                } else {
                    Toast.makeText(SubordinateActivity.this, stuMsg.getReason(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setMyAdapter() {
        String tag = listView.getTag().toString();
        Gson gson = new Gson();
        stuMsg = gson.fromJson(tag,StuMsg.class);
        if (stuMsg.getCode() == 200) {
            StuMsglist.addAll(stuMsg.getResult());
            adapter = new CoachStuAdapter(this, StuMsglist);
            listView.setAdapter(adapter);
        } else {
            swipeLayout.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Toast.makeText(this, stuMsg.getReason(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView state = (TextView) view.findViewById(R.id.state);
            TextView countTime = (TextView) view.findViewById(R.id.countTime);
            TextView phone = (TextView) view.findViewById(R.id.phone);
            TextView time = (TextView) view.findViewById(R.id.time);
            Intent intent = new Intent();
            intent.setClass(this, LearnRecordActivity.class);
            intent.putExtra("name",name.getTag().toString());
            intent.putExtra("state",state.getTag().toString());
            intent.putExtra("countTime",countTime.getTag().toString());
            intent.putExtra("phone",phone.getTag().toString());
            intent.putExtra("time",time.getTag().toString());
            intent.putExtra("pid",pid);
            startActivity(intent);
    }

    @Override
    public void onLoad() {
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage++;
                getStu(MyTraineeUrl, currentPage+"");
                swipeLayout.setTag("ONLOAD");
                swipeLayout.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        try{
            swipeLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentPage = 1;
                    swipeLayout.setTag("REFRESH");
                    getStu(MyTraineeUrl, currentPage+"");
                    swipeLayout.setRefreshing(false);
                }
            }, 2000);
        }catch (Exception e){
            Log.i("TAG","数据错误！");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.button_backward:
                finish();
                break;
        }
    }
}
