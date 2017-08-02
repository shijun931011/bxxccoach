package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.adapter.ExistRouteAdapter;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.AllRouteEntity.AllRouteResult;
import com.jgkj.bxxccoach.tools.ConfirmRouteDialog;
import com.jgkj.bxxccoach.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;

/**
 * 选择路线
 */
public class ChooseRouteActivity extends Activity implements View.OnClickListener{

    private Button back;
    private TextView title;
    private Button button_forward;
    private ListView listView;
    private ExistRouteAdapter adapter;
    private ProgressDialog dialogs;
    private AllRouteResult allRouteResult;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        dialogs = ProgressDialog.show(ChooseRouteActivity.this, null, "请求中...");

        title = (TextView) findViewById(R.id.text_title);
        title.setText("选择路线");
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        button_forward = (Button)findViewById(R.id.button_forward);
        button_forward.setText("路线");
        button_forward.setVisibility(View.VISIBLE);
        button_forward.setOnClickListener(this);

        listView = (ListView)findViewById(R.id.listView);

        //获取pid
        SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ConfirmRouteDialog(ChooseRouteActivity.this,"确定使用这条线路？",userInfo.getResult().getPid(),allRouteResult.getResult().get(position).getId()).call();
            }
        });

        //获取全部路线
        getAllRoutes();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.button_forward:
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
                        Toast.makeText(ChooseRouteActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialogs.dismiss();
                        Log.i("百信学车","全部路线结果" + s);
                        Gson gson = new Gson();
                        allRouteResult = gson.fromJson(s, AllRouteResult.class);
                        if(allRouteResult.getCode() == 200){
                            adapter = new ExistRouteAdapter(ChooseRouteActivity.this,allRouteResult.getResult());
                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(ChooseRouteActivity.this, allRouteResult.getReason(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
