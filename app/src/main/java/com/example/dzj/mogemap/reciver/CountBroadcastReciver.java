package com.example.dzj.mogemap.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dzj.mogemap.activity.RunActivity;

import static com.example.dzj.mogemap.utils.StepDetection.COUNT_BROADCASTRECEIVER;

/**
 * Created by dzj on 2017/11/7.
 */

public class CountBroadcastReciver extends BroadcastReceiver {
    private RunActivity.CountListener countListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(COUNT_BROADCASTRECEIVER)){
            int count = intent.getIntExtra(COUNT_BROADCASTRECEIVER,0);
            if(null != countListener){
                countListener.setCount(count);
            }
        }
    }

    public void setCountListener(RunActivity.CountListener listener){
        this.countListener = listener;
    }
}
