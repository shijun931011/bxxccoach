package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.BaseEntity.BaseEntity;
import com.jgkj.bxxccoach.bean.entity.BaseEntity.ListToJsonEntity;
import com.jgkj.bxxccoach.bean.entity.BaseEntity.ListToJsonResult;
import com.jgkj.bxxccoach.bean.entity.BroadcastEntity.BroadcastResult;
import com.jgkj.bxxccoach.tools.DividerGridItemDecoration;
import com.jgkj.bxxccoach.tools.RecyclerItemClickListener;
import com.jgkj.bxxccoach.tools.StationDetailVideoAdapter;
import com.jgkj.bxxccoach.tools.Urls;
import com.jgkj.bxxccoach.xfutils.SpeechSynthesizerUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class CreateActivity extends Activity implements View.OnClickListener, LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter{

    private Button back;
    private TextView title;
    private Button button_forward;
    private ProgressDialog dialogs;
    private BroadcastResult broadcastResult;

    private RecyclerView mRecyclerView;
    private StationDetailVideoAdapter mAdapter;
    private List<Integer> mDatas;
    private List<String> titleDatas = new ArrayList<>();
    private Context mContext;
    private TextView tv_preview;

    //显示地图需要的变量
    private MapView mapView;//地图控件
    private AMap aMap;//地图对象

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    //记录定点
    private List<LatLng> latLngsList = new ArrayList<>();

    //原点
    private Marker marker = null;

    //记录点击的是哪一个
    private int count = 0;

    private double latitude;
    private double longitude;

    private List<ListToJsonEntity> routes = new ArrayList<>();
    private UserInfo userInfo;

    private TextView cancel, ok;
    private EditText editText;

    private Dialog dialog;

    private ListToJsonResult result = new ListToJsonResult();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        dialogs = ProgressDialog.show(CreateActivity.this, null, "请求中...");

        title = (TextView) findViewById(R.id.text_title);
        title.setText("创建路线");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        button_forward = (Button)findViewById(R.id.button_forward);
        button_forward.setText("保存");
        button_forward.setVisibility(View.VISIBLE);
        button_forward.setOnClickListener(this);

        mContext = this;
        mRecyclerView = (RecyclerView)findViewById(R.id.rcv_my);

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext,4));

        //网络请求
        getBroadcast();
        //点击事件
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if(position == 0){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间在窄路与非机动车会车、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、" +
//                            "夜间在道路上发生故障，妨碍交通又难以移动、、模拟夜间考试完成，请关闭所有灯光、、请起步，继续完成考试", "mode0000");  //语音缓存采用默认路径
//                }else if(position == 1){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间在窄桥与非机动车会车、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、" +
//                            "雾天行驶、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0001");  //语音缓存采用默认路径
//                }else if(position == 2){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、在刚才同样条件下请跟前车行驶、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间与机动车会车、、、、、、、、、、、、、、、" +
//                            "夜间在道路上发生故障，妨碍交通又难以移动、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0002");  //语音缓存采用默认路径
//                }else if(position == 3){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过拱桥、人行横道、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动" +
//                            "、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0003");  //语音缓存采用默认路径
//                }else if(position == 4){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过急弯坡路、、、、、、、、、、、、、、、雾天行驶、、、、、、、、、、、、、、、模拟夜间考试完成，" +
//                            "请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0004");  //语音缓存采用默认路径
//                }else if(position == 5){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过没有交通信号灯设置的路口、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动" +
//                            "、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0005");  //语音缓存采用默认路径
//                }else if(position == 6){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过急弯坡路、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动、、、、、、、、、、、、、、、" +
//                            "模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0006");  //语音缓存采用默认路径
//                }else if(position == 7){
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
//                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间超越前方车辆、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动、、、、、、、、、、、、、、、" +
//                            "模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0007");  //语音缓存采用默认路径
//                }else if(position == 8){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("请立即起步");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "学员审核成功，请立即起步。按语音提示完成考试", "mode0008");  //语音缓存采用默认路径
//                }else if(position == 9){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("直线行驶");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "直线行驶", "mode0009");  //语音缓存采用默认路径
//                }else if(position == 10){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("变换车道");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "请变更车道", "mode00010");  //语音缓存采用默认路径
//                }else if(position == 11){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("靠边停车");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "请靠边停车", "mode00011");  //语音缓存采用默认路径
//                }else if(position == 12){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("前方路口直行");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "前方路口直行", "mode00012");  //语音缓存采用默认路径
//                }else if(position == 13){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("前方路口左转");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "前方路口左转", "mode00013");  //语音缓存采用默认路径
//                }else if(position == 14){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("前方路口右转");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "前方路口右转", "mode00014");  //语音缓存采用默认路径
//                }else if(position == 15){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("前方人行横道");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "前方人行横道", "mode00015");  //语音缓存采用默认路径
//                }else if(position == 16){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("经过学校区域");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "经过学校区域", "mode00016");  //语音缓存采用默认路径
//                }else if(position == 17){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("经过公交车站");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "经过公交车站", "mode00017");  //语音缓存采用默认路径
//                }else if(position == 18){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("与机动车会车");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "与机动车会车", "mode00018");  //语音缓存采用默认路径
//                }else if(position == 19){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("请超越前方车辆");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "请超越前方车辆", "mode00019");  //语音缓存采用默认路径
//                }else if(position == 20){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("前方合适路段掉头");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "前方合适路段掉头", "mode00020");  //语音缓存采用默认路径
//                }else if(position == 21){
//                    latLngsList.add(new LatLng(latitude,longitude));
//                    titleDatas.add("请进行百米加减档位操作");
//                    count++;
//                    aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
//                    setUpMap(latLngsList);
//                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "请进行百米加减档位操作", "mode00021");  //语音缓存采用默认路径
//                }

                if(position == 0){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间在窄路与非机动车会车、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、" +
                            "夜间在道路上发生故障，妨碍交通又难以移动、、模拟夜间考试完成，请关闭所有灯光、、请起步，继续完成考试", "mode0000");  //语音缓存采用默认路径
                }else if(position == 1){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间在窄桥与非机动车会车、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、" +
                            "雾天行驶、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0001");  //语音缓存采用默认路径
                }else if(position == 2){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、在刚才同样条件下请跟前车行驶、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间与机动车会车、、、、、、、、、、、、、、、" +
                            "夜间在道路上发生故障，妨碍交通又难以移动、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0002");  //语音缓存采用默认路径
                }else if(position == 3){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过拱桥、人行横道、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动" +
                            "、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0003");  //语音缓存采用默认路径
                }else if(position == 4){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过急弯坡路、、、、、、、、、、、、、、、雾天行驶、、、、、、、、、、、、、、、模拟夜间考试完成，" +
                            "请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0004");  //语音缓存采用默认路径
                }else if(position == 5){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过没有交通信号灯设置的路口、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动" +
                            "、、、、、、、、、、、、、、、模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0005");  //语音缓存采用默认路径
                }else if(position == 6){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间通过急弯坡路、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动、、、、、、、、、、、、、、、" +
                            "模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0006");  //语音缓存采用默认路径
                }else if(position == 7){
                    SpeechSynthesizerUtil.getInstance().makeSpeech(mContext, "下面将进行模拟夜间行驶场景灯光使用的考试，请按语音指令在5秒内做出相应的灯光操作。夜间在没有路灯、照明不良条件下行驶" +
                            "、、、、、、、、、、、、、、、请将前照灯变换成远光、、、、、、、、、、、、、、、夜间同方向近距离跟车行驶、、、、、、、、、、、、、、、夜间超越前方车辆、、、、、、、、、、、、、、、夜间在道路上发生故障，妨碍交通又难以移动、、、、、、、、、、、、、、、" +
                            "模拟夜间考试完成，请关闭所有灯光、、、、、、、、、、、、、、、请起步，继续完成考试", "mode0007");  //语音缓存采用默认路径
                }

                for(int i=8;i<broadcastResult.getResult().size();i++){
                    //判断点击的是哪一个
                    if(i == position){
                        latLngsList.add(new LatLng(latitude,longitude));
                        titleDatas.add(broadcastResult.getResult().get(position).getContent());
                        count++;
                        aMap.addMarker(getMarkerOptions(new LatLng(latitude,longitude),count));
                        setUpMap(latLngsList);
                        SpeechSynthesizerUtil.getInstance().makeSpeech(mContext,broadcastResult.getResult().get(position).getContent(), "mode0000");  //语音缓存采用默认路径
                        break;
                    }
                }
            }
        }));

        //初始化地图
        initMap(savedInstanceState);
        //开始定位
        initLoc();

    }

    private void initMap(Bundle savedInstanceState){
        //显示地图
        mapView = (MapView) findViewById(R.id.map);
        //必须要写
        mapView.onCreate(savedInstanceState);
        //获取地图对象
        aMap = mapView.getMap();

        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(false);

        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);

        //定位的小图标 默认是蓝点 这里自定义图片，其实就是一张图片
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);

        //设置Marker点击事件
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
    }

    //定位
    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(500);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        //启动定位
        mLocationClient.startLocation();
    }

    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                //保存经纬度
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();

                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(amapLocation);
                StringBuffer buffer = new StringBuffer();
                buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    aMap.clear();
                    isFirstLoc = false;
                }

                //设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                //将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longitude)));

                //清除原有的点
                if(marker != null){
                    marker.remove();
                    marker = null;
                }
                marker = aMap.addMarker(getMarkerOptionsLocation(new LatLng(latitude, longitude)));

                //画点
                for(int i=0;i<latLngsList.size();i++){
                    aMap.addMarker(getMarkerOptions(new LatLng(latLngsList.get(i).latitude, latLngsList.get(i).longitude),i + 1));
                }

                //画路线
                setUpMap(latLngsList);

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(LatLng latLng, int i) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.imag1));
        //位置
        options.position(new LatLng(latLng.latitude, latLng.longitude));
        //标题
        options.title( i + "." + titleDatas.get(i-1));

        //设置多少帧刷新一次图片资源
        options.period(60);

        return options;

    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptionsLocation(LatLng latLng) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location));
        //位置
        options.position(new LatLng(latLng.latitude, latLng.longitude));
        //标题

        //设置多少帧刷新一次图片资源
        options.period(60);

        return options;
    }

    /**
     * 在地图上画线
     */
    public void setUpMap(List<LatLng> list){
        if(list.size()>1){
            PolylineOptions polt=new PolylineOptions();
            for(int i=0;i<list.size();i++){
                polt.add(list.get(i));
            }
            polt.width(5).geodesic(true).color(Color.GREEN);
            aMap.addPolyline(polt);
        }else{
            //Toast.makeText(this,"没有移动轨迹", Toast.LENGTH_SHORT).show();
        }

    }

    //激活定位
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(R.layout.item, null);//display为自定义layout文件
        TextView name = (TextView) infoWindow.findViewById(R.id.tv_title);
        name.setText(marker.getTitle());
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward://保存
                if (latLngsList.size() != 0) {
                    dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
                    // 填充对话框的布局
                    View inflate = LayoutInflater.from(this).inflate(R.layout.route_name,null);
                    // 初始化控件
                    ok = (TextView) inflate.findViewById(R.id.ok);
                    cancel = (TextView) inflate.findViewById(R.id.cancel);
                    editText = (EditText) inflate.findViewById(R.id.editText);
                    ok.setOnClickListener(this);
                    cancel.setOnClickListener(this);
                    // 将布局设置给Dialog
                    dialog.setContentView(inflate);
                    // 获取当前Activity所在的窗体
                    Window dialogWindow = dialog.getWindow();
                    // 设置dialog横向充满
                    dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    // 设置Dialog从窗体中间弹出
                    dialogWindow.setGravity(Gravity.CENTER);
                    dialog.show();/// 显示对话框
                }else{
                    Toast.makeText(CreateActivity.this, "请先设置路线", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.ok:
                //不为空
                if(editText.getText().toString() != null && !editText.getText().toString().equals("")){
                    for(int i=0;i<latLngsList.size();i++){
                        ListToJsonEntity listToJsonEntity = new ListToJsonEntity();
                        listToJsonEntity.setId(String.valueOf(i + 1));
                        listToJsonEntity.setLat_lon(latLngsList.get(i).longitude  + "," + latLngsList.get(i).latitude);
                        routes.add(listToJsonEntity);
                    }
                    result.setResult(routes);

                    SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
                    String str = sp.getString("CoachInfo", null);
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(str,UserInfo.class);

                    String jsonArray = gson.toJson(result);
                    Log.d("百信学车","json=" + jsonArray);

                    dialogs = ProgressDialog.show(CreateActivity.this, null, "请求中...");
                    saveRoute(userInfo.getResult().getPid(), jsonArray,editText.getText().toString());

                    dialog.dismiss();
                }else{
                    Toast.makeText(CreateActivity.this, "路线名不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * 手动播报
     */
    private void getBroadcast() {
        OkHttpUtils
                .post()
                .url(Urls.broadcast)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialogs.dismiss();
                        Toast.makeText(CreateActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialogs.dismiss();
                        Log.i("百信学车","获取手动播报结果" + s);
                        Gson gson = new Gson();
                        broadcastResult = gson.fromJson(s, BroadcastResult.class);
                        mAdapter = new StationDetailVideoAdapter(mContext, broadcastResult.getResult());
                        mRecyclerView.setAdapter(mAdapter);

                    }
                });
    }

    /**
     * 保存路线
     */
    private void saveRoute(String pid,String json,String name) {
        OkHttpUtils
                .post()
                .url(Urls.getRoute)
                .addParams("pid", pid)
                .addParams("route", json)
                .addParams("name", name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialogs.dismiss();
                        Toast.makeText(CreateActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialogs.dismiss();
                        Log.i("百信学车","保存路线结果" + s);
                        Gson gson = new Gson();
                        BaseEntity baseEntity = gson.fromJson(s,BaseEntity.class);
                        Toast.makeText(CreateActivity.this, baseEntity.getReason(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
