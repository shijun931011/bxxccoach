package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.entity.ScheduleEntity.ScheduleEntity;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> list;
    private List<ScheduleEntity> scheduleList;
    public List<String> time_solt = new ArrayList<>();

    public ScheduleAdapter(Context context, List<String> list ,List<ScheduleEntity> scheduleList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.scheduleList = scheduleList;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.select_schedule_item, viewGroup, false);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            viewHolder.tv_mune = (TextView) view.findViewById(R.id.tv_mune);
            viewHolder.linear = (LinearLayout) view.findViewById(R.id.linear);
            viewHolder.views = (View) view.findViewById(R.id.view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.checkBox.setChecked(false);

        if(i == list.size()-1){
            viewHolder.views.setVisibility(View.GONE);
        }else{
            viewHolder.views.setVisibility(View.VISIBLE);
        }

        //判断课表是否选中
        if(scheduleList.size() != 0){
            time_solt.clear();
            for(int j=0;j<scheduleList.get(0).getTime_slot().size();j++){
                if(scheduleList.get(0).getTime_slot().get(j).equals(list.get(i).toString())){
                    viewHolder.checkBox.setChecked(true);
                }
                //保存已选课
                if(!time_solt.contains(scheduleList.get(0).getTime_slot().get(j))){
                    time_solt.add(scheduleList.get(0).getTime_slot().get(j));
                }
            }
        }
        viewHolder.tv_mune.setText(list.get(i).toString());

//        //设置checkbox状态
//        viewHolder.linear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CheckBox checkBox = (CheckBox)v.findViewById(R.id.checkBox);
//                if(checkBox.isChecked()){
//                    checkBox.setChecked(false);
//                    if(time_solt.contains(list.get(i).toString())){
//                        time_solt.remove(list.get(i).toString());
//                    }
//                }else{
//                    checkBox.setChecked(true);
//                    if(!time_solt.contains(list.get(i).toString())){
//                        time_solt.add(list.get(i).toString());
//                    }
//                }
//            }
//        });

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v.findViewById(R.id.checkBox);
                if(checkBox.isChecked()){
                    if(!time_solt.contains(list.get(i).toString())){
                        time_solt.add(list.get(i).toString());
                    }
                }else{
                    if(time_solt.contains(list.get(i).toString())){
                        time_solt.remove(list.get(i).toString());
                    }
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        public CheckBox checkBox;
        public TextView tv_mune;
        public LinearLayout linear;
        public View views;
    }
}
