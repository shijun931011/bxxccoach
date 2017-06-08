package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.WithDrawRecord;

import java.util.List;

/**
 * Created by Administrator on 2017/6/8.
 */

public class WithDrawRecordAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<WithDrawRecord.Result> list;
    private WithDrawRecord.Result result;


    public WithDrawRecordAdapter(Context context, List<WithDrawRecord.Result> list){
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
        if (view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_withdraw_record, viewGroup, false);
            viewHolder.withdraw_time = (TextView) view.findViewById(R.id.withdraw_time);
            viewHolder.withdraw_money = (TextView) view.findViewById(R.id.withdraw_money);
            viewHolder.state = (TextView) view.findViewById(R.id.state);
            viewHolder.withdraw_card = (TextView) view.findViewById(R.id.withdraw_card);
            view.setTag(viewHolder);
        }else  {
            viewHolder = (ViewHolder) view.getTag();
        }

        result = list.get(i);
        viewHolder.withdraw_time.setText(result.getTime());
        viewHolder.withdraw_money.setText(result.getMoney());
        if (result.getStatus() == "1"){
            viewHolder.state.setText("提现审核中...");
        }else if (result.getStatus() == "2"){
            viewHolder.state.setText("提现成功");
        }
        viewHolder.withdraw_card.setText(result.getToAccount());
        return view;
    }

    class ViewHolder {
        public TextView withdraw_time, withdraw_txt, withdraw_money, state_txt, state, withdraw_go, withdraw_card;

    }
}
