package com.jgkj.bxxccoach.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.activity.MainActivity;
import com.jgkj.bxxccoach.activity.ManagementCreditActivity;
import com.jgkj.bxxccoach.activity.ModifyLoginPasswordActivity;
import com.jgkj.bxxccoach.activity.MySetActivity;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.Version;
import com.jgkj.bxxccoach.tools.CleanSaveDialog;
import com.jgkj.bxxccoach.tools.GetVersion;
import com.jgkj.bxxccoach.tools.GlideCacheUtil;
import com.jgkj.bxxccoach.tools.QuitDialog;
import com.jgkj.bxxccoach.tools.UpdateManger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class PersonalFragment extends Fragment implements View.OnClickListener{

    private View view;
    private LinearLayout linear_management_credit;
    private LinearLayout linear_modfly_password;
    private LinearLayout linear_clean;
    private LinearLayout linear_quit;
    private LinearLayout linear_version;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView tv_clean;
    private TextView tv_version;
    private ImageView head;
    private TextView tv_name;
    private UserInfo userInfo;

    //版本更新，点击版本号，与后台进行版本信息的交互
    private String versionUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/versionAndroidCoach";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal, container, false);
        init();
        return view;
    }

    private void init() {
        tv_clean = (TextView)view.findViewById(R.id.tv_clean);
        tv_version = (TextView)view.findViewById(R.id.tv_version);
        String str = GlideCacheUtil.getInstance().getCacheSize(getActivity());
        tv_clean.setText(str);
        tv_version.setText(GetVersion.getVersion(getActivity()));

        //设置头像，名字
        head = (ImageView)view.findViewById(R.id.head);
        tv_name = (TextView)view.findViewById(R.id.tv_name);
        SharedPreferences sp = getActivity().getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String strResult = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(strResult,UserInfo.class);
        String path = userInfo.getResult().getCoafile().toString();
//        if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
//                !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
//            Glide.with(this).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(head);
//        } else {
            Glide.with(this).load(path).error(R.drawable.defaultimg).into(head);
//        }
        tv_name.setText(userInfo.getResult().getCname());

        linear_management_credit = (LinearLayout)view.findViewById(R.id.linear_management_credit);
        linear_modfly_password = (LinearLayout)view.findViewById(R.id.linear_modfly_password);
        linear_clean = (LinearLayout)view.findViewById(R.id.linear_clean);
        linear_quit = (LinearLayout)view.findViewById(R.id.linear_quit);
        linear_version = (LinearLayout)view.findViewById(R.id.linear_version);

        linear_management_credit.setOnClickListener(this);
        linear_modfly_password.setOnClickListener(this);
        linear_clean.setOnClickListener(this);
        linear_quit.setOnClickListener(this);
        linear_version.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.linear_management_credit:
                intent.setClass(getActivity(),ManagementCreditActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_modfly_password:
                intent.setClass(getActivity(),ModifyLoginPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_version:
                checkSoftInfo();
                break;
            case R.id.linear_clean:
                new CleanSaveDialog(getActivity(),"确定清理缓存吗？",tv_clean).call();
                break;
            case R.id.linear_quit:
                new QuitDialog(getContext(),"确定退出吗？").call();
                break;
        }
    }

    /**
     * 检查更新
     */
    public  void checkSoftInfo() {
        OkHttpUtils
                .get()
                .url(versionUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        Version version = gson.fromJson(s, Version.class);
                        if (version.getCode() == 200) {
                            if (version.getResult().get(0).getVersionCode() > GetVersion.getVersionCode(getActivity())) {
                                UpdateManger updateManger = new UpdateManger(getActivity(),
                                        version.getResult().get(0).getPath(),version.getResult().get(0).getVersionName());
                                updateManger.checkUpdateInfo();
                            } else {
                                Toast.makeText(getActivity(), "已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
