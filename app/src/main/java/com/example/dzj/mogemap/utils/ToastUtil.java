package com.example.dzj.mogemap.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dzj on 2018/3/1.
 */

public class ToastUtil {
    public static void tip(Context context, String s, int t){
        Toast.makeText(context, s, t).show();
    }
}
