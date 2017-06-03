package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.StuAppraise;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/4.
 * 展示学员评价具体信息
 */

public class StuAppraiseAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<StuAppraise.Result> list;
    private StuAppraise.Result res;
    private LinearLayout.LayoutParams wrapParams;

    public StuAppraiseAdapter(Context context,List<StuAppraise.Result> list){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
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
            view = inflater.inflate(R.layout.stuappraise_listview,
                    viewGroup, false);
            viewHolder.day = (TextView) view.findViewById(R.id.day);
            viewHolder.comment = (TextView) view.findViewById(R.id.comment);
            viewHolder.teach = (LinearLayout) view.findViewById(R.id.stuteach);
            viewHolder.wait = (LinearLayout) view.findViewById(R.id.stuwait);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        res = list.get(i);
        viewHolder.day.setText(res.getComment_time());
        viewHolder.comment.setText(res.getComment());
        viewHolder.teach.removeAllViews();
        viewHolder.wait.removeAllViews();
        int teachnum = 0;
        try {
            teachnum = Integer.parseInt(res.getTeach());
        }catch (Exception e){
            teachnum = 1;
        }

        for (int j = 0; j < teachnum; j++) {
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.xin_1);
            wrapParams = new LinearLayout.LayoutParams(25,25);
            image.setLayoutParams(wrapParams);
            viewHolder.teach.addView(image);
        }
        int waitnum = 0;
        try {
            waitnum = Integer.parseInt(res.getWait());
        }catch (Exception e){
            waitnum = 1;
        }
        for (int j = 0; j < waitnum; j++) {
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.xin_1);
            wrapParams = new LinearLayout.LayoutParams(25,25);
            image.setLayoutParams(wrapParams);
            viewHolder.wait.addView(image);
        }
        return view;
    }

    static class ViewHolder {
        public TextView day,comment;
        public LinearLayout teach,wait;
    }
}
