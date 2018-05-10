package com.example.dzj.mogemap.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.adapter.RecordCountAdapter;
import com.example.dzj.mogemap.modle.Mogemap_run_record;
import com.example.dzj.mogemap.modle.RunRecords;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.OtherUtil;
import com.example.dzj.mogemap.utils.ToastUtil;
import com.example.dzj.mogemap.utils.UserManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by dzj on 2018/2/28.
 */

public class WeekCountActivity extends BaseActivty {
    private final static String TAG = "WeekCountActivity";
    private List<Mogemap_run_record> records = new ArrayList<>();
    private TextView distanceCount, timeCount;
    private ListView list;
    private RecordCountAdapter adapter;

    private int times = 0;
    private double distance = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_count_layout);
        initView();
        setMyTitle();
        getData();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0x01:
                    setDate();
                    break;
                case 0x02:
                    statrProgressDialog();
                    break;
                case 0x03:
                    cancel();
                    break;
            }
        }
    };
    @Override
    protected void onResume(){
        super.onResume();

    }
    private void setMyTitle(){
        initTitle();
        setTitle("周统计");
        setIconListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView(){
        distanceCount = (TextView)findViewById(R.id.distance_count);
        timeCount = (TextView)findViewById(R.id.time_count);
        list = (ListView)findViewById(R.id.list);
    }
    private void getData(){
        handler.sendEmptyMessage(0x02);
        if (!UserManager.getInstance().getUser().getPhone().equals("")){
            OkHttpUtils
                    .get()
                    .url(HttpUtil.GET_RECORDS_WEEK+ UserManager.getInstance().getUser().getPhone())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            log(e.toString());
                            handler.sendEmptyMessage(0x03);
                            ToastUtil.tip(WeekCountActivity.this, "请求出错", 1);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            log(response);
                            RunRecords runRecords = JSON.parseObject(response, RunRecords.class);
                            records = runRecords.getRecords();
                            handler.sendEmptyMessage(0x01);
                            handler.sendEmptyMessage(0x03);
                        }
                    });
        }

    }
    private void setDate(){
        times = records.size();
        for(Mogemap_run_record record: records){
            distance +=record.getDistance();
        }
        distanceCount.setText(OtherUtil.getKM(distance));
        timeCount.setText(times+"");

        adapter = new RecordCountAdapter(this, records);
        list.setAdapter(adapter);
    }
    private void log(String s){
        Log.d(TAG, s);
    }
}
