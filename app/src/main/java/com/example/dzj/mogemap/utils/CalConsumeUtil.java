package com.example.dzj.mogemap.utils;

import android.util.Log;

/**
 * Created by dzj on 2018/1/22.
 */

public class CalConsumeUtil {
    //运动指数k
    public final static double WALK = 0.51;//步行
    public final static double WALK_AWAY = 0.8214;//健走
    public final static double RUN = 1.036;//跑步
    public final static double BYCYCLE = 0.6142;//自行车
    public final static double PULLEY = 0.518;//滑轮、溜冰
    public final static double OUTDOOR_SKIING = 0.888;//室外滑雪

    public final static int DEFAULT_WEIGHT = 60;//默认体重60kg

    //运动距离卡路里消耗公式 体重（kg）* 距离（km）* 运动系数（k）
    public static int getCalConsume(double kg, double km, double k){
        double cal = kg*km*k;
        Log.i("CalConsumeUtil","kg="+kg+" km="+km+" k="+k+" cal="+cal);
        return (int)cal;
    }
    public static int getCalByType(int positon, double kg, double km){
        switch (positon){
            case 0:
                return getCalConsume(kg, km, RUN);
            case 1:
                return getCalConsume(kg, km, RUN);
            case 2:
                return getCalConsume(kg, km, WALK);
            case 3:
                return getCalConsume(kg, km, BYCYCLE);
        }
        return 0;
    }
}
