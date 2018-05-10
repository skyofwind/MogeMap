package com.example.dzj.mogemap.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dzj.mogemap.reciver.TimerBroadcastReciver;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dzj on 2018/2/19.
 */

public class RunService extends Service {
    public static final String TAG="RunService";
    private Timer timer;
    public static int time = 0, minute = 0, second = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        time = 0;
        minute = 0;
        second = 0;
        Log.w(TAG,"in onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "in onStartCommand");
        if(timer == null){
            timer = new Timer();
            timer.schedule(new MyTimerTask(), 1000, 1000);
        }
        if(intent!=null){
            if(intent.getStringExtra("type") != null){
                String str = intent.getStringExtra("type");
                if(str.equals("pause")){
                    Log.d("pause", "开始暂停");
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                        Log.d("pause", "暂停了");
                    }
                }else if(str.equals("stop")){
                    onDestroy();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "in onDestroy");
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        time = 0;
        minute = 0;
        second = 0;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind");
        super.onRebind(intent);
    }
    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            Intent i = new Intent();
            i.setAction(TimerBroadcastReciver.TIMER_NAME);
            dealTime();
            sendBroadcast(i);
        }
    }
    private void dealTime(){
        second++;
        if(second >= 60){
            second = 0;
            minute++;
        }
        if(minute >= 60){
            minute = 0;
            time++;
        }
        //log("strs="+time+" "+minute+" "+second);
    }
}
