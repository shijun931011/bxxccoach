package com.jgkj.bxxccoach;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxccoach.adapter.CoachStuAdapter;
import com.jgkj.bxxccoach.bean.StuMsg;
import com.jgkj.bxxccoach.bean.UserInfo;
import com.jgkj.bxxccoach.tools.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2017/1/4.
 *
 * 展示所带所有学员状态
 */

public class PageFragment extends Fragment implements AdapterView.OnItemClickListener,
        RefreshLayout.OnLoadListener,SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    private CoachStuAdapter adapter;

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private String learningUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/myStuStudy";
    private String failureUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/myStuTextNoPass";
    private String nextTestUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/myStuText";
    private String successUrl = "http://www.baixinxueche.com/index.php/Home/Apicoachtoken/myStuTextPass";
    private String pid;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserInfo user;
    private ProgressDialog dialog;
    private StuMsg stuMsg;
    private List<StuMsg.Result> list = new ArrayList<>();
    private RefreshLayout swipeLayout;
    private int currentPage = 1;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE",page);
        PageFragment pageFragment = new PageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取page页数
        mPage = getArguments().getInt(ARG_PAGE);
    }

    /**
     * 初始化布局
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        //下拉刷新
        swipeLayout = (RefreshLayout) view.findViewById(R.id.refresh);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        swipeLayout.setTag("isfirst");
        list.clear();
        currentPage = 1;
        sp = getActivity().getSharedPreferences("Coach", Activity.MODE_PRIVATE);
        editor = sp.edit();
        Gson gson = new Gson();
        if(sp.getString("CoachInfo",null)!=null){
            user = gson.fromJson(sp.getString("CoachInfo",null),UserInfo.class);
            pid = user.getResult().getPid();
        }
        //Page滑动和点击页数内容加载
        switch (mPage){
            case 0:
                dialog = ProgressDialog.show(getActivity(),null,"加载中...");
                getStu(learningUrl,currentPage+"");
                break;
            case 1:
                dialog = ProgressDialog.show(getActivity(),null,"加载中...");
                getStu(nextTestUrl,currentPage+"");
                break;
            case 2:
                dialog = ProgressDialog.show(getActivity(),null,"加载中...");
                getStu(successUrl,currentPage+"");
                break;
            case 3:
                dialog = ProgressDialog.show(getActivity(),null,"加载中...");
                getStu(failureUrl,currentPage+"");
                break;
        }
        listView = (ListView) view.findViewById(R.id.coach_Stu_ListView);
        listView.setOnItemClickListener(this);
        return view;
    }

    //读取数据并且解析处理
    private void getList(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        stuMsg = gson.fromJson(str,StuMsg.class);
        if(stuMsg.getCode()==200){
            list.addAll(stuMsg.getResult());
            if(swipeLayout.getTag().toString().equals("isfirst")){
                adapter = new CoachStuAdapter(getActivity(),list);
                listView.setAdapter(adapter);
            }if(swipeLayout.getTag().toString().equals("refresh")){
                adapter = new CoachStuAdapter(getActivity(),list);
                listView.setAdapter(adapter);
            }else if(swipeLayout.getTag().toString().equals("onLoad")){
                adapter.notifyDataSetChanged();
            }
        }else{
            Toast.makeText(getActivity(),stuMsg.getReason(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 学员获取
     */
    private void getStu(String url,String page) {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("pid", pid)
                .addParams("page",page)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "网络状态不佳，请稍后再试！", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        listView.setTag(s);
                        if(listView.getTag()!=null){
                            getList();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try{
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView state = (TextView) view.findViewById(R.id.state);
            TextView countTime = (TextView) view.findViewById(R.id.countTime);
            TextView phone = (TextView) view.findViewById(R.id.phone);
            TextView time = (TextView) view.findViewById(R.id.time);
            Intent intent = new Intent();
            intent.setClass(getActivity(),LearnRecordActivity.class);
            intent.putExtra("name",name.getTag().toString());
            intent.putExtra("state",state.getTag().toString());
            intent.putExtra("countTime",countTime.getTag().toString());
            intent.putExtra("phone",phone.getTag().toString());
            intent.putExtra("time",time.getTag().toString());
            intent.putExtra("pid",pid);
            startActivity(intent);
        }catch (Exception e){
            Log.i("TAG","数据错误！");
        }

    }

    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage++;
                swipeLayout.setTag("onLoad");
                switch (mPage){
                    case 0:
                        getStu(learningUrl,currentPage+"");
                        break;
                    case 1:
                        getStu(nextTestUrl,currentPage+"");
                        break;
                    case 2:
                        getStu(successUrl,currentPage+"");
                        break;
                    case 3:
                        getStu(failureUrl,currentPage+"");
                        break;
                }
                swipeLayout.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
       try{
           swipeLayout.postDelayed(new Runnable() {
               @Override
               public void run() {
                   int rePage = mPage;
                   currentPage = 1;
                   swipeLayout.setTag("refresh");
                   list.clear();
                   switch (rePage){
                       case 0:
                           getStu(learningUrl,currentPage+"");
                           break;
                       case 1:
                           getStu(nextTestUrl,currentPage+"");
                           break;
                       case 2:
                           getStu(successUrl,currentPage+"");
                           break;
                       case 3:
                           getStu(failureUrl,currentPage+"");
                           break;
                   }
                   swipeLayout.setRefreshing(false);
               }
           }, 2000);
       }catch (Exception e){
           Log.i("TAG","数据错误！");
       }

    }
}
