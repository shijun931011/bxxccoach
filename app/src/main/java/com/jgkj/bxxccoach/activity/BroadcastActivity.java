package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.entity.BroadcastEntity.BroadcastResult;
import com.jgkj.bxxccoach.tools.DividerGridItemDecoration;
import com.jgkj.bxxccoach.tools.RecyclerItemClickListener;
import com.jgkj.bxxccoach.tools.StationDetailVideoAdapter;
import com.jgkj.bxxccoach.tools.Urls;
import com.jgkj.bxxccoach.xfutils.SpeechSynthesizerUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class BroadcastActivity extends Activity implements View.OnClickListener{

    private Button back;
    private TextView title;
    private ProgressDialog dialog;

    private RecyclerView mRecyclerView;
    private StationDetailVideoAdapter mAdapter;
    private Context mContext;

    private BroadcastResult broadcastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        dialog = ProgressDialog.show(BroadcastActivity.this, null, "请求中...");

        title = (TextView) findViewById(R.id.text_title);
        title.setText("手动播报");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

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
                for(int i=0;i<broadcastResult.getResult().size();i++){
                    //判断点击的是哪一个
                    if(i == position){
                        SpeechSynthesizerUtil.getInstance().makeSpeech(mContext,broadcastResult.getResult().get(position).getContent(), "mode0000");  //语音缓存采用默认路径
                        break;
                    }
                }
            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
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
                        dialog.dismiss();
                        Toast.makeText(BroadcastActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Log.i("百信学车","获取手动播报结果" + s);
                        Gson gson = new Gson();
                        broadcastResult = gson.fromJson(s, BroadcastResult.class);
                        mAdapter = new StationDetailVideoAdapter(mContext, broadcastResult.getResult());
                        mRecyclerView.setAdapter(mAdapter);

                    }
                });
    }

}
