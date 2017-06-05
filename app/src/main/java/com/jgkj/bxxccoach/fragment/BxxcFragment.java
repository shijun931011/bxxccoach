package com.jgkj.bxxccoach.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.activity.HeadlinesActivity;
import com.jgkj.bxxccoach.activity.WebViewActivity;
import com.jgkj.bxxccoach.adapter.MyAdapter;
import com.jgkj.bxxccoach.bean.BannerResult;
import com.jgkj.bxxccoach.bean.HeadlinesAction;
import com.jgkj.bxxccoach.tools.AutoTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;


public class BxxcFragment extends Fragment implements View.OnClickListener{
    private View view;
    private AutoTextView headlines;
    private ImageView bxhead;
    private HeadlinesAction action;
    private List<HeadlinesAction.Result> headlinesList;

    private ImageView imageView;
    private MyAdapter adapter;
    private ViewPager viewpager;
    private LinearLayout.LayoutParams wrapParams;
    private LinearLayout linearlayout;
    private int currentItem = 0;
    private Timer timer = new Timer();
    private Runnable runnable;
    private int headlinesCount = 0;
    private Handler handler = new Handler();
    //图片地址
    private String Bannerurl = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerpics";
    private String headlinesUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/nowLinesTitleAndroid";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bxxc, container, false);
        initView();
        getImage();
        getheadlines();
        headlinesList = new ArrayList<HeadlinesAction.Result>();
        return view;
    }

    private void initView(){
        //百信头条
        headlines = (AutoTextView) view.findViewById(R.id.headlines);
        headlines.setTag("nourl");
        bxhead = (ImageView) view.findViewById(R.id.bxhead);
        viewpager = (ViewPager) view.findViewById(R.id.viewPage);
        linearlayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        headlines.setOnClickListener(this);
        headlines.setText("科技改变生活，百信引领学车!");
        bxhead.setOnClickListener(this);
    }
    /**
     * 百信头条轮播文字
     */
    private void getheadlines() {
        OkHttpUtils
                .post()
                .url(headlinesUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络异常，请检查网络！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        headlines.setTag(s);
                        if (headlines.getTag() != null) {
                            setHeadlines();
                        }
                    }
                });
    }

    private void setHeadlines() {
        String headlinesTag = headlines.getTag().toString();
        Gson gson = new Gson();
        action = gson.fromJson(headlinesTag, HeadlinesAction.class);
        if (action.getCode() == 200) {
            headlinesList.addAll(action.getResult());
            runnable = new Runnable() {
                public void run() {
                    headlines.next();
                    headlines.setText(headlinesList.get(headlinesCount).getTitle());
                    headlines.setTag(headlinesList.get(headlinesCount).getUrl());
                    if (headlinesCount < (headlinesList.size()-1)) {
                        headlinesCount++;
                    } else {
                        headlinesCount = 0;
                    }
                    handler.postDelayed(this, 2000);
                }
            };
            handler.postDelayed(runnable, 2000);
        }
    }

    /**
     * 图片请求，几张图片创建相对应的viewPager+ImageView
     * 来显示图片
     */
    private void getImage() {
        OkHttpUtils
                .post()
                .url(Bannerurl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "网络状态不佳,请稍后再试！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百姓学车","轮播图"+s);
                        Gson gson = new Gson();
                        BannerResult pic = gson.fromJson(s, BannerResult.class);
                        if (pic.getCode() == 200) {
                            final List<String> list = pic.getResult();
                            if (list != null) {
                                // 实例化listView
                                List<View> listView = new ArrayList<View>();
                                for (int k = 0; k < list.size(); k++) {
                                    imageView = new ImageView(getActivity());
                                    Glide.with(getActivity()).load(list.get(k)).placeholder(R.drawable.coach_pic).error(R.drawable.coach_pic).into(imageView);
                                    imageView.setTag(list.get(k));
                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                    listView.add(imageView);
                                }
                                adapter = new MyAdapter(getActivity(), listView);
                                SharedPreferences sp = getActivity().getSharedPreferences("PicCount", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("Count", list.size());
                                editor.commit();
                                viewpager.setAdapter(adapter);
                            }
                            scrollView();
                        }
                    }
                });
    }

    //自动轮播
    private void scrollView() {
        SharedPreferences sp = getActivity().getSharedPreferences("PicCount", Activity.MODE_PRIVATE);
        final int count = sp.getInt("Count", -1);
        if (count != -1) {
            final ImageView[] dots = new ImageView[count];
            for (int k = 0; k < count; k++) {
                ImageView image = new ImageView(getActivity());
                image.setImageDrawable(getResources().getDrawable(R.drawable.selector));
                image.setId(k);
                wrapParams = new LinearLayout.LayoutParams(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
                wrapParams.leftMargin = 5;
                image.setLayoutParams(wrapParams);
                linearlayout.addView(image);
                dots[k] = (ImageView) linearlayout.getChildAt(k);
                dots[k].setEnabled(true);
            }
            final Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (currentItem < (count - 1)) {
                        currentItem++;
                        viewpager.setCurrentItem(currentItem);
                    } else if (currentItem == (count - 1)) {
                        currentItem = 0;
                        viewpager.setCurrentItem(currentItem);
                    }
                    for (int j = 0; j < count; j++) {
                        dots[j].setEnabled(false);
                    }
                    dots[currentItem].setEnabled(true);
                }
            };
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            };
            timer.schedule(timerTask, 1000, 3000);
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.headlines:
                if (headlines.getTag().toString().equals("nourl")) {
                    Toast.makeText(getActivity(), "加载中,请稍后再试!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setClass(getActivity(),WebViewActivity.class);
                    intent.putExtra("url",headlines.getTag().toString());
                    intent.putExtra("title","头条详情");
                    startActivity(intent);
                }
                break;
            case R.id.bxhead:
                Intent bxheadIntent = new Intent();
                bxheadIntent.setClass(getActivity(), HeadlinesActivity.class);
                startActivity(bxheadIntent);
                break;
        }
    }
}
