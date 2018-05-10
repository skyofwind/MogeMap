package com.example.dzj.mogemap.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.AllRecordsActivity;
import com.example.dzj.mogemap.activity.RunActivity;
import com.example.dzj.mogemap.modle.Mogemap_run_record;
import com.example.dzj.mogemap.modle.RunRecords;
import com.example.dzj.mogemap.reciver.ShouyeBroadcastReciver;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.OtherUtil;
import com.example.dzj.mogemap.utils.UserManager;
import com.example.dzj.mogemap.view.RainbowView;
import com.example.dzj.mogemap.view.RunRecordView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by dzj on 2017/12/14.
 */

public class ShouyeManagerFragment extends Fragment {

    private View rootView;
    private RainbowView rainbowView;
    private RunRecordView runRecordView;
    private TextView distanceCount, timeCount;
    private Button button;
    private LinearLayout allRecord;
    private List<Mogemap_run_record> records = new ArrayList<>();
    private int times = 0;
    private double distance = 0;
    private ShouyeBroadcastReciver shouyeBroadcastReciver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView=inflater.inflate(R.layout.shouye_manager,null);
        registerReciver();
        //rainbowView = (RainbowView)rootView.findViewById(R.id.rainbow);
        //rainbowView.setInstensityTime(5);
        //rainbowView.setStepCount(1002);
        initView();

        return rootView;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0x01:
                    setDate();
                    break;
            }
        }
    };
    @Override
    public void onResume(){
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDate();
            }
        }, 1000);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReciver();
        //用完回调要注销掉，否则可能会出现内存泄露
    }
    private void initView(){
        //runRecordView = new RunRecordView(getContext());
        runRecordView = (RunRecordView)rootView.findViewById(R.id.runview);
        distanceCount = (TextView)rootView.findViewById(R.id.distance_count);
        timeCount = (TextView)rootView.findViewById(R.id.time_count);
        button = (Button) rootView.findViewById(R.id.startRun);
        allRecord = (LinearLayout)rootView.findViewById(R.id.all_record);
        allRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllRecordsActivity.class));
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RunActivity.class));
            }
        });
    };
    private void getDate(){
        if(UserManager.getInstance().getUser().getPhone().equals("")){
            log("没有发送");
        }else {
            log("发送了请求");

            String phone = UserManager.getInstance().getUser().getPhone();
            log(HttpUtil.GET_RECORDS+phone);
            OkHttpUtils
                    .get()
                    .url(HttpUtil.GET_RECORDS+phone)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            log("首页"+ e.toString()+"失败");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            RunRecords runRecords = JSON.parseObject(response, RunRecords.class);
                            records = runRecords.getRecords();
                            log("首页"+ response+"成功");
                            handler.sendEmptyMessage(0x01);
                        }
                    });
        }
    }
    private void setDate(){
        times = records.size();
        distance = 0;
        for(Mogemap_run_record record: records){
            distance +=record.getDistance();
        }
        distanceCount.setText(OtherUtil.getKM(distance));
        timeCount.setText(times+"");

        List<Mogemap_run_record> mogemap_run_records = new ArrayList<>();
        for (int i = records.size()-1; i >= 0;i--){
            if((records.size() -i) <= 7){
                mogemap_run_records.add(records.get(i));
            }
        }
        if(mogemap_run_records != null){
            runRecordView.setRecords(mogemap_run_records);
        }
    }
    private void log(String s){
        Log.d("ShouyeManagerFragment", s);
    }
    private void registerReciver(){
        if(null == shouyeBroadcastReciver){
            IntentFilter filter = new IntentFilter();
            filter.addAction(ShouyeBroadcastReciver.SHOU_YE);
            shouyeBroadcastReciver = new ShouyeBroadcastReciver();
            shouyeBroadcastReciver.setUpdateShouyeListener(new ShouyeBroadcastReciver.UpdateShouyeListener() {
                @Override
                public void onUpdate() {
                    getDate();
                }
            });
            getActivity().registerReceiver(shouyeBroadcastReciver, filter);
        }
    }
    private void unRegisterReciver(){
        if(shouyeBroadcastReciver != null){
            getActivity().unregisterReceiver(shouyeBroadcastReciver);
            shouyeBroadcastReciver = null;
        }
    }
}
