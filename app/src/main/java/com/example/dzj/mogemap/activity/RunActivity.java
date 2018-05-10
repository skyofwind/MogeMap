package com.example.dzj.mogemap.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.dialog.picker.MovingTargetPickerDialog;
import com.example.dzj.mogemap.fragment.ConfirmDialogFragment;
import com.example.dzj.mogemap.modle.Mogemap_run_record;
import com.example.dzj.mogemap.reciver.CountBroadcastReciver;
import com.example.dzj.mogemap.reciver.TimerBroadcastReciver;
import com.example.dzj.mogemap.service.RunService;
import com.example.dzj.mogemap.utils.CalConsumeUtil;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.MapUtil;
import com.example.dzj.mogemap.utils.StepDetection;
import com.example.dzj.mogemap.utils.SystemUtils;
import com.example.dzj.mogemap.utils.UserManager;
import com.example.dzj.mogemap.view.GpsStrengthView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by dzj on 2018/1/25.
 */

public class RunActivity extends AppCompatActivity {
    private static final String TAG = "RunActivity";
    public static final String COUNT_BROADCASTRECEIVER = "countBroadcastReceiver";
    private CountBroadcastReciver countBroadcastReciver;
    private TimerBroadcastReciver timerBroadcastReciver;

    private SensorManager sensorManager;
    private Sensor mAccelerometer,mOrientation;
    private StepDetection stepDetection;
    private OrientationSensorLinstener orientationSensorLinstener;
    private ImageView back;

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
    private UiSettings mUiSettings;

    private boolean isFirst = false; // 是否首次定位
    private MyLocationData locData;

    boolean isGPS = false;//当前是否是GPS定位
    private List<LatLng> mTrajectory = new LinkedList<>(), myPoints = new LinkedList<>();//轨迹经纬度集合
    private LatLng mLatLng;

    private static final Double MAX_DISTANCE = 22.00;//最大距离
    private static final Double MIN_DISTANCE = 2.0;//最小距离
    private double distance = 0;

    private MapUtil mapUtil;//地图绘制轨迹工具类

    private static int RECORD = 0;
    private int invalidTimes = 1;

    private int firstLoc = 0;//
    private boolean isStart = false, isEnd = false;
    //图标相关
    private int[] icomTextIds = new int[]{R.id.run_outdoor, R.id.run_indoor, R.id.walk, R.id.cycle};
    private int[] icomIds = new int[]{R.id.run_outdoor_icon, R.id.run_indoor_icon, R.id.walk_icon, R.id.cycle_icon};
    private int[] icomItemIds = new int[]{R.id.run_outdoor_item, R.id.run_indoor_item, R.id.walk_item, R.id.cycle_item};

    private int[] imageIds = new int[]{R.drawable.run_outdoor, R.drawable.run_indoor, R.drawable.walk, R.drawable.cycle};
    private int[] imageSelectdIds = new int[]{R.drawable.run_outdoor_choose, R.drawable.run_indoor_choose, R.drawable.walk_choose, R.drawable.cycle_choose};
    private static int runTypeNum = 0;
    private String runTypeRecord = "不设目标";
    private String[] datasRecord = new String[]{"基本跑步", ""};

    private String[] jsonName = new String[]{"run_outdoor_data.json", "run_indoor_data.json", "walk_data.json", "cycler_data.json"};

    private List<TextView> iconTexts;
    private List<ImageView> icons;
    private List<LinearLayout> iconItems;

    private GpsStrengthView gpsStrengthView;
    private LinearLayout linearLayoutCenter, linearLayoutCenterBottom, typeTarget;
    private Point mapPoint;
    private TextView searchTip;
    private TextView typeItem, itemValue, itemUnit;
    private MovingTargetPickerDialog.Builder builder;
    private MovingTargetPickerDialog dialog;
    public static String[] runType = new String[]{"不设目标", "距离目标", "时间目标", "热量目标"};
    public static String[] runBasicText = new String[]{"基本跑步", "基本跑步", "基本步行", "基本骑行"};

    private ImageView start;
    //新加载布局相关
    private LinearLayout slideTop, slideBottom, topTitle;
    private LinearLayout toggleDown, toggleUp;
    private TextView topTextTime, topTextMinute, topTextSecond;
    private TextView bottomTextTime, bottomTextMinute, bottomTextSecond;
    private TextView topRunTarget;
    private ProgressBar progress;
    private TextView topDistance, topSpeed, topSpeedUnit, topCalories;
    private ImageView topPause, topStop, topContinue;
    private LinearLayout topScModule;
    private TextView bottomDistance, bottomTime, bottomMinute, bottomSecond;

    private int targetType = 0;
    private double targetValue = 0;

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
    public void onBackPressed(){
        if (isFirst){

        }else {
            super.onBackPressed();
        }
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
        Intent intent1 = new Intent(RunActivity.this, RunService.class);
        intent1.putExtra("type", "stop");
        startService(intent1);
    }
    private void initViews() {
        initViewOfIcon();
        start = (ImageView)findViewById(R.id.start);
        start.setOnClickListener(startListener);
        gpsStrengthView = (GpsStrengthView)findViewById(R.id.gpsStrength);
        searchTip = (TextView)findViewById(R.id.searchTip);
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
        linearLayoutCenter = (LinearLayout)findViewById(R.id.linearlayout_center);
        linearLayoutCenterBottom = (LinearLayout)findViewById(R.id.linearlayout_center_bottom);
        typeTarget = (LinearLayout)findViewById(R.id.type_target);
        typeItem = (TextView)findViewById(R.id.typeItem);
        itemValue = (TextView)findViewById(R.id.itemValue);
        itemUnit = (TextView)findViewById(R.id.itemUnit);
        typeTarget.post(new Runnable() {
            @Override
            public void run() {
                //log("theTop: "+linearLayoutCenter.getTop());
                //log("theTop: "+linearLayoutCenterBottom.getTop());
                //log("theTop: "+typeTarget.getTop());
                int y = linearLayoutCenter.getTop()+linearLayoutCenterBottom.getTop()+45;
                int x = SystemUtils.MAX_WIDTH/2-15;
                mapPoint = new Point(x, y);
                //log("theTop: "+y);
            }
        });
        linearLayoutCenterBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTypeChoose();
            }
        });
        changeChooseIcon(icomItemIds[runTypeNum]);
        back = (ImageView)findViewById(R.id.icon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            //isMove();
            if (location == null || mMapView == null) {
                return;
            }
            //Log.i("mytype","纬度="+location.getLongitude()+" 经度="+location.getLatitude());
            setIsGps(location.getLocType());
            //log("drawLine-mode: "+location.getLocType());
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            myPoints.add(mLatLng);
            if (!isFirst) {
                MapStatus.Builder builder = new MapStatus.Builder();
                if(mapPoint != null){
                    builder.targetScreen(mapPoint);
                }
                builder.target(mLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            int strength = getGpsStrength(location.getSatelliteNumber());
            //log("strength="+strength+" number"+location.getSatelliteNumber());
            if(strength > 0){
                if(searchTip.getVisibility() == View.VISIBLE){
                    searchTip.setVisibility(View.GONE);
                }
                if(gpsStrengthView.getVisibility() == View.GONE){
                    gpsStrengthView.setVisibility(View.VISIBLE);
                }
                gpsStrengthView.setStrength(strength);
            }else {
                if(searchTip.getVisibility() == View.GONE){
                    searchTip.setVisibility(View.VISIBLE);
                }
                if(gpsStrengthView.getVisibility() == View.VISIBLE){
                    gpsStrengthView.setVisibility(View.GONE);
                }
            }
            try{
                if(isFirst){
                    if(isMove()&&mTrajectory.size()!=0){
                        //mapUtil.drawTwoPointLine(mTrajectory.get(mTrajectory.size()-1),mLatLng);
                        //log("drawLine-mode: "+location.getLocType());
                        if (isEnd){
                            mapUtil.drawHistoryTrack(mTrajectory);
                        }else {
                            mapUtil.drawRuningLine(mTrajectory);
                        }
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
        //BitmapUtil.init();
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        //mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setAllGesturesEnabled(false);
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
        log("ismoving");
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
                    if(distance > MAX_DISTANCE*invalidTimes){
                        invalidTimes++;
                        return false;
                    }
                    mTrajectory.add(mLatLng);
                    this.distance +=distance;
                    //setLatlng(mTrajectory.size()+" "+distance);
                    log("drawLine-distance: "+distance);
                    //setLatlng("第"+RECORD+"次绘制轨迹 "+mTrajectory.size());
                    invalidTimes = 1;
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
                mCurrentMode, true, mCurrentMarker, 0x00ffffff, 0x00ffffff));
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
    //private int time = 0, minute = 0, second = 0;
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
        if(null == timerBroadcastReciver){
            IntentFilter filter = new IntentFilter();
            filter.addAction(TimerBroadcastReciver.TIMER_NAME);
            timerBroadcastReciver = new TimerBroadcastReciver();
            registerReceiver(timerBroadcastReciver,filter);
            timerBroadcastReciver.setTimerListener(new TimerBroadcastReciver.TimerListener() {
                @Override
                public void onChange() {
                    //dealTime();
                    //log("RunService="+RunService.time+" "+RunService.minute+" "+RunService.second);
                    setTimeText(getTime(RunService.time, RunService.minute, RunService.second));
                    //setTimeText(getTime(time, minute, second));
                    setDistanceText(distance);
                    setCalories(runTypeNum, getWeight(), distance/1000);
                    setProgress();
                }
            });
        }
    }
    private void unregisterBroadcastReciver(){
        if(null != countBroadcastReciver){
            unregisterReceiver(countBroadcastReciver);
            countBroadcastReciver = null;
        }
        if(null != timerBroadcastReciver){
            unregisterReceiver(timerBroadcastReciver);
            timerBroadcastReciver = null;
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
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if(v.getId() == icomItemIds[runTypeNum]){

            }else {
                changeChooseIcon(v.getId());
            }

        }
    };
    private void changeChooseIcon(int id){
        for (int i = 0; i < icomItemIds.length; i++){
            if (id == icomItemIds[i]){
                if(Build.VERSION.SDK_INT >= 23){
                    icons.get(i).setImageDrawable(getDrawable(imageSelectdIds[i]));
                    iconTexts.get(i).setTextColor(getColor(R.color.black));
                }else {
                    icons.get(i).setImageDrawable(getResources().getDrawable(imageSelectdIds[i]));
                    iconTexts.get(i).setTextColor(getResources().getColor(R.color.black));
                }

                runTypeNum = i;
                MovingTargetPickerDialog.dataName = jsonName[i];
                builder = new MovingTargetPickerDialog.Builder(this);
                typeItem.setText(runType[0]);
                itemValue.setText(runBasicText[i]);
                itemUnit.setText("");
            }else {
                if(Build.VERSION.SDK_INT >= 23){
                    icons.get(i).setImageDrawable(getDrawable(imageIds[i]));
                    iconTexts.get(i).setTextColor(getColor(R.color.text_run_no_selectd));
                }else {
                    icons.get(i).setImageDrawable(getResources().getDrawable(imageIds[i]));
                    iconTexts.get(i).setTextColor(getResources().getColor(R.color.text_run_no_selectd));
                }

            }
        }
    }
    private int getGpsStrength(int number){
        int strength = -1;
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
    private void setIsGps(int type){
        switch (type){
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
    }
    String[] s;
    private final void showDialogTypeChoose() {

        dialog = builder.setOnMovingTargetSelectedListener(new MovingTargetPickerDialog.OnMovingTargetSelectedListener() {
            @Override
            public void onMovingTargetSelected(String[] datas) {
                //Toast.makeText(getApplicationContext(), datas[0] + "#" + datas[1], Toast.LENGTH_SHORT).show();
                runTypeRecord = datas[0];
                typeItem.setText(datas[0]);
                s = parseText(datas[1]);
                datasRecord = s;
                targetType = getTargetType(typeItem.getText().toString());
                itemValue.setText(s[0]);
                itemUnit.setText(s[1]);
            }

        }).create();
        dialog.show();
    }

    private String[] parseText(String str){
        String[] s = new String[2];
        String temp = str.substring(str.length()-2, str.length());
        if (temp.equals("公里") || temp.equals("分钟") || temp.equals("千卡")){
            s[0] = str.substring(0, str.length()-2);
            s[1] = temp;
            targetValue = Double.parseDouble(s[0]);
        }else {
            s[0] = str;
            s[1] = "";
            if(str.equals("半马")){
                targetValue = 21.09;
            }
            if(str.equals("全马")){
                targetValue = 42.19;
            }
        }
        return s;
    }
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isFirst = true;
            mUiSettings.setAllGesturesEnabled(true);
            //int height= 0;
            LinearLayout title = (LinearLayout)findViewById(R.id.title);
            LinearLayout typeBar = (LinearLayout)findViewById(R.id.type_bar);
            LinearLayout bottomBar = (LinearLayout)findViewById(R.id.bottomBar);
           //height += typeBar.getHeight();
            title.setVisibility(View.GONE);
            typeBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            linearLayoutCenterBottom.setVisibility(View.GONE);
            linearLayoutCenter.setPadding(0, 20, 0, 0);
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(linearLayoutCenter.getLayoutParams());
            //params.height = height;
            //params.topMargin = SystemUtils.dip2px(RunActivity.this, 10);
            //linearLayoutCenter.setLayoutParams(params);
            //linearLayoutCenter.setBackgroundColor(0x55000000);
            initSlideMenu(runTypeRecord);
        }
    };
    private int topMenuHeight = 0, bottomMenuHeight = 0;
    private Point pTop,pBottom;
    private void initSlideMenu(String type){
        RelativeLayout root = (RelativeLayout)findViewById(R.id.root);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(getMenuId(type), null);
        root.addView(view);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(view.getLayoutParams());
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        view.setLayoutParams(params2);

        if(!runTypeRecord.equals(runType[0])){
            topRunTarget = (TextView)view.findViewById(R.id.top_run_target);
            progress = (ProgressBar)view.findViewById(R.id.progress);
            log("Myprogress"+s[0]);
            topRunTarget.setText(s[0]+" "+s[1]+" ");
        }
        slideTop = (LinearLayout)view.findViewById(R.id.slide_top);
        slideBottom = (LinearLayout)view.findViewById(R.id.slide_bottom);
        topTitle = (LinearLayout)view.findViewById(R.id.top_title);
        toggleDown = (LinearLayout)view.findViewById(R.id.toggle_down);
        toggleUp = (LinearLayout)view.findViewById(R.id.toggle_up);
        topTextTime = (TextView)view.findViewById(R.id.top_text_time);
        topTextMinute = (TextView)view.findViewById(R.id.top_text_minute);
        topTextSecond = (TextView)view.findViewById(R.id.top_text_second);
        bottomTextTime = (TextView)view.findViewById(R.id.bottom_time);
        bottomTextMinute = (TextView)view.findViewById(R.id.bottom_minute);
        bottomTextSecond = (TextView)view.findViewById(R.id.bottom_second);
        progress = (ProgressBar)view.findViewById(R.id.progress);
        topDistance = (TextView)view.findViewById(R.id.top_distance);
        topSpeed = (TextView)view.findViewById(R.id.top_speed);
        topSpeedUnit = (TextView)view.findViewById(R.id.top_speed_unit);
        topCalories = (TextView)view.findViewById(R.id.top_calories);
        topPause = (ImageView)view.findViewById(R.id.top_pause);
        topStop = (ImageView)view.findViewById(R.id.top_stop);
        topContinue = (ImageView)view.findViewById(R.id.top_continue);
        topScModule = (LinearLayout)view.findViewById(R.id.top_sc_module);
        bottomDistance = (TextView)view.findViewById(R.id.bottom_distance);
        bottomTime = (TextView)view.findViewById(R.id.bottom_time);
        bottomMinute = (TextView)view.findViewById(R.id.bottom_minute);
        bottomSecond = (TextView)view.findViewById(R.id.bottom_second);
        //startAlarm();
        Intent intent = new Intent(RunActivity.this, RunService.class);
        startService(intent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(slideBottom.getLayoutParams());
        params.width = SystemUtils.MAX_WIDTH;
        slideBottom.setLayoutParams(params);
        slideTop.post(new Runnable() {
            @Override
            public void run() {
                topMenuHeight = slideTop.getHeight();
                bottomMenuHeight = topTitle.getHeight();
                pTop = new Point(SystemUtils.MAX_WIDTH/2-15, (SystemUtils.HEIGHT-topMenuHeight)/2);
                pBottom = new Point(SystemUtils.MAX_WIDTH/2-15, (SystemUtils.HEIGHT-bottomMenuHeight)/2);
                mMapView.setPadding(0, 0, 0, topMenuHeight);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.targetScreen(pTop);
                builder.target(mLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });
        toggleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(slideTop.getVisibility() == View.GONE){
                    slideBottom.setVisibility(View.GONE);
                    slideTop.setVisibility(View.VISIBLE);
                    mMapView.setPadding(0, 0, 0, topMenuHeight);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.targetScreen(pTop);
                    builder.target(mLatLng);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        });
        toggleDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(slideBottom.getVisibility() == View.GONE){
                    slideTop.setVisibility(View.GONE);
                    slideBottom.setVisibility(View.VISIBLE);
                    mMapView.setPadding(0, 0, 0, bottomMenuHeight);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.targetScreen(pBottom);
                    builder.target(mLatLng);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        });
        topPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topScModule.getVisibility() == View.GONE){
                    topPause.setVisibility(View.GONE);
                    topScModule.setVisibility(View.VISIBLE);
                    Intent intent1 = new Intent(RunActivity.this, RunService.class);
                    intent1.putExtra("type", "pause");
                    startService(intent1);
                }
            }
        });
        topStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(distance > 100){
                    //getMyRunRecord(runTypeNum, getWeight(), distance, getSeconds(), mTrajectory);
                    String phone = UserManager.getInstance().getUser().getPhone();
                    if(phone == null){
                        tip("用户未登录，运动数据无法上传但会保存至本地");
                        //RunActivity.this.finish();
                    }else {
                        String json = getMyRunRecord(runTypeNum, getWeight(), distance, getSeconds(), mTrajectory);
                        Log.d("post", "url="+(HttpUtil.ADD_RECORD));
                        postRunRecord(json, HttpUtil.ADD_RECORD);

                    }
                }else {
                    ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                    confirmDialogFragment.show(getFragmentManager(), "android");
                    confirmDialogFragment.setKeep(new ConfirmDialogFragment.OnDialogListener() {
                        @Override
                        public void onDialogClick() {
                            if(topPause.getVisibility() == View.GONE){
                                topScModule.setVisibility(View.GONE);
                                topPause.setVisibility(View.VISIBLE);
                                Intent intent1 = new Intent(RunActivity.this, RunService.class);
                                startService(intent1);
                            }
                        }
                    });
                    confirmDialogFragment.setFinish(new ConfirmDialogFragment.OnDialogListener() {
                        @Override
                        public void onDialogClick() {
                            RunActivity.this.finish();
                        }
                    });
                }
            }
        });
        topContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topPause.getVisibility() == View.GONE){
                    topScModule.setVisibility(View.GONE);
                    topPause.setVisibility(View.VISIBLE);
                    Intent intent1 = new Intent(RunActivity.this, RunService.class);
                    startService(intent1);
                }
            }
        });
    }
    private int getMenuId(String value){
        int id = R.layout.type_basic_layout;
        if(value.equals(runType[0])){
            id = R.layout.type_basic_layout;
        }else if (value.equals(runType[1])){
            id = R.layout.type_distance_layout;
        }else if (value.equals(runType[2])){
            id = R.layout.type_time_layout;
        }else if (value.equals(runType[3])){
            id = R.layout.type_calories_layout;
        }
        return id;
    }
//    private void dealTime(){
//        second++;
//        if(second >= 60){
//            second = 0;
//            minute++;
//        }
//        if(minute >= 60){
//            minute = 0;
//            time++;
//        }
//        log("strs="+time+" "+minute+" "+second);
//    }
    private String[] getTime(int time, int minute, int second){
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
    private void setTimeText(String[] strs){
        setText(topTextTime, strs[0]);
        setText(topTextMinute, strs[1]);
        setText(topTextSecond, strs[2]);
        setText(bottomTextTime, strs[0]);
        setText(bottomTextMinute, strs[1]);
        setText(bottomTextSecond, strs[2]);
    }
    private void setDistanceText(double distance){
        DecimalFormat df = new DecimalFormat("######0.00");
        double x = distance/1000;
        setText(topDistance, df.format(x)+"");
        setText(bottomDistance, df.format(x)+"");
    }
    private void setCalories(int position, double kg, double km){
        int cal = CalConsumeUtil.getCalByType(position, kg, km);
        setText(topCalories, cal+"");
    }
    private void setText(TextView view, String s){
        if(null != view){
            view.setText(s);
        }
    }
    private String getMyRunRecord(int position, double kg, double distance, int runTime, List<LatLng> points){
        Mogemap_run_record run_record = new Mogemap_run_record();
        String array = JSON.toJSONString(points);
        Log.d("post", "json="+array);
        //array = array.replace("\"", "\\");
        run_record.setCalories(CalConsumeUtil.getCalByType(position, kg, distance/1000));
        run_record.setRuntime(runTime);
        run_record.setDistance(distance);
        run_record.setRuntype(position+"");
        run_record.setJson(array);
        run_record.setId(0);
        run_record.setDate(new Date());
        run_record.setPhone(UserManager.getInstance().getUser().getPhone());
        String json = JSON.toJSONStringWithDateFormat(run_record, "yyyy-MM-dd HH:mm:ss");//new Gson().toJson(run_record);
        Log.d("post", "json="+json);
        run_record = JSON.parseObject(json, Mogemap_run_record.class);
        json = JSON.toJSONString(run_record);
        return json;
    }
    private void postRunRecord(String json, String url){
        Log.d("post", "json="+json);
        OkHttpUtils
                .postString()
                .url(url)
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        Log.d("post", " call="+call.toString()+" e="+e.toString()+" id="+id);
                        tip("上传失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("post", "response"+response.toString()+" id="+id);
                        tip("上传成功");
                        Mogemap_run_record record = JSON.parseObject(response, Mogemap_run_record.class);
                        Intent intent = new Intent(RunActivity.this, RunRecordActivity.class);
                        intent.putExtra("id", record.getId());
                        startActivity(intent);
                        RunActivity.this.finish();
                    }
                });
    }
    private int getSeconds(){
        int seconds = RunService.second+RunService.minute*60+RunService.time*60*60;
        return seconds;
    }
    private int getWeight(){
        int weight = UserManager.getInstance().getUser().getWeight();
        if(weight == 0){
            return CalConsumeUtil.DEFAULT_WEIGHT;
        }
        return weight;
    }
    private int getTargetType(String s){
        for (int i = 0; i < runType.length; i++){
            if (s.equals(runType[i])){
                return i;
            }
        }
        return 0;
    }
    private void setProgress(){
        if(progress != null){
            double value;
            if(targetType == 1){//公里
                value = (distance/(targetValue*1000))*1000;
                if (value < 5){
                    value = 5;
                }
                progress.setProgress((int)value);
                log("Myprogress="+(int)value);
            }else if (targetType == 2){//分钟
                value = getSeconds()/(targetValue*60)*1000;
                if(value < 5){
                    value = 5;
                }
                progress.setProgress((int)value);
                log("Myprogress="+(int)value);
            }else if (targetType == 3){//千卡
                int cal = CalConsumeUtil.getCalByType(runTypeNum, getWeight(), distance/1000);
                value = cal/targetValue*1000;
                if(value < 5){
                    value = 5;
                }
                progress.setProgress((int)value);
                log("Myprogress="+(int)value);
            }
        }
    }
}
