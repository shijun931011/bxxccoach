package com.jgkj.bxxccoach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.bean.entity.ManagementCreditEntity.ManagementCreditResult;
import com.jgkj.bxxccoach.tools.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import me.xiaopan.swsv.CircularLayout;
import me.xiaopan.swsv.SpiderWebScoreView;
import okhttp3.Call;


/**
 * Created by tongshoujun on 2017/6/6.
 */

public class ManagementCreditActivity extends Activity {

    //标题
    private TextView title;
    private Button button_backward;
    private UserInfo userInfo;
    private ProgressDialog dialog;
    private String str[] = {"好评率","通过率","教学质量","服务态度","综合评价"};

    private SpiderWebScoreView spiderWebScoreView2;
    private CircularLayout circularLayout2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = ProgressDialog.show(ManagementCreditActivity.this, null, "请求中...");
        setContentView(R.layout.activity_management_credit);

        //标题
        title = (TextView) findViewById(R.id.text_title);
        title.setText("信用管理");
        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences sp = getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        String str = sp.getString("CoachInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str,UserInfo.class);

        //设置蜘蛛网数据
        spiderWebScoreView2 = (SpiderWebScoreView) findViewById(R.id.spiderWeb_mainActivity_2);
        circularLayout2 = (CircularLayout) findViewById(R.id.layout_mainActivity_circular2);
//        setup(spiderWebScoreView2, circularLayout2, new Score(Float.parseFloat(userInfo.getResult().getPraise())/20),
//                new Score(Float.parseFloat(userInfo.getResult().getPass())/20),
//                new Score(Float.parseFloat(userInfo.getResult().getTeach())),
//                new Score(Float.parseFloat(userInfo.getResult().getWait())),
//                new Score(Float.parseFloat(userInfo.getResult().getCredit())));
        getManagementCredit(userInfo.getResult().getPid(),userInfo.getResult().getTid());

    }

    private void setup(SpiderWebScoreView spiderWebScoreView, CircularLayout circularLayout, Score... scores){
        spiderWebScoreView.setScores(5f, assembleScoreArray(scores));
        circularLayout.removeAllViews();
        for(int i=0;i<scores.length;i++){
            //添加提示
            TextView scoreTextView = (TextView) LayoutInflater.from(getBaseContext()).inflate(R.layout.score, circularLayout, false);
            scoreTextView.setText(str[i] + "\n" + scores[i].score+"");
            if(scores[i].iconId != 0){
                scoreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, scores[i].iconId, 0);
            }
            circularLayout.addView(scoreTextView);
        }
    }

    private float[] assembleScoreArray(Score... scores){
        float[] scoreArray = new float[scores.length];
        for(int w = 0; w < scores.length; w++){
            scoreArray[w] = scores[w].score;
        }
        return scoreArray;
    }

    public static class Score{
        public float score;
        public int iconId;

        public Score(float score, int iconId) {
            this.score = score;
            this.iconId = iconId;
        }

        public Score(float score) {
            this.score = score;
        }
    }

    /**
     * 获取信用管理
     */
    private void getManagementCredit(String pid,String tid) {
        Log.d("百信学车","信用管理参数" + " pid=" + pid + " tid=" + tid);
        OkHttpUtils
                .post()
                .url(Urls.credits)
                .addParams("pid", pid)
                .addParams("tid",tid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(ManagementCreditActivity.this, "网络状态不佳，请稍后再试！", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Log.d("百信学车","信用管理结果"+s);
                        Gson gson = new Gson();
                        ManagementCreditResult managementCreditResult = gson.fromJson(s,ManagementCreditResult.class);
                        if(managementCreditResult.getCode() == 200){
                            setup(spiderWebScoreView2, circularLayout2, new Score(Float.parseFloat(managementCreditResult.getResult().getPraise())/20),
                                    new Score(Float.parseFloat(managementCreditResult.getResult().getPass())/20),
                                    new Score(Float.parseFloat(managementCreditResult.getResult().getTeach())),
                                    new Score(Float.parseFloat(managementCreditResult.getResult().getWait())),
                                    new Score(Float.parseFloat(managementCreditResult.getResult().getZonghe())));
                        }else{
                            Toast.makeText(ManagementCreditActivity.this, managementCreditResult.getReason(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
