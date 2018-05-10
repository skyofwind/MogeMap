package com.example.dzj.mogemap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.RankingActivity;
import com.example.dzj.mogemap.modle.MogeUserItem;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.OtherUtil;
import com.example.dzj.mogemap.view.RoundImageView;

import java.util.List;

/**
 * Created by dzj on 2018/3/5.
 */

public class RankingListViewAdapter extends BaseAdapter {
    private Context context;
    List<MogeUserItem> items;
    public RankingListViewAdapter(Context context, List<MogeUserItem> items){
        this.context = context;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.ranking_item, null);
            holder.count = (TextView)convertView.findViewById(R.id.count);
            holder.number = (TextView)convertView.findViewById(R.id.number);
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.icon = (RoundImageView)convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.number.setText((position+1)+"");
        holder.name.setText(items.get(position).getName());
        holder.count.setText(OtherUtil.getKM(items.get(position).getDistance())+" 公里");
        setHeadImage(holder.icon, items.get(position).getHead());
        return convertView;
    }
    static class ViewHolder{
        public RoundImageView icon;
        public TextView number, name, count;
    }
    private void setHeadImage(final RoundImageView view, final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(url != null){
                    final Bitmap bitmap = HttpUtil.getHttpBitmap(url);
                    ((RankingActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bitmap);
                        }
                    });

                }
            }
        }).start();
    }
}
