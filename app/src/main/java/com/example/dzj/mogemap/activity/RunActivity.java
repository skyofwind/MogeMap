package com.example.dzj.mogemap.activity;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.reciver.CountBroadcastReciver;
import com.example.dzj.mogemap.utils.BitmapUtil;
import com.example.dzj.mogemap.utils.MapUtil;
import com.example.dzj.mogemap.utils.StepDetection;
import com.example.dzj.mogemap.utils.SystemUtils;
import com.example.dzj.mogemap.view.GpsStrengthView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2018/1/25.
 */

public class RunActivity extends AppCompatActivity {
    private static final String TAG = "RunActivity";
    public static final String COUNT_BROADCASTRECEIVER = "countBroadcastReceiver";
    private CountBroadcastReciver countBroadcastReciver;

    private SensorManager sensorManager;
    private Sensor mAccelerometer,mOrientation;
    private StepDetection stepDetection;
    private OrientationSensorLinstener orientationSensorLinstener;

    private TextView count;
    private TextView mtip;
    private TextView latlng;

    // 定位相关
    LocationClient mLocClient;
    private BDLocationListener myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    boolean isGPS = false;//当前是否是GPS定位
    private List<LatLng> mTrajectory = new ArrayList<>();//轨迹经纬度集合
    private LatLng mLatLng;

    private static final Double MAX_DISTANCE = 22.00;//最大距离
    private static final Double MIN_DISTANCE = 2.0;//最小距离

    private MapUtil mapUtil;//地图绘制轨迹工具类

    private static int RECORD = 0;

    private int firstLoc = 0;//
    private boolean isStart = false, isEnd = false;
    //图标相关
    private int[] icomTextIds = new int[]{R.id.run_outdoor, R.id.run_indoor, R.id.walk, R.id.cycle};
    private int[] icomIds = new int[]{R.id.run_outdoor_icon, R.id.run_indoor_icon, R.id.walk_icon, R.id.cycle_icon};
    private int[] icomItemIds = new int[]{R.id.run_outdoor_item, R.id.run_indoor_item, R.id.walk_item, R.id.cycle_item};

    private int[] imageIds = new int[]{R.drawable.run_outdoor, R.drawable.run_indoor, R.drawable.walk, R.drawable.cycle};
    private int[] imageSelectdIds = new int[]{R.drawable.run_outdoor_choose, R.drawable.run_indoor_choose, R.drawable.walk_choose, R.drawable.cycle_choose};

    private List<TextView> iconTexts;
    private List<ImageView> icons;
    private List<LinearLayout> iconItems;

    private GpsStrengthView gpsStrengthView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_main);

        initViews();
        //getPersimmions();
        initMap();
        initSensor();
        setCurrentMode();

    }
    @Override
    protected void onResume() {
        super.onResume();
        // 注册传感器监听函数

        mMapView.onResume();
        registerBroadcastReciver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMapView.onPause();
    }
    @Override
    protected void onStop() {
        // 注销监听函数
        super.onStop();

        unregisterBroadcastReciver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        sensorManager.unregisterListener(stepDetection);
        sensorManager.unregisterListener(orientationSensorLinstener);
    }
    private void initViews() {
        initViewOfIcon();
        gpsStrengthView = (GpsStrengthView)findViewById(R.id.gpsStrength);
        gpsStrengthView.setStrength(0);
        final int[] width = {0};
        if(iconItems.size() > 0){
            iconItems.get(0).post(new Runnable() {
                @Override
                public void run() {
                    for (LinearLayout l: iconItems) width[0] += l.getWidth();
                    int remainder = SystemUtils.MAX_WIDTH - width[0];
                    int margin = remainder/5;
                    for (LinearLayout l: iconItems){
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(l.getLayoutParams());
                        params.leftMargin = margin;
                        l.setLayoutParams(params);
                    }
                }
            });
        }


        //mSensorInfoA = (TextView) findViewById(R.id.sensor_info_a);
        //count = (TextView)findViewById(R.id.count);
        //mtip = (TextView)findViewById(R.id.tip);
        //latlng = (TextView)findViewById(R.id.latlng);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            Log.i("mytype","纬度="+location.getLongitude()+" 经度="+location.getLatitude());
            switch (location.getLocType()){
                case BDLocation.TypeNone:
                    tip("无效定位结果");
                    isGPS = false;
                    break;
                case BDLocation.TypeGpsLocation:
                    tip("GPS信号良好");
                    isGPS = true;
                    break;
                case BDLocation.TypeCriteriaException:
                    tip("无法定位结果");
                    isGPS = false;
                    break;
                case BDLocation.TypeNetWorkException:
                    tip("网络连接失败");
                    isGPS = false;
                    break;
                case BDLocation.TypeOffLineLocation:
                    tip("离线定位中，GPS信号差，请走到宽阔地方进行运动");
                    isGPS = false;
                    break;
                case BDLocation.TypeOffLineLocationFail:
                    tip("网络连接失败");
                    isGPS = false;
                    break;
                case BDLocation.TypeOffLineLocationNetworkFail:
                    tip("离线定位失败结果");
                    isGPS = false;
                    break;
                case BDLocation.TypeNetWorkLocation:
                    tip("网络定位成功，GPS信号差，请走到宽阔地方进行运动");
                    isGPS = false;
                    break;
                case BDLocation.TypeCacheLocation:
                    tip("缓存定位结果");
                    isGPS = false;
                    break;
                case BDLocation.TypeServerError:
                    tip("server定位失败，没有对应的位置信息");
                    isGPS = false;
                    break;
            }
            log("drawLine-mode: "+location.getLocType());
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            mLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            //setLatlng(mLatLng.toString());
            //log(mCurrentLat+" "+mCurrentLon);
            log("gps-strength:"+location.getGpsAccuracyStatus()+" "
                    +location.getGpsCheckStatus()
                    +" "+location.getSatelliteNumber()+" "+location.isIndoorLocMode()+" "+location.getNetworkLocationType());
            if(iconTexts != null){
                iconTexts.get(0).setText("卫星:"+location.getSatelliteNumber());
                iconTexts.get(1).setText("信号:"+location.getGpsAccuracyStatus()+"");
            }
            try{
                if(isMove()&&mTrajectory.size()!=0){
                    //mapUtil.drawTwoPointLine(mTrajectory.get(mTrajectory.size()-1),mLatLng);
                    log("drawLine-mode: "+location.getLocType());
                    if (isEnd){
                        mapUtil.drawHistoryTrack(mTrajectory);
                    }else {
                        mapUtil.drawRuningLine(mTrajectory);
                    }

                }
            }catch (Exception e){
                stip("isMove判断出错"+e.getStackTrace());
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    private void tip(String s){
        //mtip.setText(s);
    }
    private void initMap(){
        BitmapUtil.init();
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        //工具类初始化
        mapUtil=MapUtil.getInstance();
        mapUtil.init(mMapView);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        //设置定位监听
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2*1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }
    private void initSensor(){
        // 初始化传感器
        stepDetection = new StepDetection(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        orientationSensorLinstener = new OrientationSensorLinstener();
        mOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        sensorManager.registerListener(stepDetection, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(orientationSensorLinstener,mOrientation,SensorManager.SENSOR_DELAY_UI);
    }
    private boolean isMove(){
        if (stepDetection.is_Acc){
            log("stepDetection.is_Acc="+stepDetection.is_Acc);
        } else {
            log("stepDetection.is_Acc="+stepDetection.is_Acc);
        }
        if(isGPS){
            //判断手机是否静止,如果静止,判定采集点无效,直接抛弃
            if(!stepDetection.is_Acc&&stepDetection.IsRun){
                stepDetection.IsRun=false;
                return false;
            }
            //抛弃初始三位数据
            if(firstLoc < 3){
                if(firstLoc < 3){
                    firstLoc++;
                    mTrajectory.add(mLatLng);
                }
                //mTrajectory.add(mLatLng);
                //mapUtil.drawStartPoint(mLatLng);
                return false;
            }else{
                try{
                    if(!isStart){
                        if (isDrawStart(mTrajectory)){
                            isStart = true;
                            return true;
                        }else {
                            firstLoc = 0;
                            mTrajectory.clear();
                            return false;
                        }
                    }
                    double distance = DistanceUtil.getDistance(mTrajectory.get(mTrajectory.size()-1),mLatLng);
                    if(distance < MIN_DISTANCE){
                        return false;
                    }
                    if(distance > MAX_DISTANCE){
                        return false;
                    }
                    mTrajectory.add(mLatLng);
                    //setLatlng(mTrajectory.size()+" "+distance);
                    log("drawLine-distance: "+distance);
                    //setLatlng("第"+RECORD+"次绘制轨迹 "+mTrajectory.size());
                    return true;

                }catch (Exception e){
                    stip("第二部分定位出错"+e.getStackTrace());
                    log("第二部分定位出错"+e.toString());
                }

            }
        }
        return false;
    }
    class OrientationSensorLinstener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType()==Sensor.TYPE_ORIENTATION){
                double x = sensorEvent.values[SensorManager.DATA_X];
                if (Math.abs(x - lastX) > 1.0) {
                    mCurrentDirection = (int) x;
                    locData = new MyLocationData.Builder()
                            .accuracy(mCurrentAccracy)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mCurrentDirection).latitude(mCurrentLat)
                            .longitude(mCurrentLon).build();
                    mBaiduMap.setMyLocationData(locData);
                }
                lastX = x;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
    private void log(String s){
        Log.i(TAG,s);
    }
    private void setCurrentMode(){
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
    }
    private void stip(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
    private void setLocalCount(int count){
        //this.count.setText(count+"步");
    }
    private void setLatlng(String s){
        this.latlng.setText(s);
    }
    public interface CountListener{
        void setCount(int count);
    }
    private void registerBroadcastReciver(){
        if(null == countBroadcastReciver){
            IntentFilter filter = new IntentFilter();
            filter.addAction(COUNT_BROADCASTRECEIVER);
            countBroadcastReciver = new CountBroadcastReciver();
            registerReceiver(countBroadcastReciver,filter);
            countBroadcastReciver.setCountListener(new CountListener() {
                @Override
                public void setCount(int count) {
                    //setLocalCount(count);
                }
            });
        }
    }
    private void unregisterBroadcastReciver(){
        if(null != countBroadcastReciver){
            unregisterReceiver(countBroadcastReciver);
            countBroadcastReciver = null;
        }
    }
    private boolean isDrawStart(List<LatLng> list){
        double m = 0;
        for (int i = 1; i < list.size(); i++){
            double distance = DistanceUtil.getDistance(list.get(i-1), list.get(i));
            if(distance >= 0 && distance <= MAX_DISTANCE){
                m +=distance;
            }else {
                return false;
            }
        }
        log("drawLine-m="+m);
        return true;
    }
    private void initViewOfIcon(){
        iconTexts = new ArrayList<>();
        iconItems = new ArrayList<>();
        icons = new ArrayList<>();

        for(int i = 0;i < icomIds.length;i++){
            icons.add((ImageView)findViewById(icomIds[i]));
            iconItems.add((LinearLayout)findViewById(icomItemIds[i]));
            iconItems.get(i).setOnClickListener(runTypeChoose);
            iconTexts.add((TextView)findViewById(icomTextIds[i]));
        }
    }
    View.OnClickListener runTypeChoose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeChooseIcon(v.getId());
        }
    };
    private void changeChooseIcon(int id){
        for (int i = 0; i < icomItemIds.length; i++){
            if (id == icomItemIds[i]){
                icons.get(i).setImageDrawable(getDrawable(imageSelectdIds[i]));
                iconTexts.get(i).setTextColor(getColor(R.color.black));
            }else {
                icons.get(i).setImageDrawable(getDrawable(imageIds[i]));
                iconTexts.get(i).setTextColor(getColor(R.color.text_run_no_selectd));
            }
        }
    }
    private int getGpsStrength(int number){
        int strength = 0;
        if(number < 4){
            strength = 0;
        }else if(number< 8){
            strength = 1;
        }else if (number < 16){
            strength = 2;
        }else {
            strength = 3;
        }
        return strength;
    }
}
