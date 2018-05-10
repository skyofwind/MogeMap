package com.example.dzj.mogemap.weather.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by dzj on 2017/5/22.
 */

public class BaiDuLocation {
    //百度地图调用
    private LocationClient mLocationClient;
    private BDLocationListener myListener;
    private boolean complete,locationType;
    private LocationInfo info;
    private Context context;
    public BaiDuLocation(Context context){
        this.context=context;
        info=new LocationInfo();
        complete=false;
        locationType=false;
        mLocationClient = new LocationClient(context);     //声明LocationClient类
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initLocation();
    }

    //百度定位相关
    private void initLocation(){
        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(mOption);
    }
    //百度定位监听器
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Intent intent=new Intent();
            intent.setAction(WeatherActivity.DYNAMICACTION);
            Bundle bundle=new Bundle();
            try{
                //得到当前城市名并转为拼音传入天气接口函数
                if((!location.getProvince().equals(""))&&(!location.getCity().equals(""))&&(!location.getDistrict().equals(""))){
                    info.setProvince(DataDeal.data_Province(location.getProvince()));
                    info.setCity(DataDeal.data_City(location.getCity()));
                    info.setDistrict(DataDeal.data_District(location.getDistrict()));
                    complete=true;
                    locationType=true;
                    Log.d("经度",location.getLongitude()+"");
                    Log.d("纬度",location.getLatitude()+"");
                    mLocationClient.stop();
                    intent.putExtra(WeatherActivity.RESULT,"success");
                    bundle.putParcelable(WeatherActivity.LOCATIONINFO,info);
                }
            }catch(Exception e){
                e.printStackTrace();
                info.setProvince("北京");
                info.setCity("北京");
                info.setDistrict("北京");
                complete=true;
                mLocationClient.stop();
                intent.putExtra(WeatherActivity.RESULT,"fail");
                bundle.putParcelable(WeatherActivity.LOCATIONINFO,info);
            }
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }
    }
    public void Start(){
        mLocationClient.start();
    }
    public void Stop(){
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }
    public boolean isLocationStart(){
        if (mLocationClient.isStarted()){
            return true;
        }else {
            return false;
        }
    }
    public LocationInfo getInfo(){
        return info;
    }
    public boolean getComplete(){
        return complete;
    }
    public boolean getLocationType(){
        return locationType;
    }
}
