package com.jgkj.bxxccoach.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.ScheduleAdapter;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxccoach.bean.entity.ScheduleEntity.ScheduleEntity;
import com.jgkj.bxxccoach.bean.entity.ScheduleEntity.ScheduleResult;
import com.jgkj.bxxccoach.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

//排课页面
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
    private TextView tv_day;
    private ListView listView;
    private Button btn_confirm;
    private Button button_forward;

    private ScheduleAdapter adapter;
    private List<ScheduleEntity> scheduleList = new ArrayList<>();
    private List<ScheduleEntity> scheduleTempList = new ArrayList<>();
    private List<String> stringList;

    private UserInfo userInfo;
    //标记哪一天
    private String day;
    private String MM;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        //教练排课
        tv_day = (TextView)view.findViewById(R.id.tv_day);
        tv_day.setText(getTempDay(0));
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

        //day默认为当天
        day = getTempDay(0);
        MM = tv1_number.getText().toString();
        btn_confirm = (Button)view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.listView);
        SharedPreferences sp = getActivity().getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        //请求数据
        getData(userInfo.getResult().getPid(), Urls.makeCourse);

    }
    //获取一个星期日期 格式dd
    public String getDay(int day){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, day);
        String dd = sf.format(c.getTime()).toString();
        return dd.substring(dd.length()-2,dd.length());
    }

    //获取 i 天后的时间
    public String getTempDay(int i){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, i);
        String dd = sf.format(c.getTime()).toString();
        return dd;
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

                day = getTempDay(0);
                MM = tv1_number.getText().toString();
                scheduleTempList = doscheduleList(tv1_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout2:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                day = getTempDay(1);
                MM = tv2_number.getText().toString();
                scheduleTempList = doscheduleList(tv2_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout3:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                day = getTempDay(2);
                MM = tv3_number.getText().toString();
                scheduleTempList = doscheduleList(tv3_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout4:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                day = getTempDay(3);
                MM = tv4_number.getText().toString();
                scheduleTempList = doscheduleList(tv4_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout5:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.white);

                day = getTempDay(4);
                MM = tv5_number.getText().toString();
                scheduleTempList = doscheduleList(tv5_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout6:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.list_text_select_color);
                tv_bg_07.setBackgroundResource(R.color.white);

                day = getTempDay(5);
                MM = tv6_number.getText().toString();
                scheduleTempList = doscheduleList(tv6_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.linearLayout7:
                tv_bg_01.setBackgroundResource(R.color.white);
                tv_bg_02.setBackgroundResource(R.color.white);
                tv_bg_03.setBackgroundResource(R.color.white);
                tv_bg_04.setBackgroundResource(R.color.white);
                tv_bg_05.setBackgroundResource(R.color.white);
                tv_bg_06.setBackgroundResource(R.color.white);
                tv_bg_07.setBackgroundResource(R.color.list_text_select_color);

                day = getTempDay(6);
                MM = tv7_number.getText().toString();
                scheduleTempList = doscheduleList(tv7_number.getText().toString());
                adapter = new ScheduleAdapter(getActivity(), stringList, scheduleTempList);
                listView.setAdapter(adapter);
                break;
            case R.id.btn_confirm://提交课表
                String time_solt = "";
                //排序
                Collections.sort(adapter.time_solt);
                for(int i=0;i<adapter.time_solt.size();i++){
                    time_solt = time_solt + adapter.time_solt.get(i) + ",";
                }
                //处理最后逗号
                if(time_solt.length() != 0){
                    time_solt = time_solt.substring(0,time_solt.length()-1);
                }
                dialog = ProgressDialog.show(getActivity(), null, "请求中...");
                getSubmitTimetable(userInfo.getResult().getPid(),day,time_solt,Urls.coachApplyCourseAgain);
                adapter.time_solt.clear();
                break;
        }
    }

    //教练排课
    private void getData(String pid,String url) {
        Log.i("百信学车","课表参数" + "pid=" + pid + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        //dialog.dismiss();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        //dialog.dismiss();
                        Log.i("百信学车","课表结果" + s);
                        Gson gson = new Gson();

                        ScheduleResult scheduleResult = gson.fromJson(s,ScheduleResult.class);
                        scheduleList = scheduleResult.getResult();

                        GetscheduleList(scheduleResult.getInfo());
                        scheduleTempList = doscheduleList(tv1_number.getText().toString());
                        adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                        listView.setAdapter(adapter);
                    }
                });
    }

    //教练排课
    private void getUpData(String pid,String url) {
        Log.i("百信学车","课表参数" + "pid=" + pid + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","课表结果" + s);
                        Gson gson = new Gson();

                        ScheduleResult scheduleResult = gson.fromJson(s,ScheduleResult.class);
                        scheduleList = scheduleResult.getResult();

                        scheduleTempList.clear();
                        scheduleTempList = doscheduleList(MM);
                        adapter = new ScheduleAdapter(getActivity(), stringList,scheduleTempList);
                        listView.setAdapter(adapter);
                    }
                });
    }

    //教练提交课表
    private void getSubmitTimetable(String pid,String day,String time_slot,String url) {
        Log.i("百信学车","提交课表参数" + "pid=" + pid +"   day=" + day + "   time_slot=" + time_slot + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .addParams("day", day)
                .addParams("time_slot", time_slot)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Log.i("百信学车","提交课表结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s, BaseEntity.class);
                        if (baseEntity.getCode() == 200) {
                            Toast.makeText(getActivity(), "排课成功", Toast.LENGTH_LONG).show();
                            getUpData(userInfo.getResult().getPid(), Urls.makeCourse);
                        }else{
                            Toast.makeText(getActivity(), baseEntity.getReason(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    //处理返回的排课时间段
    public void GetscheduleList(String info){
        stringList = Arrays.asList(info.split(","));
    }

    //获取点击日期所对应的数据
    public List<ScheduleEntity> doscheduleList(String day){
        List<ScheduleEntity> list = new ArrayList<>();
        for (ScheduleEntity subject: scheduleList) {
            if(subString(subject.getDay()).equals(day)){
                list.add(subject);
            }
        }
        return list;
    }

    //截取字符串
    public String subString(String str){
        int length = str.length();
        return str.substring(length-3,length-1);
    }

}
