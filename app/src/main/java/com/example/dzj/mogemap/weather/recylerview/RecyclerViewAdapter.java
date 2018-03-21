package com.example.dzj.theweather.recylerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dzj.theweather.R;
import com.example.dzj.theweather.voice.Voice;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.example.dzj.theweather.voice.Voice.Switchcontrol;
import static com.example.dzj.theweather.voice.Voice.bgmusic;
import static com.example.dzj.theweather.voice.Voice.control1;
import static com.example.dzj.theweather.voice.Voice.control2;
import static com.example.dzj.theweather.voice.Voice.control3;
import static com.example.dzj.theweather.voice.Voice.mp;
import static com.example.dzj.theweather.voice.Voice.multiple;
import static com.example.dzj.theweather.voice.Voice.play;
import static com.example.dzj.theweather.voice.Voice.setVoice;


/**
 * Created by dzj on 2016/11/7.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private List<String> mtime,mtmp,mwind,mtxt,city,ntmp,naqi,ntrav,nflu;
    private List<Integer> micon,mheight,bgpic,bg_min,color;
    private LayoutInflater mInflater;
    Context mcontext;
    RecyclerView.ViewHolder temp;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    @Override
    public void onClick(View v) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view , int tag);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public enum ITEM_TYPE {
        ITEM1,
        ITEM2
    }
    public RecyclerViewAdapter(Context context, List<String> time, List<String> tmp, List<Integer> icon, List<String> wind, List<String> txt, List<String> Ntmp, List<String> mcity, List<Integer> height, List<String> aqi, List<String> trav, List<String> flu, List<Integer> bg, List<Integer> min, List<Integer> tcolor)
    {
        mInflater = LayoutInflater.from(context);
        mtime = time;
        mtmp=tmp;
        micon=icon;
        mwind=wind;
        mtxt=txt;
        city=mcity;
        ntmp=Ntmp;
        mheight=height;
        naqi=aqi;
        ntrav=trav;
        nflu=flu;
        bgpic=bg;
        mcontext=context;
        bg_min=min;
        color=tcolor;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mInflater.inflate(R.layout.today,parent,false));
        }
        else{
            return new Item1ViewHolder(mInflater.inflate(R.layout.item_home,parent,false));
        }
    }
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        control1=false;control2=false;control3=false;
        final Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String Month=String.valueOf(mCalendar.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay=String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay=String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK));
        String mHour=String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));
        int Minuts=mCalendar.get(Calendar.MINUTE);
        String wee=getWeek(mWay);
        if(holder instanceof Item1ViewHolder){
            ((Item1ViewHolder)holder).time.setText(mtime.get(position));
            ((Item1ViewHolder)holder).weather_text.setText(mtxt.get(position));
            ((Item1ViewHolder)holder).tmp.setText(mtmp.get(position));
            ((Item1ViewHolder)holder).wind.setText(mwind.get(position));
            ((Item1ViewHolder)holder).icon.setImageResource(micon.get(position));
            ((Item1ViewHolder)holder).ly.setBackgroundResource(bg_min.get(position));
        }else if(holder instanceof Item2ViewHolder){
            if(multiple==true&&play==true){
                if(mp!=null){
                    Voice.stopMusic();
                }
            }
            temp=holder;
            String time2;
            if(Minuts<10){
                time2="现在 "+mHour+":0"+Minuts;
            }else{
                time2="现在 "+mHour+":"+Minuts;
            }
            ViewGroup.LayoutParams lp = ((Item2ViewHolder) holder).ll.getLayoutParams();
            lp.height=mheight.get(position);
            ((Item2ViewHolder) holder).ll.setLayoutParams(lp);
            //背景更改
            ((Item2ViewHolder) holder).ll.setBackgroundResource(bgpic.get(position));
            ((Item2ViewHolder)holder).trav.setText(ntrav.get(position));
            ((Item2ViewHolder)holder).flu.setText(nflu.get(position));
            ((Item2ViewHolder)holder).date.setText(Month+"月"+mDay+"日 "+wee);
            ((Item2ViewHolder)holder).time.setText(time2);
            ((Item2ViewHolder)holder).weather_text.setText(mtxt.get(position));
            ((Item2ViewHolder)holder).tmp.setText(mtmp.get(position));
            ((Item2ViewHolder)holder).wind.setText(mwind.get(position));
            ((Item2ViewHolder)holder).icon.setImageResource(micon.get(position));
            ((Item2ViewHolder)holder).city.setText(city.get(position));
            ((Item2ViewHolder)holder).city.setOnClickListener(this);
            //((Item2ViewHolder)holder).select.setOnClickListener(this);
            ((Item2ViewHolder)holder).city.setTag(0);
            //((Item2ViewHolder)holder).select.setTag(0);
            ((Item2ViewHolder)holder).ntmp.setText(ntmp.get(position));
            ((Item2ViewHolder)holder).aqi.setText(naqi.get(position));
            //判断是否显示声音按钮
            if(Switchcontrol()){
                setVoice(mtxt.get(position));
                ((Item2ViewHolder)holder).voice.setVisibility(View.VISIBLE);
                if(control1){
                    Voice.startMusic(R.raw.thunder_rain,mcontext);
                }else if(control2){
                    Voice.startMusic(R.raw.rain,mcontext);
                }else if(control3){
                    Voice.startMusic(R.raw.rainstorm,mcontext);
                }else{
                    ((Item2ViewHolder)holder).voice.setVisibility(View.GONE);
                }
                ((Item2ViewHolder)holder).voice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(play){
                            Voice.stopMusic();
                            ((Item2ViewHolder)temp).voice.setImageResource(R.drawable.voice_pause);
                        }else if(!play){
                            Voice.startMusic(bgmusic,mcontext);
                            ((Item2ViewHolder)temp).voice.setImageResource(R.drawable.voice_play);
                        }
                    }
                });
            }else{
                if(play){
                    Voice.stopMusic();
                }
                ((Item2ViewHolder)holder).voice.setVisibility(View.GONE);
            }

            //更改颜色
            ((Item2ViewHolder)holder).city.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).time.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).ntmp.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).weather_text.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).tmp.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).wind.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).date.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).aqi.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).trav.setTextColor(color.get(position));
            ((Item2ViewHolder)holder).flu.setTextColor(color.get(position));
        }

    }
    public int getItemViewType(int positon){
        if(positon==0){
            return ITEM_TYPE.ITEM2.ordinal();
        }else{
            return ITEM_TYPE.ITEM1.ordinal();
        }
    }
    public int getItemCount(){
        return mtime.size();
    }
    //future message
    public static class Item1ViewHolder extends RecyclerView.ViewHolder{
        TextView time,weather_text,tmp,wind;
        ImageView icon;
        LinearLayout ly;
        public Item1ViewHolder(View itemView){
            super(itemView);
            time=(TextView)itemView.findViewById(R.id.time);
            weather_text=(TextView) itemView.findViewById(R.id.weather_text);
            tmp=(TextView) itemView.findViewById(R.id.tmp);
            wind=(TextView)itemView.findViewById(R.id.wind);
            icon=(ImageView)itemView.findViewById(R.id.icon);
            ly=(LinearLayout)itemView.findViewById(R.id.bg_min);
        }
    }
    //today message
    public static class Item2ViewHolder extends RecyclerView.ViewHolder{
        TextView city,time,ntmp,weather_text,tmp,wind,date,aqi,trav,flu;
        ImageView icon,voice;
        RelativeLayout ll;
        //ImageView select;
        public Item2ViewHolder(View itemView){
            super(itemView);
            trav=(TextView)itemView.findViewById(R.id.trav);
            flu=(TextView)itemView.findViewById(R.id.flu);
            aqi=(TextView)itemView.findViewById(R.id.aqi);
            date=(TextView)itemView.findViewById(R.id.date);
            city=(TextView)itemView.findViewById(R.id.city);
            time=(TextView)itemView.findViewById(R.id.time);
            ntmp=(TextView)itemView.findViewById(R.id.ntmp);
            weather_text=(TextView)itemView.findViewById(R.id.weather_text);
            tmp=(TextView)itemView.findViewById(R.id.tmp);
            wind=(TextView)itemView.findViewById(R.id.wind);
            icon=(ImageView)itemView.findViewById(R.id.icon);
            ll=(RelativeLayout)itemView.findViewById(R.id.ll);
            voice=(ImageView)itemView.findViewById(R.id.voice);
            //select=(ImageView)itemView.findViewById(R.id.city_select);
        }
    }
    public String getWeek(String mWay){
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return "星期"+mWay;
    }
    public LayoutInflater getLayoutInflater(){
        return this.mInflater;
    }
}

