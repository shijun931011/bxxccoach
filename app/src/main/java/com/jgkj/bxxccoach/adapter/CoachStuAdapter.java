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
import com.jgkj.bxxccoach.bean.StuMsg;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/4.
 */

public class CoachStuAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private StuMsg.Result stuMsg;
    private List<StuMsg.Result> list;
    private String defaultImgUrl = "http://www.baixinxueche.com/Public/Home/img/default.png";


    public CoachStuAdapter(Context context,List<StuMsg.Result> list){
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
            view = inflater.inflate(R.layout.coach_stu_listview_item,
                    viewGroup, false);
            viewHolder.image = (ImageView) view.findViewById(R.id.image);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.state = (TextView) view.findViewById(R.id.state);
            viewHolder.countTime = (TextView) view.findViewById(R.id.countTime);
            viewHolder.phone = (TextView) view.findViewById(R.id.phone);
            viewHolder.time = (TextView) view.findViewById(R.id.time);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        stuMsg = list.get(i);
        String path = stuMsg.getFile();
        if(!path.endsWith(".jpg")&&!path.endsWith(".jpeg")&&!path.endsWith(".png")&&
                !path.endsWith(".GIF")&&!path.endsWith(".PNG") &&!path.endsWith(".JPG")&&!path.endsWith(".gif")){
            Glide.with(context).load(defaultImgUrl).into(viewHolder.image);
            viewHolder.image.setTag(defaultImgUrl);
        }else{
            Glide.with(context).load(path).placeholder(R.drawable.defaultimg).error(R.drawable.defaultimg).into(viewHolder.image);
        }
        viewHolder.name.setText(stuMsg.getName());
        viewHolder.name.setTag(stuMsg.getUid()+","+stuMsg.getFile()+","+stuMsg.getName());
        viewHolder.state.setText(stuMsg.getState());
        viewHolder.state.setTag(stuMsg.getState());
        viewHolder.countTime.setText(stuMsg.getCountTime()+"次");
        viewHolder.countTime.setTag(stuMsg.getCountTime()+"");
        if(stuMsg.getPhone()==null){
            viewHolder.phone.setText("暂未填写");
            viewHolder.phone.setTag("暂未填写");
        }else{
            viewHolder.phone.setText(stuMsg.getPhone());
            viewHolder.phone.setTag(stuMsg.getPhone());
        }

        viewHolder.time.setText(stuMsg.getTime());
        viewHolder.time.setTag(stuMsg.getTime());

        return view;
    }


    static class ViewHolder {
        public TextView name,state,countTime,phone,time;
        private ImageView image;
    }
}
