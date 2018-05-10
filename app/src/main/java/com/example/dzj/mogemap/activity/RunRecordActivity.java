package com.example.dzj.mogemap.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.modle.Mogemap_run_record;
import com.example.dzj.mogemap.modle.MyLatLng;
import com.example.dzj.mogemap.utils.BitmapUtil;
import com.example.dzj.mogemap.utils.FileUtil;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.MapUtil;
import com.example.dzj.mogemap.utils.OtherUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

import static android.R.attr.path;

/**
 * Created by dzj on 2018/2/28.
 */

public class RunRecordActivity extends BaseActivty {

    private final static String TAG = "RunRecordActivity";

    private TextView date, topDistance, hour, minute, second, topSpeed, topCalories;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private RelativeLayout personalData;
    private MapUtil mapUtil;//地图绘制轨迹工具类
    Mogemap_run_record record;
    private Bitmap mapBitmap, textBitmap;
    private String screenName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runrecord_layout);
        personalData = (RelativeLayout)findViewById(R.id.personal_data);
        setMyTitle();
        initTextView();
        initMap();
        getData();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message m){
            switch (m.what){
                case 0x01:
                    updateUI();
                    drawMap();
                    break;
                case 0x02:
                    statrProgressDialog();
                    break;
                case 0x03:
                    cancel();
                    break;
                case 0x04:
                    showShare();
                    break;
                case 0x05:

                    break;
            }
        }
    };
    private void setMyTitle(){
        initTitle();
        setTitle("运动记录");
        setIconRight(R.drawable.shared);
        setIconListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seticonRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMapBitmap();
            }
        });
    }
    private void initTextView(){
        date = (TextView)findViewById(R.id.date);
        topDistance = (TextView)findViewById(R.id.top_distance);
        hour = (TextView)findViewById(R.id.top_text_time);
        minute = (TextView)findViewById(R.id.top_text_minute);
        second = (TextView)findViewById(R.id.top_text_second);
        topSpeed = (TextView)findViewById(R.id.top_speed);
        topCalories = (TextView)findViewById(R.id.top_calories);
    }
    private void initMap(){
        mapView = (MapView)findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        mapUtil=MapUtil.getInstance();
        mapUtil.init(mapView);
    }
    private void getData(){
        int id = getIntent().getIntExtra("id", 0);
        handler.sendEmptyMessage(0x02);
        OkHttpUtils
                .get()
                .url(HttpUtil.GET_RECORD+id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        Toast.makeText(RunRecordActivity.this, "请求错误", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0x03);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log(response);
                        record = JSON.parseObject(response, Mogemap_run_record.class);
                        handler.sendEmptyMessage(0x01);
                        handler.sendEmptyMessage(0x03);
                    }
                });
    }
    private void updateUI(){
        DecimalFormat df = new DecimalFormat("######0.00");
        double km = record.getDistance()/1000;
        topDistance.setText(df.format(km));
        int mSecond = record.getRuntime()%60;
        int myMinute = record.getRuntime()/60;
        int mMinute, mHour;
        if(myMinute >= 60){
            mMinute = myMinute%60;
            mHour = myMinute/60;
        }else {
            mMinute = myMinute;
            mHour = 0;
        }
        String[] time = getTime(mHour, mMinute, mSecond);
        hour.setText(time[0]);
        minute.setText(time[1]);
        second.setText(time[2]);

        topCalories.setText(record.getCalories()+"");
        if(record.getDistance() == 0){
            topSpeed.setText("----");
        }else {
            double m = record.getRuntime()/60/record.getDistance()/1000;
            String myPace = OtherUtil.getPace(m);
            topSpeed.setText(myPace);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日 HH:mm");
        date.setText(simpleDateFormat.format(record.getDate())+" "+OtherUtil.getRunType(Integer.parseInt(record.getRuntype())));
    }
    private void drawMap(){
        if(record != null){
            log(record.getJson());
            List<MyLatLng> mlist = JSON.parseArray(record.getJson(), MyLatLng.class);
            List<LatLng> list = getLatLngs(mlist);

            LatLng center = new LatLng((list.get(0).latitude+list.get(list.size()-1).latitude)/2,(list.get(0).longitude+list.get(list.size()-1).longitude)/2);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(center).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            mapUtil.drawHistoryTrack(list);

        }
    }
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
    private void log(String s){
        Log.d(TAG, s);
    }
    private List<LatLng> getLatLngs(List<MyLatLng> list){
        List<LatLng> latLngs = new LinkedList<>();
        for (MyLatLng l: list){
            LatLng latLng = new LatLng(l.latitude, l.longitude);
            latLngs.add(latLng);
        }
        return latLngs;
    }
    private void getMapBitmap(){
//        textBitmap = convertViewToBitmap(personalData);
//        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
//            @Override
//            public void onSnapshotReady(Bitmap bitmap) {
//                mapBitmap = BitmapUtil.mergeBitmap(bitmap, textBitmap);
//                saveImageToGallery(RunRecordActivity.this, mapBitmap);
//            }
//        });
        handler.sendEmptyMessage(0x04);
    }
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();

        //关闭sso授权
        //oks.disableSSOWhenAuthorize();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("我的运动轨迹");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl(HttpUtil.DISPLAY_RECORD+record.getId());
        // text是分享文本，所有平台都需要这个字段
        DecimalFormat df = new DecimalFormat("######0.00");
        double km = record.getDistance()/1000;
        oks.setText("我跑了"+df.format(km)+"公里,消耗了"+record.getCalories()+"千卡热量");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath(FileUtil.FILE_PATH+"/"+screenName);//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl(HttpUtil.DISPLAY_RECORD+record.getId());
        // comment是我对这条分享的评论，仅在人人网使用
        //oks.setComment("我是测试评论文本");
        //oks.setImageData(mapBitmap);
        // 启动分享GUI
        oks.show(this);
    }
    private Bitmap convertViewToBitmap(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }
    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        FileUtil.judePackageDirExists();
        File dir = new File(FileUtil.FILE_PATH);
//+System.currentTimeMillis()
        screenName = "map_track"+ ".jpg";
        File file = new File(dir, screenName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            handler.sendEmptyMessage(0x04);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), screenName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
}
