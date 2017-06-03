package com.jgkj.bxxccoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jgkj.bxxccoach.adapter.SimpleFragmentPagerAdapter;

/**
 * Created by fangzhou on 2017/1/4.
 *
 * 学员中心
 */

public class StuCenterActivity extends FragmentActivity implements View.OnClickListener{
    //导航栏
    private Button back;
    private TextView title;
    //接受上个activity的传值
    private String maxNum,token;
    private SimpleFragmentPagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private TabLayout tabLayout;
    private Button button_forward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stu_center);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("学员中心");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);

        button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setVisibility(View.VISIBLE);
        button_forward.setText("设置");
        back.setOnClickListener(this);
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        //设置可以滑动
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        Intent intent = getIntent();
        maxNum = intent.getStringExtra("maxNum");
        token = intent.getStringExtra("token");

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case  R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
                Intent intent = new Intent();
                intent.setClass(StuCenterActivity.this,StuAmountActivity.class);
                intent.putExtra("maxNum",maxNum);
                intent.putExtra("token",token);
                startActivity(intent);
                break;
        }
    }

}
