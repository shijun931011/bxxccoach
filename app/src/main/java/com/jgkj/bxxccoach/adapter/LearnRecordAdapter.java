package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.StuRecord;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/4.
 */

public class LearnRecordAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private StuRecord.Result stuRecord;
    private List<StuRecord.Result> list;
    private String file;

    public LearnRecordAdapter(Context context,List<StuRecord.Result> list,String file){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.file = file;
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
            view = inflater.inflate(R.layout.learnrecord_listview,
                    viewGroup, false);
            viewHolder.day_time = (TextView) view.findViewById(R.id.day_time);
            viewHolder.isSuccess = (TextView) view.findViewById(R.id.isSuccess);
            viewHolder.head_img = (ImageView) view.findViewById(R.id.head_img);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        stuRecord = list.get(i);
        //Glide.with(context).load(file).into(viewHolder.head_img);
        if(stuRecord.getClass_style().equals("0")){
            viewHolder.head_img.setImageResource(R.drawable.xue);
        }else{
            viewHolder.head_img.setImageResource(R.drawable.pei);
        }

        viewHolder.day_time.setText(stuRecord.getDay()+" "+stuRecord.getTime_slot());
        if(stuRecord.getStatement().equals("0")){
            viewHolder.isSuccess.setText("预约成功");
            viewHolder.isSuccess.setTextColor(context.getResources().getColor(R.color.orange));
        }else if(stuRecord.getStatement().equals("1")){
            viewHolder.isSuccess.setText("已学车");
            viewHolder.isSuccess.setTextColor(context.getResources().getColor(R.color.green));
        }else{
            viewHolder.isSuccess.setText("未到场");
            viewHolder.isSuccess.setTextColor(context.getResources().getColor(R.color.redTheme));
        }
        return view;
    }


    static class ViewHolder {
        public TextView day_time,isSuccess;
        public ImageView head_img;
    }
}
