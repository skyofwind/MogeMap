package com.example.dzj.theweather.main;

import android.content.Context;
import android.os.Environment;

import com.example.dzj.theweather.R;
import com.example.dzj.theweather.main_menu.DB_code;
import com.google.gson.Gson;
import com.example.dzj.theweather.json_analysis.Aqi;
import com.example.dzj.theweather.json_analysis.City;
import com.example.dzj.theweather.json_analysis.Cond;
import com.example.dzj.theweather.json_analysis.DailyForecast;
import com.example.dzj.theweather.json_analysis.Flu;
import com.example.dzj.theweather.json_analysis.HEWeather5;
import com.example.dzj.theweather.json_analysis.Now;
import com.example.dzj.theweather.json_analysis.Suggestion;
import com.example.dzj.theweather.json_analysis.Tmp;
import com.example.dzj.theweather.json_analysis.Trav;
import com.example.dzj.theweather.json_analysis.Wind;
import com.example.dzj.theweather.json_analysis.tCond;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dzj on 2016/11/24.
 */

public class DataDeal {

    public static final String PACKAGE_NAME2 = "com.example.dzj.theweather";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME2;  //在手机里存放数据库的位置
    //RecyclerView传入参数
    public static List<String> mtime; //日期
    public static List<String> mtmp; //温度
    public static List<String> mwind;//风力
    public static List<String> mtxt;//天气状况
    public static List<String> ncity;//城市
    public static List<String> ntmp;//温度
    public static List<String> naqi;//空气质量
    public static List<String> ntrav;//旅游建议
    public static List<String> nflu;//流感指数
    public static List<Integer> micon,height,bgpic,bg_min,tcolor;//天气图标 及背景颜色及背景图片 height市获取屏幕运用高度
    public static StringBuffer sb = new StringBuffer(256);
    public static boolean welcome=false;
    public static String citycode="",temp="";
    public static String rjson="";
    public static final String url="https://way.jd.com/he/freeweather?city=";
    public static final String url2="https://way.jd.com/he/freecity?city=";
    public static final String password="&appkey=3d1cdd0bcc9a795377b1c3403320c4e6";
    public static boolean mvoice=true;
    public static boolean wc1=false,wc2=false;
    //初始化RecyclerView中传递的数组初始化
    public static void initData(){
        mtime= new ArrayList<>();//日期数组
        mtmp= new ArrayList<>();//日温数组
        micon=new ArrayList<>();//图片id数组
        mwind= new ArrayList<>();//风力数组
        mtxt= new ArrayList<>();//天气信息
        ncity= new ArrayList<>();
        ntmp= new ArrayList<>();
        height=new ArrayList<>();
        naqi= new ArrayList<>();
        ntrav= new ArrayList<>();
        nflu= new ArrayList<>();
        bgpic=new ArrayList<>();
        bg_min=new ArrayList<>();
        tcolor=new ArrayList<>();
        height.add(1500);
    }
    //json数据处理和数组赋值
    public static void Json_deal(String json, Gson mg, String temp, int Hour){
        HEWeather5 hw=mg.fromJson(json,HEWeather5.class);

            if(hw.getSuggestion()!=null){
                Suggestion suggestion=hw.getSuggestion();
                Trav trav=suggestion.getTrav();
                Flu flu=suggestion.getFlu();
                ntrav.add(trav.getTxt());
                nflu.add(flu.getTxt());
                System.out.println(trav.getTxt()+flu.getTxt());
            }else{
                nflu.add("旅游建议暂无更新");
                ntrav.add("流感建议暂无更新");
            }

            if(hw.getAqi()!=null){
                Aqi aqi=hw.getAqi();
                City cy=aqi.getCity();
                naqi.add("空气质量 "+cy.getQlty()+" "+cy.getAqi());
            }else{
                naqi.add("空气质量暂无更新");
            }
            if(hw.getBasic()!=null){
                ncity.add(temp);
            }else{
                ncity.add(temp);
            }
            Now now=hw.getNow();
            tCond tc=now.getCond();
            ntmp.add(now.getTmp()+"°");
            List<DailyForecast> list=hw.getForecast();
            if(list!=null){
                int size= list.size();
                if(size>0){
                    for(int i=0;i<size;i++){
                        mtime.add(list.get(i).getDate());
                        Cond cn=list.get(i).getCond();

                        if(i==0){
                            if(Hour>=18||Hour<=6){
                                setBgimg_night(tc.getTxt());
                                getImgByWeather_night(tc.getTxt());
                                tcolor.add(0xffffffff);
                            }else{
                                getImgByWeather_day(tc.getTxt());
                                setBgimg_day(tc.getTxt());
                                setTxtColor(tc.getTxt());
                            }
                            mtxt.add(tc.getTxt());
                            setBgimg_min(tc.getTxt());
                        }else{
                            getImgByWeather_day(cn.getTxt_d());
                            setBgimg_min(cn.getTxt_d());
                            if(cn.getTxt_d().equals(cn.getTxt_n())){
                                mtxt.add(cn.getTxt_d());
                            }else{
                                mtxt.add(cn.getTxt_d()+"转"+cn.getTxt_n());
                            }
                        }
                        Tmp tmp=list.get(i).getTmp();
                        mtmp.add(tmp.getMin()+"~"+tmp.getMax()+"°C");
                        Wind wind=list.get(i).getWind();
                        if(wind.getDir().equals("无持续风向")){
                            mwind.add(wind.getSc());
                        }else{
                            if(wind.getSc().equals("微风")){
                                mwind.add(wind.getDir()+wind.getSc());
                            }else{
                                mwind.add(wind.getDir()+wind.getSc()+"级");
                            }

                        }
                    }

                }

        }
    }
    //判断天气对应图标
    public static void getImgByWeather_day(String weather){

        if("晴".equals(weather)){
            micon.add(R.drawable.icon_day_fine);
        }else if("多云".equals(weather)){
            micon.add(R.drawable.icon_day_cloudy);
        }else if("晴间多云".equals(weather)){
            micon.add(R.drawable.icon_day_sun_cloud);
        }
        else if("阴".equals(weather)){
            micon.add(R.drawable.icon_day_overcast);
        }
        else if("阵雨".equals(weather)){
            micon.add(R.drawable.icon_day_shower);
        }
        else if("雷阵雨".equals(weather)){
            micon.add(R.drawable.icon_day_thunder_shower);
        }
        else if("雷阵雨伴有冰雹".equals(weather)){
            micon.add(R.drawable.icon_day_thunder_shower_hail);
        }
        else if("雨夹雪".equals(weather)){
            micon.add(R.drawable.icon_day_rain_snow);
        }
        else if("小雨".equals(weather)){
            micon.add(R.drawable.icon_day_light_rain);
        }
        else if("中雨".equals(weather)){
            micon.add(R.drawable.icon_day_moderate_rain);
        }
        else if("大雨".equals(weather)){
            micon.add(R.drawable.icon_day_heavy_rain);
        }
        else if("暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_rainstorm);
        }
        else if("大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }
        else if("特大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }
        else if("阵雪".equals(weather)){
            micon.add(R.drawable.icon_day_snow_shower);
        }
        else if("小雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("中雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("大雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("暴雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("雾".equals(weather)){
            micon.add(R.drawable.icon_day_cfog_);
        }
        else if("冻雨".equals(weather)){
            micon.add(R.drawable.icon_freezing_rain);
        }
        else if("沙尘暴".equals(weather)){
            micon.add(R.drawable.icon_day_dstorms);
        }
        else if("小到中雨".equals(weather)){
            micon.add(R.drawable.icon_day_moderate_rain);
        }
        else if("中到大雨".equals(weather)){
            micon.add(R.drawable.icon_day_heavy_rain);
        }
        else if("大到暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_heavy_rain);
        }
        else if("暴雨到大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }else if("大暴雨到特大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }
        else if("小到中雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("中到大雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("大到暴雪".equals(weather)){
            micon.add(R.drawable.icon_day_light_snow);
        }
        else if("浮尘".equals(weather)){
            micon.add(R.drawable.icon_dust);
        }
        else if("扬沙".equals(weather)){
            micon.add(R.drawable.icon_dust);
        }
        else if("强沙尘暴".equals(weather)){
            micon.add(R.drawable.icon_day_dstorms);
        }
        else if("霾".equals(weather)){
            micon.add(R.drawable.icon_day_cfog_);
        }else{
            micon.add(R.drawable.undefined);
        }

    }
    public static void getImgByWeather_night(String weather){
        if("晴".equals(weather)){
            micon.add(R.drawable.icon_night_fine);
        }else if("多云".equals(weather)){
            micon.add(R.drawable.icon_night_cloudy);
        }else if("晴间多云".equals(weather)){
            micon.add(R.drawable.icon_night_cloudy);
        }
        else if("阴".equals(weather)){
            micon.add(R.drawable.icon_day_overcast);
        }
        else if("阵雨".equals(weather)){
            micon.add(R.drawable.icon_night_shower);
        }
        else if("雷阵雨".equals(weather)){
            micon.add(R.drawable.icon_night_thunder_shower);
        }
        else if("雷阵雨伴有冰雹".equals(weather)){
            micon.add(R.drawable.icon_night_thunder_shower);
        }
        else if("雨夹雪".equals(weather)){
            micon.add(R.drawable.icon_night_rain_snow);
        }
        else if("小雨".equals(weather)){
            micon.add(R.drawable.icon_day_light_rain);
        }
        else if("中雨".equals(weather)){
            micon.add(R.drawable.icon_day_moderate_rain);
        }
        else if("大雨".equals(weather)){
            micon.add(R.drawable.icon_day_heavy_rain);
        }
        else if("暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_rainstorm);
        }
        else if("大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }
        else if("特大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }
        else if("阵雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("小雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("中雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("大雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("暴雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("雾".equals(weather)){
            micon.add(R.drawable.icon_night_fog);
        }
        else if("冻雨".equals(weather)){
            micon.add(R.drawable.icon_night_freezing_rain);
        }
        else if("沙尘暴".equals(weather)){
            micon.add(R.drawable.icon_day_dstorms);
        }
        else if("小到中雨".equals(weather)){
            micon.add(R.drawable.icon_day_moderate_rain);
        }
        else if("中到大雨".equals(weather)){
            micon.add(R.drawable.icon_day_heavy_rain);
        }
        else if("大到暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_heavy_rain);
        }
        else if("暴雨到大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }else if("大暴雨到特大暴雨".equals(weather)){
            micon.add(R.drawable.icon_day_big_rainstorm);
        }
        else if("小到中雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("中到大雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("大到暴雪".equals(weather)){
            micon.add(R.drawable.icon_night_snow);
        }
        else if("浮尘".equals(weather)){
            micon.add(R.drawable.icon_dust);
        }
        else if("扬沙".equals(weather)){
            micon.add(R.drawable.icon_dust);
        }
        else if("强沙尘暴".equals(weather)){
            micon.add(R.drawable.icon_day_dstorms);
        }
        else if("霾".equals(weather)){
            micon.add(R.drawable.icon_night_fog);
        }else{
            micon.add(R.drawable.undefined);
        }

    }
    //判断天气背景图片
    public static void setBgimg_day(String weather){
        if(weather.equals("晴")){
            bgpic.add(R.drawable.bg_sunny_day);
        }else if(weather.equals("多云")){
            bgpic.add(R.drawable.bg_cloudy_day);
        }else if(weather.equals("晴间多云")){
            bgpic.add(R.drawable.bg_cloud_day);
        }else if(weather.equals("雷阵雨")||weather.equals("雷阵雨伴有冰雹")||weather.equals("雨夹雪")||weather.equals("暴雨")||weather.equals("大暴雨")||weather.equals("特大暴雨")||weather.equals("暴雨到大暴雨")){
            bgpic.add(R.drawable.bg_thunderstorm_day);
        }else if(weather.equals("阵雨")||weather.equals("小雨")||weather.equals("中雨")||weather.equals("大雨")||weather.equals("小到中雨")||weather.equals("中到大雨")||weather.equals("大到暴雨")){
            bgpic.add(R.drawable.bg_rain_day);
        }else if(weather.equals("阵雪")||weather.equals("小雪")||weather.equals("中雪")||weather.equals("大雪")||weather.equals("暴雪")||weather.equals("小到中雪")||weather.equals("中到大雪")||weather.equals("大到暴雪")){
            bgpic.add(R.drawable.bg_snow_day);
        }else if(weather.equals("雾")){
            bgpic.add(R.drawable.bg_fog_day);
        }else if(weather.equals("霾")){
            bgpic.add(R.drawable.bg_haze_day);
        }else if(weather.equals("沙尘暴")||weather.equals("浮尘")||weather.equals("扬沙")||weather.equals("强沙尘暴")){
            bgpic.add(R.drawable.bg_dust_day);
        }else if(weather.equals("阴")){
            bgpic.add(R.drawable.bg_osck_day);
        }else if(weather.equals("冻雨")){
            bgpic.add(R.drawable.bg_ice_rain);
        }else{
            bgpic.add(R.drawable.bg_sunny_day);
        }
    }
    public static void setBgimg_night(String weather){
        if(weather.equals("晴")){
            bgpic.add(R.drawable.bg_sunny_night);
        }else if(weather.equals("多云")){
            bgpic.add(R.drawable.bg_cloudy_night);
        }else if(weather.equals("晴间多云")){
            bgpic.add(R.drawable.bg_cloud_night);
        }else if(weather.equals("雷阵雨")||weather.equals("雷阵雨伴有冰雹")||weather.equals("雨夹雪")||weather.equals("暴雨")||weather.equals("大暴雨")||weather.equals("特大暴雨")||weather.equals("暴雨到大暴雨")){
            bgpic.add(R.drawable.bg_thunderstorm_night);
        }else if(weather.equals("阵雨")||weather.equals("小雨")||weather.equals("中雨")||weather.equals("大雨")||weather.equals("小到中雨")||weather.equals("中到大雨")||weather.equals("大到暴雨")){
            bgpic.add(R.drawable.bg_rain_night);
        }else if(weather.equals("阵雪")||weather.equals("小雪")||weather.equals("中雪")||weather.equals("大雪")||weather.equals("暴雪")||weather.equals("小到中雪")||weather.equals("中到大雪")||weather.equals("大到暴雪")){
            bgpic.add(R.drawable.bg_snow_night);
        }else if(weather.equals("雾")){
            bgpic.add(R.drawable.bg_fog_night);
        }else if(weather.equals("霾")){
            bgpic.add(R.drawable.bg_haze_night);
        }else if(weather.equals("沙尘暴")||weather.equals("浮尘")||weather.equals("扬沙")||weather.equals("强沙尘暴")){
            bgpic.add(R.drawable.bg_dust_night);
        }else if(weather.equals("阴")){
            bgpic.add(R.drawable.bg_osck_night);
        }else if(weather.equals("冻雨")){
            bgpic.add(R.drawable.bg_ice_rain);
        }else {
            bgpic.add(R.drawable.bg_sunny_night);
        }
    }
    public static void setBgimg_min(String weather){
        if(weather.equals("晴")){
            bg_min.add(R.drawable.bg_sunny_min);
        }else if(weather.equals("多云")){
            bg_min.add(R.drawable.bg_cloudy_min);
        }else if(weather.equals("晴间多云")){
            bg_min.add(R.drawable.bg_cloudy_min);
        }else if(weather.equals("雷阵雨")||weather.equals("雷阵雨伴有冰雹")||weather.equals("雨夹雪")||weather.equals("暴雨")||weather.equals("大暴雨")||weather.equals("特大暴雨")||weather.equals("暴雨到大暴雨")){
            bg_min.add(R.drawable.bg_thunderstorm_min);
        }else if(weather.equals("阵雨")||weather.equals("小雨")||weather.equals("中雨")||weather.equals("大雨")||weather.equals("小到中雨")||weather.equals("中到大雨")||weather.equals("大到暴雨")){
            bg_min.add(R.drawable.bg_rain_min);
        }else if(weather.equals("阵雪")||weather.equals("小雪")||weather.equals("中雪")||weather.equals("大雪")||weather.equals("暴雪")||weather.equals("小到中雪")||weather.equals("中到大雪")||weather.equals("大到暴雪")){
            bg_min.add(R.drawable.bg_snow_min);
        }else if(weather.equals("雾")){
            bg_min.add(R.drawable.bg_fog_min);
        }else if(weather.equals("霾")){
            bg_min.add(R.drawable.bg_haze_min);
        }else if(weather.equals("沙尘暴")||weather.equals("浮尘")||weather.equals("扬沙")||weather.equals("强沙尘暴")){
            bg_min.add(R.drawable.bg_dust_min);
        }else if(weather.equals("阴")){
            bg_min.add(R.drawable.bg_osck_min);
        }else if(weather.equals("冻雨")){
            bg_min.add(R.drawable.bg_rain_min);
        }else {
            bg_min.add(R.drawable.bg_sunny_min);
        }
    }
    public static void setTxtColor(String str){
        if(str.equals("晴")){
            tcolor.add(0xff1e5f9f);
        }else if(str.equals("多云")){
            tcolor.add(0xffffffff);
        }else if(str.equals("晴间多云")){
            tcolor.add(0xffffffff);
        }else if(str.equals("雷阵雨")||str.equals("雷阵雨伴有冰雹")||str.equals("雨夹雪")||str.equals("暴雨")||str.equals("大暴雨")||str.equals("特大暴雨")||str.equals("暴雨到大暴雨")){
            tcolor.add(0xffffffff);
        }else if(str.equals("阵雨")||str.equals("小雨")||str.equals("中雨")||str.equals("大雨")||str.equals("小到中雨")||str.equals("中到大雨")||str.equals("大到暴雨")){
            tcolor.add(0xffffffff);
        }else if(str.equals("阵雪")||str.equals("小雪")||str.equals("中雪")||str.equals("大雪")||str.equals("暴雪")||str.equals("小到中雪")||str.equals("中到大雪")||str.equals("大到暴雪")){
            tcolor.add(0xffff5908);
        }else if(str.equals("雾")){
            tcolor.add(0xffffffff);
        }else if(str.equals("霾")){
            tcolor.add(0xffffffff);
        }else if(str.equals("沙尘暴")||str.equals("浮尘")||str.equals("扬沙")||str.equals("强沙尘暴")){
            tcolor.add(0xffffffff);
        }else if(str.equals("阴")){
            tcolor.add(0xffffffff);
        }else if(str.equals("冻雨")){
            tcolor.add(0xffffffff);
        }else {
            tcolor.add(0xff1e5f9f);
        }
    }
    public static String data_Province(String str){
        //省名处理
        int length=str.length();
        if(str.substring(length-1,length).equals("市")||str.substring(length-1,length).equals("省")){
            return str.substring(0,length-1);
        }else if(str.equals("内蒙古自治区")){
            return "内蒙古";
        }else if(str.equals("广西壮族自治区")){
            return "广西";
        }else if(str.equals("西藏自治区")){
            return "西藏";
        }else if(str.equals("宁夏回族自治区")){
            return "宁夏";
        }else if(str.equals("广西壮族自治区")){
            return "广西";
        }else if(str.equals("新疆维吾尔自治区")){
            return "新疆";
        }else if(str.equals("香港特別行政區")){
            return "香港";
        }else if(str.equals("澳門特別行政區")){
            return "澳门";
        }
        return str;
    }
    public static String data_City(String str){
        //二级市名处理
        int length=str.length();
        if(str.substring(length-1,length).equals("市")){
            return (str.substring(0,length-1));
        }else if(str.substring(length-1,length).equals("盟")){
            return (str.substring(0,length-1));
        }else if(str.substring(length-2,length).equals("地区")||str.substring(length-2,length).equals("林区")){
            return (str.substring(0,length-2));
        }else if(length>4){
            if(str.substring(length-4,length).equals("市市辖区")){
                return (str.substring(0,length-4));
            }
        }else if(length>3){
            if(str.substring(length-3,length).equals("自治州")){
                if(length>8){
                    if(str.equals("克孜勒苏柯尔克孜自治州")){
                        return "克州";
                    }else
                    if(str.substring(length-8,length ).equals("布依族苗族自治州")||str.substring(length-8,length ).equals("哈尼族彝族自治州")||str.substring(length-8,length ).equals("土家族苗族自治州")||str.substring(length-8,length ).equals("蒙古族藏族自治州")||str.substring(length-8,length ).equals("傣族景颇族自治州")){
                        return (str.substring(0,length-8));
                    }
                }else if(length>7){
                    if(str.substring(0,length-7).equals("苗族侗族自治州")||str.substring(0,length-7).equals("藏族羌族自治州")||str.substring(0,length-7).equals("壮族苗族自治州")){
                        return (str.substring(0,length-7));
                    }
                }else if(length>6){
                    if(str.substring(length-6,length).equals("朝鲜族自治州")||str.substring(length-6,length).equals("傈僳族自治州")||str.substring(length-6,length).equals("哈萨克自治州")){
                        return (str.substring(0,length-6));
                    }
                }else if(length>5){
                    if(str.substring(length-5,length).equals("藏族自治州")||str.substring(length-5,length).equals("彝族自治州")||str.substring(length-5,length).equals("白族自治州")||str.substring(length-5,length).equals("回族自治州")||str.substring(length-5,length).equals("蒙古自治州")||str.substring(length-5,length).equals("傣族自治州")){
                        return (str.substring(0,length-5));
                    }
                }
            }else if(str.substring(length-3,length).equals("自治县")){
                if(length>7){
                    if(str.substring(length-7,length).equals("黎族苗族自治县")){
                        return (str.substring(0,length-7));
                    }
                }else if(length>5){
                    if(str.substring(length-5,length).equals("黎族自治县")){
                        return (str.substring(0,length-5));
                    }
                }
            }
        }
        return str;
    }
    public static String data_District(String str){
        //三级县市处理
        int length=str.length();
        if(length<=2){
            return  str;
        }else if(str.substring(length-1,length).equals("县")){
            if(str.equals("通化县")||str.equals("本溪县")||str.equals("辽阳县")||str.equals("建平县")||str.equals("承德县")||str.equals("大同县")||str.equals("五台县")||str.equals("伊宁县")||str.equals("芜湖县")||str.equals("南昌县")||str.equals("上饶县")||str.equals("吉安县")||str.equals("邵阳县")||str.equals("遵义县")||str.equals("宜宾县")){
                return (str);
            }else{
                return (str.substring(0,length-1));
            }
        }else if(str.substring(length-1,length).equals("市")){
            return (str.substring(0,length-1));
        }else if(str.substring(length-2,length).equals("地区")||str.substring(length-2,length).equals("林区")||str.substring(length-2,length).equals("矿区")){
            return (str.substring(0,length-2));
        }else if(str.substring(length-1,length).equals("区")){
            if(str.equals("呼市郊区")||str.equals("尖草坪区")||str.equals("小店区")||str.equals("淮阴区")||str.equals("淮安区")||str.equals("黄山区")||str.equals("黄山风景区")||str.equals("赫山区")){
                return (str);
            }else{
                return (str.substring(0,length-1));
            }
        }else{
            if(length>3){
                if(str.substring(length-3,length).equals("自治县")){
                    if(length>13){
                        if(str.substring(length-13,length).equals("拉祜族佤族布朗族傣族自治县")){
                            return (str.substring(0,length-13));
                        }
                    }else if(length>12){
                        if(str.substring(length-12,length).equals("保安族东乡族撒拉族自治县")){
                            return (str.substring(0,length-12));
                        }
                    }else if(length>11){
                        if(str.substring(length-11,length).equals("彝族哈尼族拉祜族自治县")){
                            return (str.substring(0,length-11));
                        }
                    }else if(length>10){
                        if(str.equals("喀喇沁左翼蒙古族自治县")){
                            return("喀左");
                        }else
                        if(str.substring(length-10,length).equals("傣族拉祜族佤族自治县")||str.substring(length-10,length).equals("哈尼族彝族傣族自治县")){
                            return (str.substring(0,length-10));
                        }
                    }else if(length>9){
                        if(str.substring(length-9,length).equals("俄罗斯蒙古族自治县")
                                ||str.substring(length-9,length).equals("苗族瑶族傣族自治县")
                                ||str.substring(length-9,length).equals("彝族回族苗族自治县")){
                            return (str.substring(0,length-9));
                        }
                    }else if(length>8){
                        if(str.substring(length-8,length).equals("仡佬族苗族自治县")
                                ||str.substring(length-8,length).equals("土家族苗族自治县")
                                ||str.substring(length-8,length).equals("塞哈萨克族自治县")
                                ||str.substring(length-8,length).equals("苗族土家族自治县")
                                ||str.substring(length-8,length).equals("苗族布依族自治县")
                                ||str.substring(length-8,length).equals("满族蒙古族自治县")
                                ||str.substring(length-8,length).equals("哈尼族彝族自治县")
                                ||str.substring(length-8,length).equals("独龙族怒族自治县")
                                ||str.substring(length-8,length).equals("布依族苗族自治县")
                                ||str.substring(length-8,length).equals("白族普米族自治县")){
                            return (str.substring(0,length-8));
                        }
                    }else if(length>7){
                        if(str.substring(length-7,length).equals("壮族瑶族自治县")
                                ||str.substring(length-7,length).equals("彝族苗族自治县")
                                ||str.substring(length-7,length).equals("彝族回族自治县")
                                ||str.substring(length-7,length).equals("彝族傣族自治县")
                                ||str.substring(length-7,length).equals("苗族侗族自治县")
                                ||str.substring(length-7,length).equals("回族彝族自治县")
                                ||str.substring(length-7,length).equals("回族土族自治县")
                                ||str.substring(length-7,length).equals("傣族彝族自治县")
                                ||str.substring(length-7,length).equals("傣族佤族自治县")
                                ||str.substring(length-7,length).equals("黎族苗族自治县")){
                            return (str.substring(0,length-7));
                        }
                    }else if(length>6){
                        if(str.substring(length-6,length).equals("裕固族自治县")
                                ||str.substring(length-6,length).equals("土家族自治县")
                                ||str.substring(length-6,length).equals("塔吉克自治县")
                                ||str.substring(length-6,length).equals("撒拉族自治县")
                                ||str.substring(length-6,length).equals("纳西族自治县")
                                ||str.substring(length-6,length).equals("仫佬族自治县")
                                ||str.substring(length-6,length).equals("蒙古族自治县")
                                ||str.substring(length-6,length).equals("毛南族自治县")
                                ||str.substring(length-6,length).equals("傈僳族自治县")
                                ||str.substring(length-6,length).equals("拉祜族自治县")
                                ||str.substring(length-6,length).equals("哈萨克自治县")
                                ||str.substring(length-6,length).equals("哈尼族自治县")
                                ||str.substring(length-6,length).equals("朝鲜族自治县")){
                            return (str.substring(0,length-6));
                        }
                    }else if(length>5){
                        if(str.equals("东乡族自治县")){
                            return ("东乡");
                        }
                        if(str.substring(length-5,length).equals("彝族自治县")
                                ||str.substring(length-5,length).equals("瑶族自治县")
                                ||str.substring(length-5,length).equals("锡伯自治县")
                                ||str.substring(length-5,length).equals("佤族自治县")
                                ||str.substring(length-5,length).equals("土族自治县")
                                ||str.substring(length-5,length).equals("水族自治县")
                                ||str.substring(length-5,length).equals("畲族自治县")
                                ||str.substring(length-5,length).equals("苗族自治县")
                                ||str.substring(length-5,length).equals("满族自治县")
                                ||str.substring(length-5,length).equals("回族自治县")
                                ||str.substring(length-5,length).equals("各族自治县")
                                ||str.substring(length-5,length).equals("侗族自治县")
                                ||str.substring(length-5,length).equals("黎族自治县")){
                            return (str.substring(0,length-5));
                        }
                    }
                }
            }else if(str.substring(length-1,length).equals("旗")){
                if(str.equals("巴林左旗")||str.equals("巴林右旗")||str.equals("杭锦旗")||str.equals("乌审旗")||str.equals("阿荣旗")||str.equals("杭锦后旗")||str.equals("四子王旗")||str.equals("镶黄旗")||str.equals("正镶白旗")||str.equals("正蓝旗")){
                    return (str);
                }else if(str.equals("克什克腾旗")||str.equals("翁牛特旗")||str.equals("喀喇沁旗")||str.equals("敖汉旗")||str.equals("库伦旗")||str.equals("奈曼旗")||str.equals("扎鲁特旗")||str.equals("达拉特旗")||str.equals("准格尔旗")||str.equals("鄂托克旗")||str.equals("伊金霍洛旗")||str.equals("扎赉特旗")||str.equals("阿巴嘎旗")||str.equals("太仆寺旗")||str.equals("额济纳旗")){
                    return (str.substring(0,length-1));
                }else if(str.substring(0,3).equals("土默特")){
                    return (str.substring(0,1)+str.substring(length-2,length));
                }else if(str.equals("达尔罕茂明安联合旗")){
                    return ("达茂旗");
                } else if(str.equals("阿鲁科尔沁旗")){
                    return ("阿鲁旗");
                }else if(str.equals("科尔沁左翼中旗")){
                    return ("科左中旗");
                }else if(str.equals("科尔沁左翼后旗")){
                    return ("科左后旗");
                }else if(str.equals("鄂托克前旗")){
                    return ("鄂前旗");
                }else if(str.equals("莫力达瓦达斡尔族自治旗")){
                    return ("莫力达瓦");
                }else if(str.equals("鄂伦春自治旗")){
                    return ("鄂伦春旗");
                }else if(str.equals("鄂温克族自治旗")){
                    return ("鄂温克旗");
                }else if(str.equals("陈巴尔虎旗")){
                    return ("陈旗");
                }else if(length>4){
                    if(str.substring(0,4).equals("新巴尔虎")||str.substring(0,3).equals("乌拉特")||str.substring(0,3).equals("苏尼特")){
                        return (str.substring(0,1)+str.substring(length-2,length));
                    }else if(str.substring(0,3).equals("察哈尔")||str.substring(0,3).equals("科尔沁")){
                        return (str.substring(0,1)+str.substring(length-4,length-3)+str.substring(length-2,length));
                    }else if(str.substring(length-4,length).equals("珠穆沁旗")){
                        return (str.substring(0,2)+"旗");
                    }else if(str.equals("阿拉善左旗")){
                        return ("阿左旗");
                    }
                }
            }
        }
        return str;
    }
    //匹配城市名的城市代码
    public static String cnCode(Context contexts,LocationInfo info){
        String code="",province,district;
        WeatherActivity.dbcode=new DB_code(contexts);
        WeatherActivity.dbcode.openDatabase();
        WeatherActivity.cursor= WeatherActivity.dbcode.Query();
        if(WeatherActivity.cursor.moveToNext()){
            do {
                district= WeatherActivity.cursor.getString(WeatherActivity.cursor.getColumnIndex("district"));
                province= WeatherActivity.cursor.getString(WeatherActivity.cursor.getColumnIndex("province"));
                if(info.getProvince().equals(province)&&info.getDistrict().equals(district)){
                    code= WeatherActivity.cursor.getString(WeatherActivity.cursor.getColumnIndex("city_id"));
                    break;
                }
            }while (WeatherActivity.cursor.moveToNext());
        }
        WeatherActivity.cursor.close();
        WeatherActivity.dbcode.closeDatabase();
        return  code;
    }
}
