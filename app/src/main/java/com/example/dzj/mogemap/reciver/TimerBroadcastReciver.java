package com.example.dzj.mogemap.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dzj.mogemap.activity.RunActivity;
import com.example.dzj.mogemap.service.RunService;

/**
 * Created by dzj on 2018/2/19.
 */

public class TimerBroadcastReciver extends BroadcastReceiver{
    public static String TIMER_NAME = "mytimerreciver";
    private TimerListener timerListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        //if(intent.getAction().equals(TIMER_NAME)){
        Log.d("RunService","执行1");
            if(timerListener != null){
                timerListener.onChange();
                Log.d("RunService","执行");
                Intent intent2 = new Intent(context, RunService.class);
                context.startService(intent2);
            }
        //}
    }
    public interface TimerListener{
        void onChange();
    }
    public void setTimerListener(TimerListener timerListener){
        this.timerListener = timerListener;
    }
}
