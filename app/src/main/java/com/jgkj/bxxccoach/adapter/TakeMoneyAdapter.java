package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgkj.bxxccoach.R;

/**
 * Created by fangzhou on 2017/1/4.
 */

public class TakeMoneyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;

    public TakeMoneyAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 10;
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
            viewHolder.money_count = (TextView)view.findViewById(R.id.money_count);
            viewHolder.money_img = (ImageView) view.findViewById(R.id.money_img);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(i%2==0){
            viewHolder.money_count.setText("+400");
            viewHolder.money_count.setTextColor(Color.GREEN);
            viewHolder.money_img.setImageResource(R.drawable.moneyin);
        }else{
            viewHolder.money_count.setText("-1200");
            viewHolder.money_count.setTextColor(Color.RED);
            viewHolder.money_img.setImageResource(R.drawable.moneyout);
        }
        return view;
    }


    static class ViewHolder {
        public TextView money_count;
        public ImageView money_img;
    }
}
