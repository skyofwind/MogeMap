package com.example.dzj.mogemap.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.modle.Mogemap_user;
import com.example.dzj.mogemap.reciver.BindAccoutBroadcastReciver;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.UserManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by dzj on 2018/2/22.
 */

public class SubmitCodeActivity extends BaseActivty {

    private static final String TAG = "SubmitCodeActivity";
    private TextView userPhone;
    private EditText subCode;
    private TextView submit;
    private LinearLayout loadingTime;
    private TextView time;
    private TextView reget;
    private Timer timer;
    private int t = 60;
    private boolean btn = false;
    private Mogemap_user myUser;

    private String name, type, head, country, phone, id;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x00:
                    loadingTime.setVisibility(View.GONE);
                    reget.setVisibility(View.VISIBLE);
                    break;
                case 0x01:
                    time.setText(t+"");
                    break;
                case 0x03:
                    UserManager.getInstance().setUser(myUser);
                    Intent intent = new Intent(SubmitCodeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    Intent intent1 = new Intent();
                    intent1.setAction(BindAccoutBroadcastReciver.BIND_ACCOUT);
                    intent1.putExtra("type", type);
                    sendBroadcast(intent1);
                    break;
                case 0x04:
                    statrProgressDialog();
                    break;
                case 0x05:
                    cancel();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_code_layout);

        userPhone = (TextView)findViewById(R.id.user_phone);
        subCode = (EditText)findViewById(R.id.sub_code);
        submit = (TextView)findViewById(R.id.submit);
        loadingTime = (LinearLayout)findViewById(R.id.loading_time);
        time = (TextView)findViewById(R.id.time);
        reget = (TextView)findViewById(R.id.reget);
        setMyTitle();
        //icon.setImageDrawable(getDrawable(R.drawable.back2));

        subCode.addTextChangedListener(new TextChange());
        reget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode(country, phone);
                t = 60;
                reget.setVisibility(View.GONE);
                loadingTime.setVisibility(View.VISIBLE);
                initTimer();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn){
                    mHandler.sendEmptyMessage(0x04);
                    submitCode(country, phone, subCode.getText().toString());
                }
            }
        });
        getData();
    }
    private void setMyTitle(){
        initTitle();
        setTitle("");
        setIconListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        SMSSDK.unregisterAllEventHandler();
    }
    private void getData(){
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        head = getIntent().getStringExtra("head");
        id = getIntent().getStringExtra("id");
        country = getIntent().getStringExtra("country");
        phone = getIntent().getStringExtra("phone");
        Log.i("data",name+" "+type+" "+head+" "+country+" "+phone);
        userPhone.setText("+"+country+phone);
        initTimer();

    }
    private void initTimer(){
        if(timer == null){
            timer = new Timer();
            timer.schedule(new MyTimerTask(), 1000, 1000);
        }
    }
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            t--;
            mHandler.sendEmptyMessage(0x01);
            if(t == 0){
                if(timer != null){
                    timer.cancel();
                    timer = null;
                    mHandler.sendEmptyMessage(0x00);
                }
            }
        }
    }
    private void sendCode(final String country, final String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                //org.json.JSONObject jsonObject = new org.json.JSONObject(o.toString());
                Log.d(TAG+"mysms"," event="+event+" result="+result+" data"+data.toString());
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                    Log.d(TAG+"mysms","sendCode+成功");
                } else{
                    // TODO 处理错误的结果
                    Log.d(TAG+"mysms","sendCode+失败");
                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Log.d(TAG+"mysms"," event="+event+" result="+result+" data"+data.toString());
                mHandler.sendEmptyMessage(0x05);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    Log.d(TAG+"mysms","submitCode成功");
                    tip("验证成功");
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                    }
                    addUser();
                } else{
                    // TODO 处理错误的结果
                    Log.d(TAG+"mysms","submitCode失败");
                    tip("验证码有误");
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }
    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(subCode.getText().toString().length() == 4){
                changeBtn(true);
            }else {
                changeBtn(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }
    private void changeBtn(boolean type){
        btn = type;
        if (type){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                submit.setBackground(getDrawable(R.drawable.button_on));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                submit.setBackground(getDrawable(R.drawable.button_off));
            }
        }
    }
    private void tip(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    private void addUser(){
        final Mogemap_user user = new Mogemap_user();
        if(type.equals("qq")){
            user.setQqid(id);
        }else {
            user.setWeiboid(id);
        }
        user.setHeadurl(head);
        user.setPhone(phone);
        user.setName(name);
        myUser = user;
        OkHttpUtils
                .postString()
                .url(HttpUtil.ADD_USER_URL)
                .content(new Gson().toJson(user))
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
                        mHandler.sendEmptyMessage(0x03);

                    }
                });
    }
}
