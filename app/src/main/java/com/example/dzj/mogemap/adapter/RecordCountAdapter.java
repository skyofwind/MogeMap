package com.example.dzj.mogemap.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.RunRecordActivity;
import com.example.dzj.mogemap.modle.Mogemap_run_record;
import com.example.dzj.mogemap.utils.OtherUtil;

import java.util.List;

/**
 * Created by dzj on 2018/3/1.
 */

public class RecordCountAdapter extends BaseAdapter{

    private final Context context;
    private List<Mogemap_run_record> records;


    public RecordCountAdapter(Context context, List<Mogemap_run_record> records){
        this.context = context;
        this.records = records;
    }
    @Override
    public int getCount() {
        return records.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.record_item, null);
            holder.top = (LinearLayout)convertView.findViewById(R.id.top);
            holder.yearMonth = (TextView)convertView.findViewById(R.id.year_month);
            holder.topDistance = (TextView)convertView.findViewById(R.id.top_distance);
            holder.slideToggle = (ImageView)convertView.findViewById(R.id.slide_toggle);
            holder.line = (View)convertView.findViewById(R.id.line);
            holder.detail = (LinearLayout)convertView.findViewById(R.id.detail);
            holder.bottomDistance = (TextView)convertView.findViewById(R.id.bottom_distance);
            holder.monthDay = (TextView)convertView.findViewById(R.id.month_day);
            holder.runTime = (TextView)convertView.findViewById(R.id.runTime);
            holder.pace = (TextView)convertView.findViewById(R.id.pace);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.yearMonth.setText(OtherUtil.getYearMonth(records.get(position).getDate()));
        holder.topDistance.setText(OtherUtil.getKM(records.get(position).getDistance()));
        holder.bottomDistance.setText(OtherUtil.getKM(records.get(position).getDistance()));
        holder.monthDay.setText(OtherUtil.getMonthDay(records.get(position).getDate()));
        holder.runTime.setText(OtherUtil.getRunTimeString(records.get(position).getRuntime()));
        if(records.get(position).getDistance() == 0){
            holder.pace.setText("--");
        }else {
            double pace = records.get(position).getRuntime()/60/records.get(position).getDistance()/1000;
            holder.pace.setText(OtherUtil.getPace(pace));
        }

        holder.top.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (holder.detail.getVisibility() == View.VISIBLE){
                    holder.slideToggle.setImageDrawable(context.getDrawable(R.drawable.arrow_up_gray));
                    holder.detail.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                }else {
                    holder.slideToggle.setImageDrawable(context.getDrawable(R.drawable.arrow_down_gray));
                    holder.detail.setVisibility(View.VISIBLE);
                    holder.line.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RunRecordActivity.class);
                intent.putExtra("id", records.get(position).getId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    static class ViewHolder{
        LinearLayout top;
        TextView yearMonth;
        TextView topDistance;
        ImageView slideToggle;
        View line;
        LinearLayout detail;
        TextView bottomDistance;
        TextView monthDay;
        TextView runTime;
        TextView pace;
    }

}
