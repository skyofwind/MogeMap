package com.example.dzj.mogemap.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dzj on 2018/2/21.
 */

public class MyPrefs {
    private static final String PREF_NAME = "qq_tencent";
    private static final String SINA_NAME = "sina_weibo";
    public static final String QQ_OPEN_ID = "openid";
    public static final String QQ_ACCESS_TOKEN = "access_token";
    public static final String QQ_EXPIRES_IN = "expires_in";
    public static final String SINA_EXPIRES_IN = "expires_in";
    public static final String SINA_UID = "uid";
    public static final String SINA_ACCESS_TOKEN = "access_token";;
    private static MyPrefs myPrefs;
    private SharedPreferences sp, sps;
    private MyPrefs(){}
    public static MyPrefs getInstance(){
        if(myPrefs == null){
            myPrefs = new MyPrefs();
        }
        return myPrefs;
    }
    public MyPrefs initSharedPreferences(Context context){
        //获取SharedPreferences对象
        if(sp == null){
            sp = context.getSharedPreferences(PREF_NAME,
                    Context.MODE_PRIVATE);
        }
        if(sps == null){
            sps = context.getSharedPreferences(SINA_NAME,
                    Context.MODE_PRIVATE);
        }
        return myPrefs;
    }
    /**
     * 向SharedPreferences中写入String类型的数据
     * @param key
     * @param value
     */
    public void writeString(String key, String value, int num){
        SharedPreferences.Editor editor;
        switch (num){
            case 0:
                //获取编辑器对象
                editor = sp.edit();
                //写入数据
                editor.putString(key, value);
                editor.commit();//提交写入的数据
                break;
            case 1:
                //获取编辑器对象
                editor = sps.edit();
                //写入数据
                editor.putString(key, value);
                editor.commit();//提交写入的数据
                break;
        }

    }

    /**
     * 根据key读取SharedPreferences中的String类型的数据
     * @param key
     * @return
     */
    public String readString(String key, int num){
        switch (num){
            case 0:
                return sp.getString(key, "");
            case 1:
                return sps.getString(key, "");
        }
        return sp.getString(key, "");
    }
    public void onDestory(){
        if (sp != null){
            sp = null;
        }
        if(sps != null){
            sps = null;
        }
    }
}
