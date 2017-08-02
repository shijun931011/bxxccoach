package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.IncomeRecordAdapter;
import com.jgkj.bxxccoach.adapter.WithDrawRecordAdapter;
import com.jgkj.bxxccoach.bean.IncomeRecord;
import com.jgkj.bxxccoach.bean.MyAccountMoney;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.WithDrawRecord;
import com.jgkj.bxxccoach.tools.AppUtility;
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
 * 教练端个人账户页面
 */

public class MyAccountActivity extends Activity implements View.OnClickListener,
        RefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private RefreshLayout refresh;
    private Button back;
    private TextView title,tv_empter,tv_account_balance,tv_anccount_tixian;
    private IncomeRecordAdapter incomeAdapter;
    private WithDrawRecordAdapter withDrawAdapter;
    private TextView tv_account_money;
    private TextView tv_now_money;
    private ImageView iv_tixian;
    private Button btn_withdrawal;
    private int pid;
    private String token;
    private int currentPage = 1;
    private MyAccountMoney.Result result;
    private UserInfo userInfo;
    private List<WithDrawRecord.Result> withDrawList = new ArrayList<>();
    private List<IncomeRecord.Result> incomList = new ArrayList<IncomeRecord.Result>();
    private LinearLayout linear_balance;//进账
    private LinearLayout linear_tixian;//提现
    private boolean falg = true;//标志是进账页面
    private ProgressDialog dialog;

    //广播接收更新数据
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            getMyAccountmoney(pid, token,userInfo.getResult().getTid());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        dialog = ProgressDialog.show(MyAccountActivity.this,null,"加载中");
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("我的账户");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        tv_account_money = (TextView)findViewById(R.id.tv_account_money);
        tv_now_money = (TextView)findViewById(R.id.tv_now_money);

        tv_account_balance = (TextView)findViewById(R.id.tv_account_balance);
        tv_anccount_tixian = (TextView)findViewById(R.id.tv_anccount_tixian);

        listView = (ListView) findViewById(R.id.listView);
        tv_empter = (TextView) findViewById(R.id.tv_empter);

        iv_tixian = (ImageView)findViewById(R.id.iv_tixian);
        iv_tixian.setOnClickListener(this);

        linear_balance = (LinearLayout)findViewById(R.id.linear_balance);
        linear_tixian = (LinearLayout)findViewById(R.id.linear_tixian);
        linear_balance.setOnClickListener(this);
        linear_tixian.setOnClickListener(this);

        refresh = (RefreshLayout) findViewById(R.id.refresh);
        refresh.setTag("refresh");
        refresh.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        refresh.setOnRefreshListener(this);
        refresh.setOnLoadListener(this);

        SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        Intent intent = getIntent();
        pid = Integer.parseInt(intent.getStringExtra("pid"));
        token = intent.getStringExtra("token");

        linear_balance.setBackgroundResource(R.color.linearBack);
        linear_tixian.setBackgroundResource(R.color.whitewhite);

        tv_account_balance.setTextColor(getResources().getColor(R.color.whitewhite));
        tv_anccount_tixian.setTextColor(getResources().getColor(R.color.textColor));

        getMyAccountmoney(pid, token,userInfo.getResult().getTid());
        getInComeRecoed(pid,token,String.valueOf(currentPage));
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

    //进账记录
    private void getInComeRecoed(int pid,String token, String page) {
        OkHttpUtils.post()
                .url(Urls.gerenAccountDetails)
                .addParams("pid", pid+"")
                .addParams("token", token)
                .addParams("page", page)
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
                        Log.i("BXXC","入账记录："+s);
                        Gson gson = new Gson();
                        IncomeRecord incomeRecord = gson.fromJson(s, IncomeRecord.class);

                        if (incomeRecord.getCode() == 200){
                            refresh.setVisibility(View.VISIBLE);
                            String ss = refresh.getTag().toString();
                            if(ss.equals("refresh")){
                                incomList = incomeRecord.getResult();
                                incomeAdapter = new IncomeRecordAdapter(MyAccountActivity.this, incomList);
                                listView.setAdapter(incomeAdapter);
                            }
                            if(ss.equals("onload")){
                                List<IncomeRecord.Result> results = incomeRecord.getResult();
                                incomList.addAll(results);
                                incomeAdapter.notifyDataSetChanged();
                            }
                        }
                        if(incomeRecord.getCode() == 400) {
                            if(incomList.size() == 0){
                                refresh.setVisibility(View.GONE);
                            }else{
                                //Toast.makeText(MyAccountActivity.this, "没有更多记录", Toast.LENGTH_LONG).show();
                                AppUtility.showToastMsg("没有更多记录");
                            }
                        }
                    }
        });

    }

    //提现记录
    private void getwithdrawalRecord(int pid,String token, String page) {
        OkHttpUtils.post()
                .url(Urls.gerenTixianDetails)
                .addParams("pid", pid+"")
                .addParams("token", token)
                .addParams("page", page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        AppUtility.showToastMsg("请检查网络");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("BXXC","提现记录："+s);
                        Gson gson = new Gson();
                        WithDrawRecord withDrawRecord = gson.fromJson(s, WithDrawRecord.class);

                        if (withDrawRecord.getCode() == 200){
                            refresh.setVisibility(View.VISIBLE);
                            String ss = refresh.getTag().toString();
                            if(ss.equals("refresh")){
                                withDrawList = withDrawRecord.getResult();
                                withDrawAdapter = new WithDrawRecordAdapter(MyAccountActivity.this, withDrawList);
                                listView.setAdapter(withDrawAdapter);
                            }
                            if(ss.equals("onload")){
                                List<WithDrawRecord .Result> results = withDrawRecord.getResult();
                                withDrawList.addAll(results);
                                withDrawAdapter.notifyDataSetChanged();
                            }
                        }
                        if(withDrawRecord.getCode() == 400) {
                            if(withDrawList.size() == 0){
                                refresh.setVisibility(View.GONE);
                            }else{
                                //Toast.makeText(MyAccountActivity.this, "没有更多记录", Toast.LENGTH_LONG).show();
                                AppUtility.showToastMsg("没有更多记录");
                            }
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
            case R.id.linear_balance://进账记录

                linear_balance.setBackgroundResource(R.color.linearBack);
                linear_tixian.setBackgroundResource(R.color.whitewhite);

                tv_account_balance.setTextColor(getResources().getColor(R.color.whitewhite));
                tv_anccount_tixian.setTextColor(getResources().getColor(R.color.textColor));

                Drawable drawable1 = getResources().getDrawable(R.drawable.income);
                /// 这一步必须要做,否则不会显示.
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                tv_account_balance.setCompoundDrawables(drawable1,null,null,null);

                Drawable drawable2 = getResources().getDrawable(R.drawable.anccount_tixian);
                /// 这一步必须要做,否则不会显示.
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                tv_anccount_tixian.setCompoundDrawables(drawable2,null,null,null);

                falg = true;
                currentPage = 1;
                refresh.setTag("refresh");
                incomList.clear();
                refresh.setVisibility(View.VISIBLE);
                getInComeRecoed(pid,token,String.valueOf(currentPage));
                break;
            case R.id.linear_tixian://提现记录

                linear_balance.setBackgroundResource(R.color.whitewhite);
                linear_tixian.setBackgroundResource(R.color.linearBack);

                Drawable drawable3 = getResources().getDrawable(R.drawable.income2);
                /// 这一步必须要做,否则不会显示.
                drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                tv_account_balance.setCompoundDrawables(drawable3,null,null,null);

                tv_account_balance.setTextColor(getResources().getColor(R.color.textColor));
                tv_anccount_tixian.setTextColor(getResources().getColor(R.color.whitewhite));

                Drawable drawable4 = getResources().getDrawable(R.drawable.anccount_tixian2);
                /// 这一步必须要做,否则不会显示.
                drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                tv_anccount_tixian.setCompoundDrawables(drawable4,null,null,null);

                falg = false;
                currentPage = 1;
                refresh.setTag("refresh");
                withDrawList.clear();
                refresh.setVisibility(View.VISIBLE);
                getwithdrawalRecord(pid,token,String.valueOf(currentPage));
                break;
            case R.id.iv_tixian:
                if ("".equals(result.getTotalMoney()) || result.getTotalMoney() == null || "0".equals(result.getTotalMoney())){
                    new RemainBaseDialog(MyAccountActivity.this, "抱歉， " +
                            "您目前的账户余额为零，暂不支持提现").call();
                }else{
                    Calendar c = Calendar.getInstance();
                    int datenum = c.get(Calendar.DATE);
                    if(datenum <= 3){
                        new WithDrawalDialog(this, "备注：申请提现后，将在3～5个工作日内到账！",pid+"",token,
                                result.getTotalMoney(),userInfo.getResult().getToAccount(),userInfo.getResult().getTid()).call();
                    }else{
                        new RemainBaseDialog(MyAccountActivity.this, "抱歉，" + "提现请在每月的前三天处理").call();
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

    @Override
    public void onRefresh() {
        currentPage = 1;
        refresh.setTag("refresh");
        if(falg == true){
            incomList.clear();
            getInComeRecoed(pid,token,String.valueOf(currentPage));
        }else{
            withDrawList.clear();
            getwithdrawalRecord(pid,token,String.valueOf(currentPage));
        }
        refresh.setRefreshing(false);
    }

    @Override
    public void onLoad() {
        currentPage++;
        refresh.setTag("onload");
        if(falg == true){
            getInComeRecoed(pid,token,String.valueOf(currentPage));
        }else{
            getwithdrawalRecord(pid,token,String.valueOf(currentPage));
        }
        refresh.setLoading(false);
    }
}
