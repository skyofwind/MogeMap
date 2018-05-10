package com.example.dzj.mogemap.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by dzj on 2018/3/5.
 */

public class ShouyeBroadcastReciver extends BroadcastReceiver {
    public static final String SHOU_YE = "ShouyeBroadcastReciver";
    private UpdateShouyeListener updateShouyeListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SHOU_YE)){
            if (updateShouyeListener != null){
                updateShouyeListener.onUpdate();
            }
        }
    }

    public void setUpdateShouyeListener(UpdateShouyeListener updateShouyeListener) {
        this.updateShouyeListener = updateShouyeListener;
    }

    public interface UpdateShouyeListener{
        void onUpdate();
    }
}
