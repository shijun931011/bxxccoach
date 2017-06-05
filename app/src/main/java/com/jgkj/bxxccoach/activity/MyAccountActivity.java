package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.TakeMoneyAdapter;

/**
 * Created by fangzhou on 2017/1/4.
 * 教练段个人中心页面
 *
 */

public class MyAccountActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private Button back;
    private TextView title;
    private TakeMoneyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("我的账户");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.accountListView);
        adapter = new TakeMoneyAdapter(MyAccountActivity.this);
        listView.setAdapter(adapter);
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
