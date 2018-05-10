package com.example.dzj.mogemap.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.adapter.MyFragmentPagerAdapter;
import com.example.dzj.mogemap.fragment.MineManagerFragment;
import com.example.dzj.mogemap.fragment.ShouyeManagerFragment;
import com.example.dzj.mogemap.fragment.WeatherManagerFragment;
import com.example.dzj.mogemap.modle.Mogemap_user;
import com.example.dzj.mogemap.utils.BitmapUtil;
import com.example.dzj.mogemap.utils.Config;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.MyPrefs;
import com.example.dzj.mogemap.utils.SystemUtils;
import com.example.dzj.mogemap.utils.UserManager;
import com.example.dzj.mogemap.weather.recylerview.hRecyclerViewAdapter;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.tauth.Tencent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, hRecyclerViewAdapter.SaveEditListener {
    private final static String TAG = "MainActivity";
    private ViewPager viewpager;
    private MyFragmentPagerAdapter adapter;
    private LinearLayout bottom;
    private TextView shouye,weather,mine;
    private ImageView cursor;
    float cursorX=0;
    private int[] widthArgs;
    private TextView[] btnArgs;
    private int[] textViewId = new int[]{R.id.btn_shouye, R.id.btn_weather, R.id.btn_mine};
    private ArrayList<Fragment> fragments;
    //android6.0需要使用的权限声明
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    public static Tencent mTencent ;
    public static Oauth2AccessToken mAccessToken;
    public static String edit_text=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTencent = Tencent.createInstance(Config.QQ_APPID, getApplicationContext());
        MyPrefs.getInstance().initSharedPreferences(this);
        getPersimmions();
        init();
        initLoginType();
        BitmapUtil.init();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        MyPrefs.getInstance().onDestory();
        mTencent = null;
        mAccessToken = null;
    }
    private void init(){
        viewpager = (ViewPager)findViewById(R.id.myviewpager);
        bottom = (LinearLayout)findViewById(R.id.bottomlinear);
        shouye = (TextView)findViewById(R.id.btn_shouye);
        weather = (TextView)findViewById(R.id.btn_weather);
        mine = (TextView)findViewById(R.id.btn_mine);

        btnArgs=new TextView[]{shouye,weather,mine};
        cursor=(ImageView)findViewById(R.id.cursor_btn);
        if(Build.VERSION.SDK_INT >= 23){
            cursor.setBackgroundColor(getColor(R.color.text_choose));
        }else {
            cursor.setBackgroundColor(getResources().getColor(R.color.text_choose));
        }


        shouye.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
                lp.width = shouye.getWidth()-shouye.getPaddingLeft()*2;
                cursor.setLayoutParams(lp);
                cursor.setX(shouye.getPaddingLeft());
            }
        });
        shouye.setOnClickListener(this);
        weather.setOnClickListener(this);
        mine.setOnClickListener(this);
        viewpager.addOnPageChangeListener(this);

        fragments = new ArrayList<>();
        fragments.add(new ShouyeManagerFragment());
        fragments.add(new WeatherManagerFragment());
        fragments.add(new MineManagerFragment());

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);

        bottom.post(new Runnable() {
            @Override
            public void run() {
                SystemUtils.getSystemDisplay(MainActivity.this);
                SystemUtils.HEIGHT -=bottom.getHeight();
                Log.d("myheight", SystemUtils.HEIGHT+"");
            }
        });
    }
    private int lastValue = -1;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(positionOffset!=0){
            int nowWidth=cursor.getWidth();
            if(lastValue>=positionOffsetPixels){
                float offset=nowWidth*positionOffset-nowWidth;
                cursorSlide(position+1,offset);
                setTabTextColor(position);
            }else if(lastValue<positionOffsetPixels){
                float offset=nowWidth*positionOffset;
                cursorSlide(position,offset);
                setTabTextColor(position+1);
                //setTabTextColor(position-1);
            }
        }
        lastValue=positionOffsetPixels;
    }

    @Override
    public void onPageSelected(int position) {
        if(widthArgs == null){
            widthArgs = new int[]{shouye.getWidth(), weather.getWidth(), mine.getWidth()};
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void cursorAnim(int curItem){
        cursorX=0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        lp.width=widthArgs[curItem]-btnArgs[0].getPaddingLeft()*2;
        cursor.setLayoutParams(lp);
        for(int i=0;i<curItem;i++){
            cursorX=cursorX+btnArgs[i].getWidth();
        }
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
        //setTabTextColor(curItem);
    }
    public void cursorSlide(int position,float offset){
        float mX=0;
        for(int i=0;i<position;i++){
            mX=mX+btnArgs[i].getWidth();
        }
        if(offset>0){
            cursor.setX(mX+btnArgs[position].getPaddingLeft()*3+offset);
        }else {
            cursor.setX(mX-btnArgs[position].getPaddingLeft()+offset);
        }
        print("paddindleft="+btnArgs[position].getPaddingLeft());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_shouye:
                viewpager.setCurrentItem(0);
                cursorAnim(0);
                break;
            case R.id.btn_weather:
                viewpager.setCurrentItem(1);
                cursorAnim(1);
                break;
            case R.id.btn_mine:
                viewpager.setCurrentItem(2);
                cursorAnim(2);
                break;
        }
    }
    private void print(String msg){
        Log.i(TAG,msg);
    }
    //权限相关
    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.SEND_SMS);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void setTabTextColor(int position){
        for(int i = 0; i < textViewId.length; i++){
            if(position == i){
                if(Build.VERSION.SDK_INT >= 23){
                    btnArgs[i].setTextColor(getColor(R.color.text_choose));
                }else {
                    btnArgs[i].setTextColor(getResources().getColor(R.color.text_choose));
                }

            }else {
                if(Build.VERSION.SDK_INT >= 23){
                    btnArgs[i].setTextColor(getColor(R.color.black));
                }else {
                    btnArgs[i].setTextColor(getResources().getColor(R.color.black));
                }

            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        adapter.getItem(2).onActivityResult(requestCode, resultCode, data);
        adapter.getItem(1).onActivityResult(requestCode, resultCode, data);
    }
    private void initLoginType() {
        String url;
        if (MyPrefs.getInstance().readString(MyPrefs.QQ_OPEN_ID, 0) != null &&
                !MyPrefs.getInstance().readString(MyPrefs.QQ_OPEN_ID, 0).equals("")) {
            String token = MyPrefs.getInstance().readString(MyPrefs.QQ_ACCESS_TOKEN, 0);
            String expires = MyPrefs.getInstance().readString(MyPrefs.QQ_EXPIRES_IN, 0);
            String openId = MyPrefs.getInstance().readString(MyPrefs.QQ_OPEN_ID, 0);
            Log.d("sdasdasd",token+"  "+expires+"  "+openId);
            mTencent.setOpenId(openId);
            mTencent.setAccessToken(token, expires);
            url = HttpUtil.GET_USER_BY_QQ_URL+"/"+openId;
            Log.d(TAG, "initLoginType: "+url);
            getUser(url);
        }else if (MyPrefs.getInstance().readString(MyPrefs.SINA_UID, 1) != null &&
                !MyPrefs.getInstance().readString(MyPrefs.SINA_UID, 1).equals("")) {
            String uid = MyPrefs.getInstance().readString(MyPrefs.SINA_UID, 1);
            mAccessToken = AccessTokenKeeper.readAccessToken(this);
            url = HttpUtil.GET_USER_BY_WEIBO_URL+"/"+uid;
            Log.d(TAG, "initLoginType: "+url);
            getUser(url);
        }
    }
    private void getUser(String url){
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //tip("请求失败");
                        Log.d("response:",e.toString()+"  call="+call.toString()+" id="+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);
                        Mogemap_user user = JSON.parseObject(response, Mogemap_user.class);
                        UserManager.getInstance().setUser(user);
//                        Intent intent = new Intent();
//                        intent.setAction(ShouyeBroadcastReciver.SHOU_YE);
//                        sendBroadcast(intent);
                    }
                });
    }

    @Override
    public void SaveEdit(int position, String string) {
        if(position==0){
            edit_text=string;
        }
    }
}
