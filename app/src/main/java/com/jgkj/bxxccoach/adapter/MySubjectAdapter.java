package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.SubjectDetail;
import com.jgkj.bxxccoach.tools.CountListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by fangzhou on 2017/1/4.
 */

public class MySubjectAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<SubjectDetail.Result> list;
    private String dayTime;
    private SubjectDetail.Result res;
    private String pid;
    private String token;
    public MySubjectAdapter(Context context, List<SubjectDetail.Result> list,
                            String dayTime,String pid,String token) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.dayTime = dayTime;
        this.pid = pid;
        this.token = token;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.mysubject_listview,
                    viewGroup, false);
            viewHolder.listView = (ListView) view.findViewById(R.id.listView);
            viewHolder.day_time = (TextView) view.findViewById(R.id.day_time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        res = list.get(i);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        viewHolder.day_time.setText(sdf.format(new Date())+res.getTime());
        viewHolder.listView.setTag(res.getRes());
        MySubjectListViewAdapter adapter = new MySubjectListViewAdapter(context,pid,
                (List<SubjectDetail.Result.Res>) viewHolder.listView.getTag(),dayTime,
                res.getTime(),token);
        viewHolder.listView.setAdapter(adapter);
        new CountListView(context).setListViewHeightBasedOnChildren(viewHolder.listView);
        return view;
    }

    static class ViewHolder {
        public TextView day_time;
        public ListView listView;
    }
}
