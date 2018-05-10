package com.example.dzj.mogemap.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by dzj on 2018/2/23.
 */

public class BindAccoutBroadcastReciver extends BroadcastReceiver {
    public static final String BIND_ACCOUT = "bindaccoutreciver";
    private BindAccoutListener listener;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BIND_ACCOUT)){
            String s = intent.getStringExtra("type");
            if (listener != null){
                listener.onUpdate(s);
            }
        }
    }
    public interface BindAccoutListener{
        void onUpdate(String s);
    }
    public void setListener(BindAccoutListener listener){
        this.listener = listener;
    }
}
