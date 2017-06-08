package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.IncomeRecord;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/4.
 */

public class IncomeRecordAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<IncomeRecord.Result> list;
    private IncomeRecord.Result result;

    public IncomeRecordAdapter(Context context, List<IncomeRecord.Result> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
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
            view = inflater.inflate(R.layout.getmoney,
                    viewGroup, false);
            viewHolder.income_time = (TextView)view.findViewById(R.id.income_time);
            viewHolder.stu = (TextView) view.findViewById(R.id.stu);
            viewHolder.stu_name = (TextView) view.findViewById(R.id.stu_name);
            viewHolder.state_txt = (TextView) view.findViewById(R.id.state_txt);
            viewHolder.state = (TextView) view.findViewById(R.id.state);
            viewHolder.study_time_txt = (TextView) view.findViewById(R.id.study_time_txt);
            viewHolder.study_time = (TextView) view.findViewById(R.id.study_time);
            viewHolder.class_txt = (TextView) view.findViewById(R.id.class_txt);
            viewHolder.class_fee = (TextView) view.findViewById(R.id.class_fee);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        result = list.get(i);
        viewHolder.income_time.setText(result.getTime());
        viewHolder.stu.setText(result.getP1());
        viewHolder.stu_name.setText(result.getName());
        viewHolder.state_txt.setText(result.getP2());
        viewHolder.state.setText(result.getState());
        viewHolder.study_time_txt.setText(result.getP3());
        viewHolder.study_time.setText(result.getTime_slot());
        viewHolder.class_txt.setText(result.getP4());
        viewHolder.class_fee.setText(result.getAmount_source());
        return view;
    }


    class ViewHolder {
        public TextView income_time, stu, stu_name, state_txt, state, study_time_txt, study_time, class_txt, class_fee;

    }
}
