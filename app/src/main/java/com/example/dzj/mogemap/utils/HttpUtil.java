package com.example.dzj.mogemap.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.dzj.mogemap.service.RunService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by dzj on 2018/2/21.
 */

public class HttpUtil {
    public static final String ROOT_URL = "http://39.108.80.212/studyplatform/mogemap/";
    public static final String CHECK_PHONE_URL = ROOT_URL+"checkPhone";
    public static final String ADD_USER_URL = ROOT_URL+"addUser";
    public static final String GET_USER_BY_PHONE_URL = ROOT_URL+"getUserPhone";
    public static final String GET_USER_BY_QQ_URL = ROOT_URL+"getUserQQ";
    public static final String GET_USER_BY_WEIBO_URL = ROOT_URL+"getUserWeibo";
    public static final String ADD_FRIEND = ROOT_URL+"user/";
    public static  final String GET_FRIENDS = ROOT_URL+"getFriends/";
    public static final String ADD_RECORD = ROOT_URL+"addRunRecord";
    public static final String GET_RECORD = ROOT_URL+"getRecord/";
    public static final String GET_RECORDS = ROOT_URL+"getRecords/";
    public static final String GET_RECORDS_DAY = ROOT_URL+"getRecordsByDay/";
    public static final String GET_RECORDS_WEEK = ROOT_URL+"getRecordsByWeek/";
    public static final String GET_RECORDS_MONTH = ROOT_URL+"getRecordsByMonth/";
    public static final String GET_PK_DAY = ROOT_URL+"day/";//{mPhone}/PK/{fPhone}
    public static final String GET_PK_WEEK = ROOT_URL+"week/";//{mPhone}/PK/{fPhone}
    public static final String GET_PK_MONTH = ROOT_URL+"month/";//{mPhone}/PK/{fPhone}
    public static final String GET_LEADERBOARDS = ROOT_URL+"leaderboards/";//{mPhone}/{mType}
    public static final String GET_SEVEN = ROOT_URL+"getSevenRecord/";//{phone}
    public static final String UPDATE_USER = ROOT_URL+"updateUser";
    public static final String DISPLAY_RECORD = ROOT_URL+"displayRecord/";
    public static final String DELETE_FRIEND = ROOT_URL+"deleteFriend/";

    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    public static void postRunRecord(final Context context, String json, String url){
        OkHttpUtils
                .postString()
                .url(url)
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("post", " call="+call.toString()+" e="+e.toString()+" id="+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("post", "response"+response.toString()+" id="+id);
                        Intent intent1 = new Intent(context, RunService.class);
                        intent1.putExtra("type", "pause");
                        context.startService(intent1);
                    }
                });
    }
}
