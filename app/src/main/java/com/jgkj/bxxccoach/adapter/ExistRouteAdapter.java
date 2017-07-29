package com.jgkj.bxxccoach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.Test;
import com.jgkj.bxxccoach.bean.entity.AllRouteEntity.AllRouteEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/7/19.
 */

public class ExistRouteAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<AllRouteEntity> list;

    public ExistRouteAdapter(Context context, List<AllRouteEntity> list) {
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
            view = inflater.inflate(R.layout.item_exist,viewGroup, false);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_route = (TextView) view.findViewById(R.id.tv_route);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_name.setText(list.get(i).getName());
        viewHolder.tv_route.setText(list.get(i).getRoute_name());
        return view;
    }

    static class ViewHolder {
        public TextView tv_name;
        public TextView tv_route;
    }
}
