package com.example.dzj.mogemap.utils;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dzj on 2018/3/1.
 */

public class OtherUtil {
    public static String getPace(double a) {
        String str = "";
        if(a == 0){
            return "--";
        }
        String[] a1 = new String(a+"").split("\\.");
        Log.i("otherutil",a+"");
        for (int i=0;i<a1.length;i++){
            Log.i("otherutil",a1[i]);
        }
        double b1 = Double.parseDouble("0."+a1[1]);
        double b2 = b1*60;
        String[] a2 = new String(b2+"").split("\\.");
        int c = Integer.parseInt(a2[0]);
        String d = "";
        if(c < 10){
            d = "0"+c;
        }else {
            d = c+"";
        }
        str = a1[0]+"'"+d+"\"";
        return str;
    }
    public static String getYearMonth(Date date) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        str = year+"年"+month+"月";
        return str;
    }
    public static String getMonthDay(Date date) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        str = month+"月"+day+"日";
        return str;
    }
    public static String getKM(double distance) {
        String str = "";
        DecimalFormat df = new DecimalFormat("######0.00");
        str = df.format(distance/1000);
        return str;
    }
    public static String getRunTimeString(int seconds) {
        String string = "";
        int mSecond = seconds%60;
        int myMinute = seconds/60;
        int mMinute, mHour;
        if(myMinute >= 60){
            mMinute = myMinute%60;
            mHour = myMinute/60;
        }else {
            mMinute = myMinute;
            mHour = 0;
        }
        String[] time = getTime(mHour, mMinute, mSecond);
        string = time[0]+":"+time[1]+":"+time[2];
        return string;
    }
    public static String[] getTime(int time, int minute, int second){
        String[] strs = new String[]{"", "", ""};
        if(second < 10){
            strs[2] = "0"+second;
        }else {
            strs[2] = ""+second;
        }
        if(minute < 10){
            strs[1] = "0"+minute;
        }else {
            strs[1] = ""+minute;
        }
        if(time < 10){
            strs[0] = "0"+time;
        }else {
            strs[0] = ""+time;
        }
        return strs;
    }
    public static String getRunType(int position){
        String type = "";
        if (position == 0){
            type = "户外跑";
        }else if (position == 1){
            type = "室内跑";
        }else if (position == 2){
            type = "步行";
        }else if (position == 3){
            type = "骑行";
        }
        return type;
    }
}
