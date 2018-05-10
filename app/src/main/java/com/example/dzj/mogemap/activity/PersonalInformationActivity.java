package com.example.dzj.mogemap.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.dialog.picker.DataPickerDialog;
import com.example.dzj.mogemap.dialog.picker.DatePickerDialog;
import com.example.dzj.mogemap.fragment.SexDialogFragment;
import com.example.dzj.mogemap.modle.Mogemap_user;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.ToastUtil;
import com.example.dzj.mogemap.utils.UserManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by dzj on 2018/3/8.
 */

public class PersonalInformationActivity extends BaseActivty {
    private final static String TAG = "PersonalInformation";
    private LinearLayout sexItem, birthdayItem, heightItem, weightItem;
    TextView sex, birthday, height, weight;
    private List<String> heights, weights;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_information_layout);
        initView();
        initData();
        setMyTitle();
        getData();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0x01:
                    updateUI();
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
        setTitle("个人信息");
        setIconListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initData(){
        heights = new ArrayList<>();
        weights = new ArrayList<>();
        for(int i = 50; i <= 250; i++){
            heights.add(i+"");
        }
        for(int i = 10; i <= 250; i++){
            weights.add(i+"");
        }
    }
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sex_item:
                    final SexDialogFragment sexDialogFragment = new SexDialogFragment();
                    sexDialogFragment.show(getFragmentManager(), "sexDialogFragment");
                    sexDialogFragment.setKeep(new SexDialogFragment.OnDialogListener() {
                        @Override
                        public void onDialogClick() {
                            if(sexDialogFragment.getChoose() == 0){
                                UserManager.getInstance().getUser().setSex("男");
                            }else {
                                UserManager.getInstance().getUser().setSex("女");
                            }
                            sendUpdate(getJson(UserManager.getInstance().getUser()));
                        }
                    });
                    break;
                case R.id.birthday_item:
                    showDialogDate();
                    break;
                case R.id.height_item:
                    showHeightDialog();
                    break;
                case R.id.weight_item:
                    showWeightDialog();
                    break;
            }
        }
    };
    private void initView(){
        sexItem = (LinearLayout)findViewById(R.id.sex_item);
        birthdayItem = (LinearLayout)findViewById(R.id.birthday_item);
        heightItem = (LinearLayout)findViewById(R.id.height_item);
        weightItem = (LinearLayout)findViewById(R.id.weight_item);

        sexItem.setOnClickListener(listener);
        birthdayItem.setOnClickListener(listener);
        heightItem.setOnClickListener(listener);
        weightItem.setOnClickListener(listener);

        sex = (TextView)findViewById(R.id.sex);
        birthday = (TextView)findViewById(R.id.birthday);
        height = (TextView)findViewById(R.id.height);
        weight = (TextView)findViewById(R.id.weight);
    }
    private void getData(){
        if (!UserManager.getInstance().getUser().getPhone().equals("")){
            updateUI();
        }else {
            ToastUtil.tip(this, "用户未登录", 1);
        }
    }
    private void updateUI(){
        Mogemap_user user = UserManager.getInstance().getUser();
        if(!user.getSex().equals("无")){
            sex.setText(user.getSex());
        }
        if (!(user.getBirthday() == null)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getBirthday());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String str = year+"/"+month+"/"+day;
            birthday.setText(str);
        }
        if(user.getHeight() != 0){
            height.setText(user.getHeight()+"厘米");
        }
        if (user.getWeight() != 0){
            weight.setText(user.getWeight()+"公斤");
        }
    }
    private void setDate(){

    }

    private final void showDialogDate() {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        if(UserManager.getInstance().getUser().getBirthday() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(UserManager.getInstance().getUser().getBirthday());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int[] datas = new int[]{year, month, day};
            builder.setChoose(datas);
        }
        DatePickerDialog dialog = builder.setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int[] dates) {
                //Toast.makeText(getApplicationContext(), dates[0] + "#" + dates[1] + "#" + dates[2], Toast.LENGTH_SHORT).show();
                String str = dates[0]+""+dates[1]+""+dates[2]+"";
                log(str);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMd");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(date != null){
                    log(date.toString());
                    UserManager.getInstance().getUser().setBirthday(date);
                    sendUpdate(getJson(UserManager.getInstance().getUser()));
                }else {

                }
            }
        }).create();

        dialog.show();
    }
    private final void showHeightDialog() {
        DataPickerDialog.Builder builder = new DataPickerDialog.Builder(this);
        if(UserManager.getInstance().getUser().getHeight() != 0){
            builder.setMyWeight(UserManager.getInstance().getUser().getHeight()+"");
        }
        DataPickerDialog dialog = builder.setUnit("厘米").setData(heights).setSelection(1).setTitle("身高")
                .setOnDataSelectedListener(new DataPickerDialog.OnDataSelectedListener() {
                    @Override
                    public void onDataSelected(String itemValue) {
                        //Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_SHORT).show();
                        int value = Integer.parseInt(itemValue);
                        UserManager.getInstance().getUser().setHeight(value);
                        sendUpdate(getJson(UserManager.getInstance().getUser()));
                    }
                }).create();

        dialog.show();
    }
    private String getJson(Mogemap_user user){
        String json = JSON.toJSONStringWithDateFormat(user, "yyyy-MM-dd HH:mm:ss");
        Mogemap_user myuser = JSON.parseObject(json, Mogemap_user.class);
        json = JSON.toJSONString(myuser);
        log(json);
        return json;
    }
    private final void showWeightDialog() {
        DataPickerDialog.Builder builder = new DataPickerDialog.Builder(this);
        if(UserManager.getInstance().getUser().getWeight() != 0){
            builder.setMyWeight(UserManager.getInstance().getUser().getWeight()+"");
        }
        DataPickerDialog dialog = builder.setUnit("公斤").setData(weights).setSelection(1).setTitle("体重")
                .setOnDataSelectedListener(new DataPickerDialog.OnDataSelectedListener() {
                    @Override
                    public void onDataSelected(String itemValue) {
                        //Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_SHORT).show();
                        int value = Integer.parseInt(itemValue);
                        UserManager.getInstance().getUser().setWeight(value);
                        sendUpdate(getJson(UserManager.getInstance().getUser()));
                    }
                }).create();

        dialog.show();
    }
    private void sendUpdate(String json){
        handler.sendEmptyMessage(0x02);
        OkHttpUtils
                .postString()
                .url(HttpUtil.UPDATE_USER)
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        log(e.toString());
                        ToastUtil.tip(PersonalInformationActivity.this, "请求失败", 1);
                        handler.sendEmptyMessage(0x03);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log(response);
                        handler.sendEmptyMessage(0x01);
                        handler.sendEmptyMessage(0x03);
                    }
                });
    }
    private void log(String s){
        Log.d(TAG, s);
    }
}
