package com.example.dzj.mogemap.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.adapter.MyFragmentPagerAdapter;
import com.example.dzj.mogemap.fragment.MineManagerFragment;
import com.example.dzj.mogemap.fragment.ShouyeManagerFragment;
import com.example.dzj.mogemap.fragment.WeatherManagerFragment;
import com.example.dzj.mogemap.utils.SystemUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private final static String TAG = "MainActivity";
    private ViewPager viewpager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPersimmions();
        init();
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
    private void init(){
        viewpager = (ViewPager)findViewById(R.id.myviewpager);
        bottom = (LinearLayout)findViewById(R.id.bottomlinear);
        shouye = (TextView)findViewById(R.id.btn_shouye);
        weather = (TextView)findViewById(R.id.btn_weather);
        mine = (TextView)findViewById(R.id.btn_mine);

        btnArgs=new TextView[]{shouye,weather,mine};
        cursor=(ImageView)findViewById(R.id.cursor_btn);
        cursor.setBackgroundColor(getColor(R.color.text_choose));

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

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
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
                btnArgs[i].setTextColor(getColor(R.color.text_choose));
            }else {
                btnArgs[i].setTextColor(getColor(R.color.black));
            }
        }

    }
}
