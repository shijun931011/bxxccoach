package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.IncomeRecordAdapter;
import com.jgkj.bxxccoach.adapter.WithDrawRecordAdapter;
import com.jgkj.bxxccoach.bean.IncomeRecord;
import com.jgkj.bxxccoach.bean.MyAccountMoney;
import com.jgkj.bxxccoach.bean.WithDrawRecord;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/4.
 * 教练段个人中心页面
 */

public class MyAccountActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private Button back;
    private TextView title;
    private IncomeRecordAdapter adapter;
    private TextView textView;
    private RadioButton radio_btn01, radio_btn02;
    private TextView myAccount_money;
    private TextView today_go_money;
    private Button btn_withdrawal;
    private int pid;
    private String token;
    private int currentPage = 1;

    /**
     * 教练入账明细
     *
     * @param savedInstanceState
     * pid, token, page
     */
    private String incomeUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/accountDetails";

    /**
     * 教练提现明细
     *
     * @param savedInstanceState
     * pid ,token ,  page
     */
    private String withdrawalUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/tixianDetails";

    /**
     * 教练余额提现
     *
     * @param savedInstanceState
     * pid, token
     */
    private String tixianUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/balanceTixian";

    /**
     * 教练余额查询
     *
     * @param savedInstanceState
     * pid, token
     */
    private String checkBalanceUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/checkBalance";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount);
        initView();
        getMyAccountmoney(pid, token);
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("我的账户");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        radio_btn01 = (RadioButton) findViewById(R.id.btn_income_record);
        radio_btn02 = (RadioButton) findViewById(R.id.btn_withdrawal_record);
        radio_btn01.setOnClickListener(this);
        radio_btn02.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);
        myAccount_money = (TextView) findViewById(R.id.myAccount_money);
        today_go_money = (TextView) findViewById(R.id.today_go_money);
        btn_withdrawal = (Button) findViewById(R.id.btn_withdrawal);
        btn_withdrawal.setOnClickListener(this);
        Intent intent = getIntent();
        pid = Integer.parseInt(intent.getStringExtra("pid"));
        token = intent.getStringExtra("token");
        if (pid > 0){
            getInComeRecoed(pid,token,currentPage+"");
            getwithdrawalRecord(pid,token,currentPage+"");
        }else{
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }



    private void getWithDrawal(){

    }

    private void getMyAccountmoney(int pid, String token){
        OkHttpUtils.post()
                .url(checkBalanceUrl)
                .addParams("pid", pid+"")
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(MyAccountActivity.this,"请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("BXXC","账户余额："+s);
                        Gson gson = new Gson();
                        MyAccountMoney myAccountMoney = gson.fromJson(s, MyAccountMoney.class);
                        if (myAccountMoney.getCode() == 200){
                            MyAccountMoney.Result result = myAccountMoney.getResult();
                            if (result.getTodayMoney().equals("")){
                                today_go_money.setText("￥" + 0 +"元");
                            }else{
                                today_go_money.setText("￥"+result.getTodayMoney()+"元");
                            }
                            if (result.getTotalMoney().equals("")){
                                myAccount_money.setText("￥" + 0 +"元");
                            }else{
                                myAccount_money.setText("￥"+result.getTotalMoney()+"元");
                            }

                        }
                    }
                });
    }


    private void getInComeRecoed(int pid,String token, String page) {
        OkHttpUtils.post()
                .url(incomeUrl)
                .addParams("pid", pid+"")
                .addParams("token", token)
                .addParams("page", page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(MyAccountActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        IncomeRecord incomeRecord = gson.fromJson(s, IncomeRecord.class);
                        List<IncomeRecord.Result> list = new ArrayList<IncomeRecord.Result>();
                        if (incomeRecord.getCode() == 200){
                            List<IncomeRecord.Result> results = incomeRecord.getResult();
                            list.addAll(results);
                            IncomeRecordAdapter adapter = new IncomeRecordAdapter(MyAccountActivity.this, list);
                            listView.setAdapter(adapter);
                        }
                        if(incomeRecord.getCode() == 400) {
                            textView.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        }
                    }
        });

    }

    private void getwithdrawalRecord(int pid,String token, String page) {
        OkHttpUtils.post()
                .url(withdrawalUrl)
                .addParams("pid", pid+"")
                .addParams("token", token)
                .addParams("page", page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(MyAccountActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        WithDrawRecord withDrawRecord = gson.fromJson(s, WithDrawRecord.class);
                        List<WithDrawRecord.Result> list = new ArrayList<WithDrawRecord.Result>();
                        if (withDrawRecord.getCode() == 200){
                            List<WithDrawRecord .Result> results = withDrawRecord.getResult();
                            list.addAll(results);
                            WithDrawRecordAdapter adapter = new WithDrawRecordAdapter(MyAccountActivity.this, list);
                            listView.setAdapter(adapter);
                        }
                        if(withDrawRecord.getCode() == 400) {
                            textView.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
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
            case R.id.btn_income_record:
                textView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                radio_btn01.setBackgroundResource(R.drawable.bg_selector);
                radio_btn01.setTextColor(getResources().getColor(R.color.redTheme));
                radio_btn02.setBackgroundResource(R.color.white);
                radio_btn02.setTextColor(getResources().getColor(R.color.right_bg));
                currentPage = 1;
                getInComeRecoed(pid, token, currentPage+"");
                break;
            case R.id.btn_withdrawal_record:
                textView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                radio_btn02.setBackgroundResource(R.drawable.bg_selector);
                radio_btn02.setTextColor(getResources().getColor(R.color.redTheme));
                radio_btn01.setBackgroundResource(R.color.white);
                radio_btn01.setTextColor(getResources().getColor(R.color.right_bg));
                currentPage=1;
                getwithdrawalRecord(pid, token, currentPage+"");
                break;
            case R.id.btn_withdrawal:

                break;

        }
    }


}
