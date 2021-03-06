package com.jgkj.bxxccoach.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jgkj.bxxccoach.R;
import com.jgkj.bxxccoach.bean.entity.BroadcastEntity.BroadcastEntity;

import java.util.List;


public class StationDetailVideoAdapter1 extends RecyclerView.Adapter<StationDetailVideoAdapter1.MyViewHolder> {

    private LayoutInflater mInflater;
    private List<Integer> mDatas;
    private Context mContext;

    public StationDetailVideoAdapter1(Context mContext, List<Integer> objects) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mDatas = objects;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_station_detail_videolist, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.imageView.setBackgroundResource(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_station_detail_video_tv);
        }
    }
}
