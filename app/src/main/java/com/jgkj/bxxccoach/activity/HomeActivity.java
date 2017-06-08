package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.fragment.BxxcFragment;
import com.jgkj.bxxccoach.fragment.PersonalFragment;
import com.jgkj.bxxccoach.fragment.ScheduleFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements View.OnClickListener{
    private ViewPager mviewPager;
    private FragmentPagerAdapter madapter;
    private List<Fragment> mfragments;
    private LinearLayout mtabBxxc;
    private LinearLayout mtabSchedule;
    private LinearLayout mtabPersonal;
    private ImageView mtabBxxcImg;
    private ImageView mtabScheduleImg;
    private ImageView mtabPersonalImg;
    private TextView  mtabBxxcTxt;
    private TextView mtabScheduleTxt;
    private TextView mtabPersonalTxt;
    private TextView title;

    private Button button_forward;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initEvent();
        //默认初始化显示第一个Tab标签
        setSelect(0);
    }
    private void initEvent(){
        mtabBxxc.setOnClickListener(this);
        mtabSchedule.setOnClickListener(this);
        mtabPersonal.setOnClickListener(this);
        mviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当滑动到一个viewPager页时，切换到对应的Tab
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mtabBxxc = (LinearLayout) findViewById(R.id.tab_bxxc);
        mtabSchedule = (LinearLayout) findViewById(R.id.tab_schedule);
        mtabPersonal = (LinearLayout) findViewById(R.id.tab_personal);
        mtabBxxcImg = (ImageView) findViewById(R.id.tab_bxxc_img);
        mtabScheduleImg = (ImageView) findViewById(R.id.tab_schedule_img);
        mtabPersonalImg = (ImageView) findViewById(R.id.tab_personal_img);
        mtabBxxcTxt = (TextView) findViewById(R.id.tab_bxxc_txt);
        mtabScheduleTxt = (TextView) findViewById(R.id.tab_schedule_txt);
        mtabPersonalTxt = (TextView) findViewById(R.id.tab_personal_txt);
        mviewPager = (ViewPager) findViewById(R.id.viewPager);
        title = ((TextView) findViewById(R.id.text_title));
        button_forward = (Button)findViewById(R.id.button_forward);

        //初始化三个Tab对应的Fragment
        mfragments = new ArrayList<Fragment>();
        Fragment msportsFragment = new BxxcFragment();
        Fragment mbarrageFragment = new ScheduleFragment();
        Fragment mPersonalCenterFragment = new PersonalFragment();
        mfragments.add(msportsFragment);
        mfragments.add(mbarrageFragment);
        mfragments.add(mPersonalCenterFragment);
        madapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mfragments.get(position);
            }

            @Override
            public int getCount() {
                return mfragments.size();
            }
        };
        mviewPager.setAdapter(madapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_bxxc:
                setSelect(0);
                break;
            case R.id.tab_schedule:
                setSelect(1);
                break;
            case R.id.tab_personal:
                setSelect(2);
                break;

        }
    }
    /**
     * 当点击Tab时，将对应的Tab图片变为亮色并显示对应的viewPager页
     *
     * @param i 选中的Tab对应的索引
     */
    private void setSelect(int i) {
        setTab(i);
        mviewPager.setCurrentItem(i);
    }

    /**
     * 将选中的Tab对应的图片变亮
     *
     * @param i 选中的Tab对应的索引
     */
    private void setTab(int i) {
        resertImg();
        switch (i) {
            case 0:
                mtabBxxcImg.setImageResource(R.drawable.main_selected);
                mtabBxxcTxt.setTextColor(this.getResources().getColor(R.color.themeColor));
                title.setText("首页");
                button_forward.setVisibility(View.GONE);
                break;
            case 1:
                mtabScheduleImg.setImageResource(R.drawable.class_selected);
                mtabScheduleTxt.setTextColor(this.getResources().getColor(R.color.themeColor));
                title.setText("我的课表");

                SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
                String str = sp.getString("CoachInfo", null);
                Gson gson = new Gson();
                userInfo = gson.fromJson(str,UserInfo.class);
                if(userInfo.getResult().getClass_type().equals("私教班") || userInfo.getResult().getClass_type().equals("陪练")){
                    button_forward.setVisibility(View.GONE);
                }else{
                    button_forward.setText("设置");
                    button_forward.setVisibility(View.VISIBLE);
                    button_forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(),StuAmountActivity.class);
                            intent.putExtra("maxNum",userInfo.getResult().getMaxnum());
                            intent.putExtra("token",userInfo.getResult().getToken());
                            startActivity(intent);
                        }
                    });
                }
                break;
            case 2:
                button_forward.setVisibility(View.GONE);
                mtabPersonalImg.setImageResource(R.drawable.me_selected);
                mtabPersonalTxt.setTextColor(this.getResources().getColor(R.color.themeColor));
                title.setText("教练");
                break;
        }
    }

    /**
     * 将所有Tab图片重置为暗色
     */
    private void resertImg() {
        mtabBxxcImg.setImageResource(R.drawable.main_unselected);
        mtabBxxcTxt.setTextColor(this.getResources().getColor(R.color.gray));
        mtabScheduleImg.setImageResource(R.drawable.class_unselect);
        mtabScheduleTxt.setTextColor(this.getResources().getColor(R.color.gray));
        mtabPersonalImg.setImageResource(R.drawable.me_unselect);
        mtabPersonalTxt.setTextColor(this.getResources().getColor(R.color.gray));
    }

}
