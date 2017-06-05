package com.jgkj.bxxccoach.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleFragment extends Fragment implements View.OnClickListener{

    private TextView tv1_week;
    private TextView tv2_week;
    private TextView tv3_week;
    private TextView tv4_week;
    private TextView tv5_week;
    private TextView tv6_week;
    private TextView tv7_week;
    private TextView tv1_number;
    private TextView tv2_number;
    private TextView tv3_number;
    private TextView tv4_number;
    private TextView tv5_number;
    private TextView tv6_number;
    private TextView tv7_number;
    private TextView tv_bg_01;
    private TextView tv_bg_02;
    private TextView tv_bg_03;
    private TextView tv_bg_04;
    private TextView tv_bg_05;
    private TextView tv_bg_06;
    private TextView tv_bg_07;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4;
    private LinearLayout linearLayout5;
    private LinearLayout linearLayout6;
    private LinearLayout linearLayout7;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        initView();
        return view;
    }


    private void initView(){

        tv1_week = (TextView) view.findViewById(R.id.tv1_week);
        tv2_week = (TextView) view.findViewById(R.id.tv2_week);
        tv3_week = (TextView) view.findViewById(R.id.tv3_week);
        tv4_week = (TextView) view.findViewById(R.id.tv4_week);
        tv5_week = (TextView) view.findViewById(R.id.tv5_week);
        tv6_week = (TextView) view.findViewById(R.id.tv6_week);
        tv7_week = (TextView) view.findViewById(R.id.tv7_week);
        tv1_number = (TextView) view.findViewById(R.id.tv1_number);
        tv2_number = (TextView) view.findViewById(R.id.tv2_number);
        tv3_number = (TextView) view.findViewById(R.id.tv3_number);
        tv4_number = (TextView) view.findViewById(R.id.tv4_number);
        tv5_number = (TextView) view.findViewById(R.id.tv5_number);
        tv6_number = (TextView) view.findViewById(R.id.tv6_number);
        tv7_number = (TextView) view.findViewById(R.id.tv7_number);

        tv_bg_01 = (TextView) view.findViewById(R.id.tv_bg_01);
        tv_bg_02 = (TextView) view.findViewById(R.id.tv_bg_02);
        tv_bg_03 = (TextView) view.findViewById(R.id.tv_bg_03);
        tv_bg_04 = (TextView) view.findViewById(R.id.tv_bg_04);
        tv_bg_05 = (TextView) view.findViewById(R.id.tv_bg_05);
        tv_bg_06 = (TextView) view.findViewById(R.id.tv_bg_06);
        tv_bg_07 = (TextView) view.findViewById(R.id.tv_bg_07);

        linearLayout1 = (LinearLayout) view.findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) view.findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout) view.findViewById(R.id.linearLayout3);
        linearLayout4 = (LinearLayout) view.findViewById(R.id.linearLayout4);
        linearLayout5 = (LinearLayout) view.findViewById(R.id.linearLayout5);
        linearLayout6 = (LinearLayout) view.findViewById(R.id.linearLayout6);
        linearLayout7 = (LinearLayout) view.findViewById(R.id.linearLayout7);

        linearLayout1.setOnClickListener(this);
        linearLayout2.setOnClickListener(this);
        linearLayout3.setOnClickListener(this);
        linearLayout4.setOnClickListener(this);
        linearLayout5.setOnClickListener(this);
        linearLayout6.setOnClickListener(this);
        linearLayout7.setOnClickListener(this);

        //设置星期
        tv1_week.setText(getWeek(0));
        tv2_week.setText(getWeek(1));
        tv3_week.setText(getWeek(2));
        tv4_week.setText(getWeek(3));
        tv5_week.setText(getWeek(4));
        tv6_week.setText(getWeek(5));
        tv7_week.setText(getWeek(6));

        //设置日期号
        tv1_number.setText(getDay(0));
        tv2_number.setText(getDay(1));
        tv3_number.setText(getDay(2));
        tv4_number.setText(getDay(3));
        tv5_number.setText(getDay(4));
        tv6_number.setText(getDay(5));
        tv7_number.setText(getDay(6));

        tv_bg_01.setBackgroundResource(R.color.list_text_select_color);
        tv_bg_02.setBackgroundResource(R.color.white);
        tv_bg_03.setBackgroundResource(R.color.white);
        tv_bg_04.setBackgroundResource(R.color.white);
        tv_bg_05.setBackgroundResource(R.color.white);
        tv_bg_06.setBackgroundResource(R.color.white);
        tv_bg_07.setBackgroundResource(R.color.white);

    }
    //获取一个星期日期 格式dd
    public String getDay(int day){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //System.out.println("当前日期："+sf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, day);
        String dd = sf.format(c.getTime()).toString();
        return dd.substring(dd.length()-2,dd.length());
    }

    //日期转星期
    public String dateToWeek(Date dt) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //获取星期
    public String getWeek(int day){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        //System.out.println("当前日期："+sf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, day);
        String dd = sf.format(c.getTime()).toString();
        String week = "";
        try {
            Date date = sf.parse(dd);
            week = dateToWeek(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.linearLayout1:
                tv_bg_01.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);


                break;
            case R.id.linearLayout2:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);


                break;
            case R.id.linearLayout3:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);


                break;
            case R.id.linearLayout4:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);


                break;
            case R.id.linearLayout5:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);


                break;
            case R.id.linearLayout6:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_07.setBackgroundResource(R.color.white);


                break;
            case R.id.linearLayout7:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.list_text_select_color);


                break;
        }
    }




}
