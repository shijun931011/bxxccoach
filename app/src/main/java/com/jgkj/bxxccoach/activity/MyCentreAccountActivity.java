package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.GroupMemberAdapter;
import com.jgkj.bxxccoach.adapter.IncomeRecordAdapter;
import com.jgkj.bxxccoach.adapter.WithDrawRecordAdapter;
import com.jgkj.bxxccoach.bean.IncomeRecord;
import com.jgkj.bxxccoach.bean.MyAccountMoney;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.WithDrawRecord;
import com.jgkj.bxxccoach.bean.entity.GroupMemberEntity.GroupMemberResult;
import com.jgkj.bxxccoach.tools.AppUtility;
import com.jgkj.bxxccoach.tools.ConfirmRouteDialog;
import com.jgkj.bxxccoach.tools.RefreshLayout;
import com.jgkj.bxxccoach.tools.RemainBaseDialog;
import com.jgkj.bxxccoach.tools.Urls;
import com.jgkj.bxxccoach.tools.WithDrawalDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * Created by tongshoujun on 2017/7/28.
 * 教练端中心账户页面
 */

public class MyCentreAccountActivity extends Activity implements View.OnClickListener{
    private ListView listView;
    private RefreshLayout refresh;
    private Button back;
    private TextView title,tv_empter;
    private TextView tv_account_money;
    private TextView tv_now_money;
    private ImageView iv_tixian;
    private int pid;
    private String token;
    private MyAccountMoney.Result result;
    private UserInfo userInfo;
    private ProgressDialog dialog;
    private GroupMemberAdapter adapter;
    private GroupMemberResult groupMemberResult;

    //广播接收更新数据
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            getMyAccountmoney(pid, token,userInfo.getResult().getTid());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centre_myaccount);
        dialog = ProgressDialog.show(MyCentreAccountActivity.this,null,"加载中");
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("我的账户");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listView);
        tv_empter = (TextView) findViewById(R.id.tv_empter);

        tv_account_money = (TextView)findViewById(R.id.tv_account_money);
        tv_now_money = (TextView)findViewById(R.id.tv_now_money);

        iv_tixian = (ImageView)findViewById(R.id.iv_tixian);
        iv_tixian.setOnClickListener(this);

        SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        Intent intent = getIntent();
        pid = Integer.parseInt(intent.getStringExtra("pid"));
        token = intent.getStringExtra("token");
        //请求接口
        getMyAccountmoney(pid, token,userInfo.getResult().getTid());
        coachList(userInfo.getResult().getTid());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intents = new Intent();
                intents.setClass(MyCentreAccountActivity.this,CoashAccountActivity.class);
                startActivity(intents);
            }
        });
    }

    //账户余额
    private void getMyAccountmoney(int pid, String token,String tid){
        OkHttpUtils.post()
                .url(Urls.checkBalance)
                .addParams("pid", pid+"")
                .addParams("token", token)
                .addParams("tid", tid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        AppUtility.showToastMsg("请检查网络");
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("BXXC","账户余额："+s);
                        Gson gson = new Gson();
                        MyAccountMoney myAccountMoney = gson.fromJson(s, MyAccountMoney.class);
                        if (myAccountMoney.getCode() == 200){
                            result = myAccountMoney.getResult();

                            if ("".equals(result.getTotalMoney()) || result.getTotalMoney() == null){
                                tv_account_money.setText("0");
                            }else{
                                tv_account_money.setText(result.getTotalMoney());
                            }

                            if ("".equals(result.getTodayMoney()) || result.getTodayMoney() == null){
                                tv_now_money.setText("0");
                            }else{
                                tv_now_money.setText(result.getTodayMoney());
                            }

                        }
                    }
                });
    }

    /**
     * 团队教练列表
     */
    private void coachList(String center_id) {
        Log.i("百信学车","教练列表" + "tid" + center_id);
        OkHttpUtils
                .post()
                .url(Urls.centerList)
                .addParams("tid", center_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        AppUtility.showToastMsg("请检查网络");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Log.i("百信学车","教练列表结果" + s);
                        Gson gson = new Gson();
                        groupMemberResult = gson.fromJson(s,GroupMemberResult.class);
                        if(groupMemberResult.getCode() == 200){
                            if(groupMemberResult.getResult() != null){
                                adapter = new GroupMemberAdapter(MyCentreAccountActivity.this,groupMemberResult.getResult());
                                listView.setAdapter(adapter);
                            }else{
                                listView.setEmptyView(tv_empter);
                            }
                        }else{
                            AppUtility.showToastMsg(groupMemberResult.getReason());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.iv_tixian:
                if ("".equals(result.getTotalMoney()) || result.getTotalMoney() == null || "0".equals(result.getTotalMoney())){
                    new RemainBaseDialog(MyCentreAccountActivity.this, "抱歉， " +
                            "您目前的账户余额为零，暂不支持提现").call();
                }else{
                    Calendar c = Calendar.getInstance();
                    int datenum = c.get(Calendar.DATE);
                    if(datenum <= 3){
                        new WithDrawalDialog(this, "备注：申请提现后，将在3～5个工作日内到账！",pid+"",token,
                                result.getTotalMoney(),userInfo.getResult().getToAccount(),userInfo.getResult().getTid()).call();
                    }else{
                        new RemainBaseDialog(MyCentreAccountActivity.this, "抱歉，" + "提现请在每月的前三天处理").call();
                    }
                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("updataTianApp");
        registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
    }
}
