package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.StuAppraiseAdapter;
import com.jgkj.bxxccoach.bean.StuAppraise;
import com.jgkj.bxxccoach.tools.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/12.
 * 学员评价
 */

public class StuAppraiseActivity extends Activity implements View.OnClickListener,
        RefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private Button back;
    private TextView title;
    private StuAppraiseAdapter adapter;
    private String pid;
    private RefreshLayout refresh;
    private ProgressDialog dialog;
    private int currentPage = 1;
    private List<StuAppraise.Result> list = new ArrayList<>();
    private String appraiseClass = "3";
    private TextView medium, good, bad;

    private TextView textView;
    private String appraiseUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/seeComment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stuappraise);
        try{
            initView();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(StuAppraiseActivity.this,"数据加载异常，请稍后再试！",Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        dialog = ProgressDialog.show(StuAppraiseActivity.this, null, "加载中...");
        title = (TextView) findViewById(R.id.text_title);
        title.setText("学员评价");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.appraiseListView);
        medium = (TextView) findViewById(R.id.medium);
        good = (TextView) findViewById(R.id.good);
        bad = (TextView) findViewById(R.id.bad);
        medium.setOnClickListener(this);
        good.setOnClickListener(this);
        bad.setOnClickListener(this);

        refresh = (RefreshLayout) findViewById(R.id.appraiseRefresh);
        refresh.setTag("isFirst");
        refresh.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        refresh.setOnRefreshListener(this);
        refresh.setOnLoadListener(this);

        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        getAppraise(pid, currentPage + "", appraiseClass);
    }

    //给listView添加数据设置适配器
    private void getList() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        StuAppraise stu = gson.fromJson(str, StuAppraise.class);
        if (stu.getCode() == 200) {
            Toast.makeText(StuAppraiseActivity.this, stu.getReason(), Toast.LENGTH_SHORT).show();
            list.addAll(stu.getResult());
            if (refresh.getTag().toString().equals("isFirst")) {
                adapter = new StuAppraiseAdapter(StuAppraiseActivity.this, list);
                listView.setAdapter(adapter);
            } else if (refresh.getTag().toString().equals("onload")) {
                adapter = new StuAppraiseAdapter(StuAppraiseActivity.this, list);
                listView.setAdapter(adapter);
            } else if (refresh.getTag().toString().equals("refresh")) {
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(StuAppraiseActivity.this, stu.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.good:
                refresh.setTag("isFirst");
                refresh.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                list.clear();
                adapter.notifyDataSetChanged();
                good.setTextColor(getResources().getColor(R.color.colorred));
                bad.setTextColor(getResources().getColor(R.color.lightblack));
                medium.setTextColor(getResources().getColor(R.color.lightblack));
                dialog = ProgressDialog.show(StuAppraiseActivity.this, null, "加载中...");
                appraiseClass = "3";
                currentPage = 1;
                getAppraise(pid, currentPage + "", appraiseClass);
                break;
            case R.id.bad:
                refresh.setTag("isFirst");
                refresh.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                currentPage = 1;
                list.clear();
                adapter.notifyDataSetChanged();
                bad.setTextColor(getResources().getColor(R.color.colorred));
                good.setTextColor(getResources().getColor(R.color.lightblack));
                medium.setTextColor(getResources().getColor(R.color.lightblack));
                dialog = ProgressDialog.show(StuAppraiseActivity.this, null, "加载中...");
                appraiseClass = "1";
                getAppraise(pid, currentPage + "", appraiseClass);
                break;
            case R.id.medium:
                refresh.setTag("isFirst");
                refresh.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                currentPage = 1;
                list.clear();
                adapter.notifyDataSetChanged();
                medium.setTextColor(getResources().getColor(R.color.colorred));
                bad.setTextColor(getResources().getColor(R.color.lightblack));
                good.setTextColor(getResources().getColor(R.color.lightblack));
                dialog = ProgressDialog.show(StuAppraiseActivity.this, null, "加载中...");
                appraiseClass = "2";
                getAppraise(pid, currentPage + "", appraiseClass);
                break;
        }
    }

    /**
     * 查看评论
     */
    private void getAppraise(String pid, String page, String haoping) {
        OkHttpUtils
                .post()
                .url(appraiseUrl)
                .addParams("pid", pid)
                .addParams("page", page)
                .addParams("haoping", haoping)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(StuAppraiseActivity.this, "网络状态不佳，请稍后再试！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            getList();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage = 1;
                list.clear();
                refresh.setTag("refresh");
                adapter.notifyDataSetChanged();
                getAppraise(pid, currentPage + "", appraiseClass);
                refresh.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onLoad() {
        refresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage++;
                refresh.setTag("onload");
                getAppraise(pid, currentPage + "", appraiseClass);
                refresh.setLoading(false);
            }
        }, 2000);
    }
}
