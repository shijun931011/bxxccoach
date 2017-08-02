package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
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
import com.jgkj.bxxccoach.adapter.ExistRouteAdapter;
import com.jgkj.bxxccoach.bean.Test;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.AllRouteEntity.AllRouteResult;
import com.jgkj.bxxccoach.bean.entity.UserRouteEntity.UserRouteEntity;
import com.jgkj.bxxccoach.bean.entity.UserRouteEntity.UserRouteResult;
import com.jgkj.bxxccoach.tools.ConfirmRouteDialog;
import com.jgkj.bxxccoach.tools.Urls;
import com.jgkj.bxxccoach.xfutils.SpeechSynthesizerUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class ExistActivity extends Activity implements View.OnClickListener,LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter {

    private Button back;
    private TextView title;
    private Button button_forward;

    private LinearLayout linear_content;
    private LinearLayout linear;
    private ListView listView;
    private ImageView im_icon;
    private ExistRouteAdapter adapter;
    private boolean flag = true;
    private DisplayMetrics dm;

    //显示地图需要的变量
    private MapView mapView;//地图控件
    private AMap aMap;//地图对象

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    //原点
    private Marker marker = null;

    private double latitude;
    private double longitude;

    private ProgressDialog dialogs;
    private AllRouteResult allRouteResult;
    private UserInfo userInfo;

    //记录定点
    private List<UserRouteEntity> latLngsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist);

        dialogs = ProgressDialog.show(ExistActivity.this, null, "请求中...");

        //获取屏幕高度
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        title = (TextView) findViewById(R.id.text_title);
        title.setText("选择路线");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        button_forward = (Button)findViewById(R.id.button_forward);
        button_forward.setText("路线");
        button_forward.setVisibility(View.VISIBLE);
        button_forward.setOnClickListener(this);

        linear_content = (LinearLayout)findViewById(R.id.linear_content);
        im_icon = (ImageView)findViewById(R.id.im_icon);

        linear = (LinearLayout)findViewById(R.id.linear);
        linear.setOnClickListener(this);

        listView = (ListView)findViewById(R.id.listView);

        //获取pid
        SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ConfirmRouteDialog(ExistActivity.this,"确定使用这条线路？",userInfo.getResult().getPid(),allRouteResult.getResult().get(position).getId()).call();
            }
        });

        //获取全部路线
        getAllRoutes();

        //设置屏幕的60%高度
        ViewGroup.LayoutParams layoutlp = linear_content.getLayoutParams();
        layoutlp.height = dm.heightPixels * 6 / 10;
        linear_content.setLayoutParams(layoutlp);

        im_icon.setImageResource(R.drawable.down);
        linear_content.setVisibility(View.VISIBLE);
        flag = false;

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
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    isFirstLoc = false;
                }

                //清除原有的点
                if(marker != null){
                    marker.remove();
                }
                marker = aMap.addMarker(getMarkerOptionsLocation(new LatLng(latitude, longitude)));
                //aMap.addMarker(getMarkerOptions(new LatLng(latitude, longitude),1));
                if(ConfirmRouteDialog.userRouteList != null){
                    //画点
                    for(int i=0;i< ConfirmRouteDialog.userRouteList.size();i++){
                        double lon = subLat(ConfirmRouteDialog.userRouteList.get(i).getLat_lon());
                        double lat = subLon(ConfirmRouteDialog.userRouteList.get(i).getLat_lon());
                        LatLng latLng = new LatLng(lat, lon);
                        aMap.addMarker(getMarkerOptions(latLng,i + 1));

//                        double distends = AMapUtils.calculateLineDistance(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),new LatLng(subLat(ConfirmRouteDialog.userRouteList.get(i).getLat_lon()), subLon(ConfirmRouteDialog.userRouteList.get(i).getLat_lon())));
//                        if(distends <= 10){
//                            if(!latLngsList.contains(ConfirmRouteDialog.userRouteList.get(i))){
//                                SpeechSynthesizerUtil.getInstance().makeSpeech(ExistActivity.this, ConfirmRouteDialog.userRouteList.get(i).getContent(), "mode9988");  //语音缓存采用默认路径
//                                latLngsList.add(ConfirmRouteDialog.userRouteList.get(i));
//                            }
//                        }
                    }
                    //画路线
                    setUpMap(latLngsList);
                }
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
        options.position(latLng);
        //标题
        options.title( i + "." + "回家看地方");

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
    public void setUpMap(List<UserRouteEntity> list){
        if(list.size()>1){
            PolylineOptions polt=new PolylineOptions();
            for(int i=0;i<list.size();i++){
                LatLng ll = new LatLng(subLat(list.get(i).getLat_lon()),subLon(list.get(i).getLat_lon()));
                polt.add(ll);
            }
            polt.width(5).geodesic(true).color(Color.GREEN);
            aMap.addPolyline(polt);
        }else{
            //Toast.makeText(this,"没有移动轨迹", Toast.LENGTH_SHORT).show();
        }

    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
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
        SpeechSynthesizerUtil.getInstance().makeSpeech(ExistActivity.this, sub(marker.getTitle()), "mode8899");  //语音缓存采用默认路径
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public String sub(String ss){
        return ss.substring(ss.indexOf("."));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:

                break;
            case R.id.linear:
                if(flag == true){
                    //设置屏幕的60%高度
                    ViewGroup.LayoutParams layoutlp = linear_content.getLayoutParams();
                    layoutlp.height = dm.heightPixels * 6 / 10;
                    linear_content.setLayoutParams(layoutlp);

                    im_icon.setImageResource(R.drawable.down);
                    linear_content.setVisibility(View.VISIBLE);
                    flag = false;
                }else{
                    im_icon.setImageResource(R.drawable.up);
                    linear_content.setVisibility(View.GONE);
                    flag = true;
                }
                break;
        }
    }

    /**
     * 获取全部路线
     */
    private void getAllRoutes() {
        OkHttpUtils
                .post()
                .url(Urls.listRoute)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialogs.dismiss();
                        Toast.makeText(ExistActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialogs.dismiss();
                        Log.i("百信学车","全部路线结果" + s);
                        Gson gson = new Gson();
                        allRouteResult = gson.fromJson(s, AllRouteResult.class);
                        if(allRouteResult.getCode() == 200){
                            adapter = new ExistRouteAdapter(ExistActivity.this,allRouteResult.getResult());
                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(ExistActivity.this, allRouteResult.getReason(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    //获取经度
    private double subLat(String lat_lon){
        int i = lat_lon.indexOf(",");
        return Double.parseDouble(lat_lon.substring(0,i));
    }

    //获取维度
    private double subLon(String lat_lon){
        int i = lat_lon.indexOf(",");
        int length = lat_lon.length();
        return Double.parseDouble(lat_lon.substring(i+1,length));
    }
}
