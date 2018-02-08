package com.example.dzj.mogemap.utils;

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

    //运动距离卡路里消耗公式 体重（kg）* 距离（km）* 运动系数（k）
    public static int getCalConsume(double kg, double km, double k){
        double cal = kg*km*k;
        return (int)cal;
    }
}
