package com.example.dzj.mogemap.weather.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.weather.main_menu.DB_code;
import com.example.dzj.mogemap.weather.main_menu.DBmanager;
import com.example.dzj.mogemap.weather.main_menu.MainmenuActivity;
import com.example.dzj.mogemap.weather.main_menu.bean.MenuData;
import com.example.dzj.mogemap.weather.recylerview.DividerItemDecoration;
import com.example.dzj.mogemap.weather.recylerview.RecyclerViewAdapter;
import com.example.dzj.mogemap.weather.recylerview.hRecyclerViewAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.example.dzj.mogemap.weather.main.DataDeal.bgpic;
import static com.example.dzj.mogemap.weather.main.DataDeal.mtmp;
import static com.example.dzj.mogemap.weather.main.DataDeal.mtxt;
import static com.example.dzj.mogemap.weather.main.DataDeal.mwind;
import static com.example.dzj.mogemap.weather.main.DataDeal.naqi;
import static com.example.dzj.mogemap.weather.main.DataDeal.ncity;
import static com.example.dzj.mogemap.weather.main.DataDeal.ntmp;
import static com.example.dzj.mogemap.weather.main.DataDeal.wc1;
import static com.example.dzj.mogemap.weather.main.DataDeal.wc2;


/**
 * Created by dzj on 2017/5/22.
 */

public class WeatherActivity extends AppCompatActivity implements hRecyclerViewAdapter.SaveEditListener{

    private PopupWindow window=null;
    //定位相关声明
    public static final String DYNAMICACTION = "BaiDuLocation";
    public static final String WINDOWCHANGE = "WINDOWCHANGE";
    public static final String RESULT="result";
    public static final String LOCATIONINFO="locationinfo";
    private DynamicBroadcastReceiver broadcastReceiver;
    private BaiDuLocation mLocation;

    //声明队列存储信息
    public LimitQueue_Repeat<String> queue_city_id;
    public LimitQueue<String> queue_city_name;
    public LimitQueue_Repeat<String> queue2;
    public LimitQueue<String> queue1,queue3;
    //SharedPreferences
    public static final String PREFS_NAME = "My";
    public static final String FIRST_RUN = "first";
    public static final String[] History_city_name={"name0","name1","name2","name3","name4","name5","name6","name7","name8","name9"};
    public static final String[] History_city_id={"id0","id1","id2","id3","id4","id5","id6","id7","id8","id9"};
    private boolean first,add_order=false;

    private DrawerLayout drawer;
    private ListView mLvLeftMenu;
    //数据库
    public static DBmanager dbHelper;
    public static DB_code dbcode;
    public static Cursor cursor;
    private static final int REQUEST_CODE = 100;//请求码
    public static final int REQUEST_CODE2 = 200;//请求码
    //定时器相关
    private Dialog progressDialog;
    private boolean  progress=false;
    //private Timer mTimer = null;
    //private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    //private static int delay = 1000;  //1s
    //private static int period = 1000;  //1s
    //android6.0需要使用的权限声明
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    //布局组件
    private RecyclerView mRecyclerView;
    public static RecyclerViewAdapter mAdapter;
    //popupwindow
    private RecyclerView hRecyclerView;
    public hRecyclerViewAdapter hAdapter;
    private List<String> history_city=new ArrayList<String>();

    private DisplayMetrics dm;
    private static boolean control=false;

    private String edit_text=null;

    private LocationInfo minfo;

    private SwipeRefreshLayout swipeRefreshLayout;

    //private DynamicWeatherView mDynamicWeatherView;

    private RelativeLayout relativeLayout;

    private static int Width;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout=(RelativeLayout)findViewById(R.id.main_body);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperRefresh);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        registerBroadcastReceiver();
        initLocation();
        queue_city_name= new LimitQueue<>(10);
        queue_city_id= new LimitQueue_Repeat<>(10);
        getPersimmions();
        InitGetui();
        history_deal();
        DataDeal.initData();
        statr_weather();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case  0x10:
                        startActivityForResult(new Intent(WeatherActivity.this,MainmenuActivity.class),REQUEST_CODE);
                        break;
                    case 0x12:
                        //stopTimer();
                        cancel();
                        tip("连接超时");
                        minfo.setStatus("连接超时");
                        break;
                    case 0x13:
                        //stopTimer();
                        cancel();
                        break;
                    case 0x14:
                        Error();
                        break;
                    case 0x15:
                        Reflash();
                        break;
                    case 0x16:
                        swipeRefresh();
                        break;
                    case 0x17:
                        setWeatherView(mtxt.get(0));
                        break;
                    case 0x18:
                        jugdeChange();
                        break;
                }
            }
        };
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterBroadcastReceiver();
        if(mLocation!=null&&mLocation.isLocationStart()){
            mLocation.Stop();
        }
//        if(play){
//            Voice.stopMusic();
//        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        registerBroadcastReceiver();

        /*if(minfo==null){
            if(mLocation==null){
                mLocation=new BaiDuLocation(this);
            }
            mLocation.Start();
        }*/
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onStop(){
        super.onStop();
//        if(play){
//            Voice.stopMusic();
//        }
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("voice", DataDeal.mvoice);
        if (first) {
            editor.putBoolean(FIRST_RUN, false);
        }
        queue1=queue_city_name.clone();
        queue2=queue_city_id.clone();
        int i=0;
        while(!queue1.isEmpty()){
            editor.putString(History_city_name[i],queue1.peek());
            editor.putString(History_city_id[i],queue2.peek());
            queue1.poll();
            queue2.poll();
            i++;
        }
        // Commit the edits!
        editor.apply();
    }
    @Override
    protected void onRestart(){
        super.onRestart();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        RemoveWeatherView();
    }
    //等待activity生成后获取屏幕高度
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!control){
            reflash();
            setMyAdapter();
            control=true;
            if(mtxt.size()>0){
                sendWindowChange();
            }
        }else{
            wc1=true;
        }
        mHandler.sendEmptyMessage(0x18);
    }
    //注册动态广播
    private void registerBroadcastReceiver(){
        if(broadcastReceiver==null){
            IntentFilter filter = new IntentFilter();
            filter.addAction(DYNAMICACTION);
            filter.addAction(WINDOWCHANGE);
            broadcastReceiver = new DynamicBroadcastReceiver();
            registerReceiver(broadcastReceiver, filter);
            broadcastReceiver.setGetLocation(new DynamicBroadcastReceiver.GetLocation() {
                @Override
                public void getLocation(LocationInfo info) {
                    minfo=new LocationInfo();
                    minfo.setDistrict(info.getDistrict());
                    minfo.setCity(info.getCity());
                    minfo.setProvince(info.getProvince());
                    distinguish(info);
                    mLocation=null;
                    Log.i("info",info.toString());
                }
            });
            broadcastReceiver.setWindowChange(new DynamicBroadcastReceiver.WindowChange() {
                @Override
                public void onChange() {
                    if(mtxt.size()>0){
                        sendMessage(0x17);
                        //Log.i("mtxt",DataDeal.mtxt.get(0));
                    }
                }
            });
        }
    }
    //注销动态广播
    private void unregisterBroadcastReceiver(){
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }
    //初始化百度定位组件
    private void initLocation(){
        mLocation=new BaiDuLocation(this);
        mLocation.Start();
        statrProgressDialog();
    }
    public void history_deal(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        first = settings.getBoolean(FIRST_RUN, true);
        String name,id;
        System.out.println(first);
        if(first){
            SharedPreferences.Editor editor = settings.edit();
            for(int i=0;i<10;i++){
                editor.putString(History_city_name[i],"");
                editor.putString(History_city_id[i],"");
                editor.putBoolean("voice",true);
            }
            editor.apply();
        }else{
            if(!add_order){
                for(int i=9;i>=0;i--){
                    name=settings.getString(History_city_name[i],"");
                    id=settings.getString(History_city_id[i],"");
                    if(!name.equals("")){
                        queue_city_name.offer(name);
                        queue_city_id.offer(id);
                    }
                }
                add_order=true;
            }
            DataDeal.mvoice=settings.getBoolean("voice",true);
        }
    }



    public void setHistory_list(){
        queue3=queue_city_name.clone();
        if(history_city!=null){
            history_city.clear();
        }
        List<String> str=new ArrayList<>();
        while(!queue3.isEmpty()){
            str.add(queue3.peek());
            queue3.poll();
            //System.out.println("队列长度："+queue3.size());
            //System.out.println("queue_city_name队列长度："+queue_city_name.size());
        }
        for(int i=str.size()-1;i>=0;i--){
            history_city.add(str.get(i));
        }
    }

    public void statr_weather(){
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.mipmap.icon2);
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //
        //drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mLvLeftMenu=(ListView)findViewById(R.id.left_slide_menu);
        ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.mipmap.icon2);
        ab.setDisplayHomeAsUpEnabled(true);

        setUpDrawer();
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //       this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        setDrawerLeftEdgeSize(this, drawer, 0.8f);
        //toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        startRecycleyvie();
    }
    //等待提示框及相关处理

    public void statrProgressDialog(){
        progressDialog = new Dialog(WeatherActivity.this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("正在加载中");
        progress=true;
        progressDialog.show();
    }
    public void cancel(){
        if(progress){
            progress=false;
            progressDialog.dismiss();
        }
    }
    public void sendMessage(int id){
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }

    public void startRecycleyvie(){
        //调用RecyclerView初始化
        mRecyclerView=(RecyclerView)findViewById(R.id.id_recyclerview);
        mAdapter=new RecyclerViewAdapter(this, DataDeal.mtime, mtmp, DataDeal.micon, mwind, mtxt, ntmp, ncity, DataDeal.height, naqi, DataDeal.ntrav, DataDeal.nflu, bgpic, DataDeal.bg_min, DataDeal.tcolor);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int tag) {
                LayoutInflater mInflater=mAdapter.getLayoutInflater();
                // TODO: 2016/5/17 构建一个popupwindow的布局
                View popupView = mInflater.inflate(R.layout.popupwindow, null);
                // TODO: 2016/5/17 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
                hRecyclerView=(RecyclerView)popupView.findViewById(R.id.history);
                initRecyclerview();
                initAdapter();
                // TODO: 2016/5/17 创建PopupWindow对象，指定宽度和高度
                window = new PopupWindow(popupView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                // TODO: 2016/5/17 设置动画
                window.setAnimationStyle(R.style.popup_window_anim);
                // TODO: 2016/5/17 设置背景颜色
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55000000")));
                // TODO: 2016/5/17 设置可以获取焦点
                window.setFocusable(true);
                // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
                window.setOutsideTouchable(true);
                // TODO：更新popupwindow的状态
                window.update();
                // TODO: 2016/5/17 以下拉的方式显示，并且可以设置显示的位置
                window.showAsDropDown(view, 0, 20);


            }
        });

    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public void reflash(){
        DataDeal.height.clear();

        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }

        dm=getResources().getDisplayMetrics();
        // 获取标题栏和状态栏高度
        int temp=dm.heightPixels-statusBarHeight1-dip2px(this,56);
        DataDeal.height.add(temp);
        Width=dm.widthPixels;
        System.out.println("屏幕高度:"+ DataDeal.height.get(0)+"屏幕宽度:"+Width);
    }

    //清空RecyclerView以及清空数组数据
    private void dataDelete(){
        try{
            mRecyclerView.removeAllViews();
            DataDeal.mtime.clear();
            mtmp.clear();
            DataDeal.micon.clear();
            mwind.clear();
            mtxt.clear();
            ncity.clear();
            ntmp.clear();
            naqi.clear();
            DataDeal.ntrav.clear();
            DataDeal.nflu.clear();
            bgpic.clear();
            DataDeal.bg_min.clear();
            DataDeal.tcolor.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //更新RecyclerView数据
    public static void setMyAdapter(){
        mAdapter.notifyDataSetChanged();
        Log.d("adapter","adapter");
    }
    @Override
    public void SaveEdit(int position, String string) {
        if(position==0){
            edit_text=string;
        }
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

    public void tip(String str){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
    //右上角菜单查询中国省市天气
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
//            switch (menuItem.getItemId()) {
//                case R.id.action_edit:
//                    sendMessage(0x10);
//                    break;
//                case R.id.action_shared:
//                    showShare();
//                    break;
//            }
            return true;
        }
    };
    //菜单回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
                if (data !=null){
                    MenuData menuData= (MenuData) data.getSerializableExtra("menu");
                    String str1=menuData.name;
                    if (menuData!=null){
                        //获取省市
                        dbHelper = new DBmanager(this);
                        dbHelper.openDatabase();
                        cursor=dbHelper.Query();
                        int parent_id=0;
                        String t_name="";
                        if(cursor.moveToFirst()){
                            do {
                                if(menuData.flag==cursor.getInt(cursor.getColumnIndex("districtid"))){
                                    t_name=cursor.getString(cursor.getColumnIndex("districtname"));
                                    parent_id=cursor.getInt(cursor.getColumnIndex("parentid"));
                                    break;
                                }
                            }while (cursor.moveToNext());
                        }
                        System.out.println("一级："+menuData.flag);
                        //获得市名
                        String str2=t_name;
                        int flag=parent_id;
                        System.out.println("二级："+flag);
                        if(cursor.moveToFirst()){
                            do {
                                if(flag==cursor.getInt(cursor.getColumnIndex("districtid"))){
                                    t_name=cursor.getString(cursor.getColumnIndex("districtname"));
                                    break;
                                }
                            }while (cursor.moveToNext());
                        }
                        //获得省名
                        String str3=t_name;
                        cursor.close();
                        dbHelper.closeDatabase();
                        //处理名字匹配城市代码
                        minfo.setProvince(DataDeal.data_Province(str3));
                        minfo.setCity(DataDeal.data_City(str2));
                        minfo.setDistrict(DataDeal.data_District(str1));
                        distinguish(minfo);
                    }
                }
                break;
            default:
        }
    }
    //断网处理
    public void Error(){
        sendMessage(0x13);
        //setContentView(R.layout.reflash);
        //Button bt=(Button)findViewById(R.id.connect);
        //TextView tv=(TextView)findViewById(R.id.sug);
        //tv.setText(minfo.getStatus());
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendMessage(0x15);
//            }
//        });
    }
    public void Reflash(){
        statrProgressDialog();
        setContentView(R.layout.activity_main);
        statr_weather();
        distinguish(minfo);
    }
    public void distinguish(LocationInfo info){
        RemoveWeatherView();
        if(!Network.isNetworkConnected(this) && !Network.isWifiConnected(this) && !Network.isMobileConnected(this)){
            info.setStatus("当前网络不可用！");
            sendMessage(0x13);
            sendMessage(0x14);
        }else{
            getWeather(info);
        }
    }
    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /**
     * 抽屉滑动范围控制
     * @param activity
     * @param drawerLayout
     * @param displayWidthPercentage 占全屏的份额0~1
     */
    private void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;
        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            Log.e("NoSuchFieldException", e.getMessage().toString());
        } catch (IllegalArgumentException e) {
            //Log.e("IllegalArgumentException", e.getMessage().toString());
        } catch (IllegalAccessException e) {
            Log.e("IllegalAccessException", e.getMessage().toString());
        }

    }
    private void setUpDrawer()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        //mLvLeftMenu.addHeaderView(inflater.inflate(R.layout.nav_header_main, mLvLeftMenu, false));
        //mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
    }
    //popupwindow相应函数
    public void initRecyclerview(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hRecyclerView.setLayoutManager(layoutManager);
        hRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
    }
    public void initAdapter(){
        hAdapter=new hRecyclerViewAdapter(this,history_city);
        hRecyclerView.setAdapter(hAdapter);
        hAdapter.setOnItemClickListener(new hRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int tag) {
                if(tag==0){
                    if(edit_text!=null&&!edit_text.equals("")){
                        SearchCity(edit_text);
                    }else{
                        Toast.makeText(WeatherActivity.this,"请输入城市名后再搜索", Toast.LENGTH_LONG).show();
                    }

                }else{
                    String name=null,id=null;
                    queue1=queue_city_name.clone();
                    queue2=queue_city_id.clone();
                    int y=10-tag;
                    for(int i=0;i<=y;i++){
                        if(i==y){
                            name=queue1.peek();
                            id=queue2.peek();
                            break;
                        }
                        queue1.poll();
                        queue2.poll();
                    }
                    if(!queue1.isEmpty()){
                        queue1.clear();
                        queue2.clear();
                    }
                    volleyGet(name,id);
                    window.dismiss();
                }

            }
        });
        hAdapter.setonEditorActionListener(new hRecyclerViewAdapter.EditKeyListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((Integer)v.getTag()==0){
                    if(actionId== EditorInfo.IME_ACTION_NEXT){
                        if(edit_text!=null&&!edit_text.equals("")){
                            SearchCity(edit_text);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public int getHour(){
        Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int Hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        return  Hour;
    }
    public void volleyGet(final String str1, String str2){
        if(window!=null){
            if(window.isShowing()){
                window.dismiss();
            }
        }
        if (DataDeal.mtime!=null){
            dataDelete();
        }
        if(!progress){
            statrProgressDialog();
        }
        if(queue_city_name.offer(str1)){
            queue_city_id.offer(str2);
        }

        Log.i("长度：",queue_city_name.queue+"    "+queue_city_id.queue);
        setHistory_list();
//        JsonObjectRequest request = new JsonObjectRequest( DataDeal.url+str2+ DataDeal.password, null,
//                new Response.Listener<org.json.JSONObject>() {
//                    @Override
//                    public void onResponse(org.json.JSONObject jsonObject) {//jsonObject为请求返回的Json格式数据
//                        //Toast.makeText(MainActivity.this,jsonObject.toString(),Toast.LENGTH_LONG).show();
//                        DataDeal.rjson=jsonObject.toString();
//                        System.out.println(DataDeal.rjson);
//                        Gson mg=new Gson();
//                        JSONObject jb = JSONObject.fromObject(DataDeal.rjson);
//                        String json1=jb.getString("result");
//                        JSONObject jsonObject1=JSONObject.fromObject(json1);
//                        //System.out.println(json1);
//                        String json2=jsonObject1.getString("HeWeather5");
//                        String json3=json2.substring(1,json2.length()-1);
//                        System.out.println(json3);
//                        WeatherResult wr=mg.fromJson(DataDeal.rjson,WeatherResult.class);
//                        if(wr.getMsg().equals("查询成功")){
//                            System.out.println("查询成功");
//                            //dataDelete();
//                            try{
//                                //System.out.println(json3);
//                                DataDeal.Json_deal(json3,mg,str1,getHour());
//                                setMyAdapter();
//                                if(control){
//                                    wc2=true;
//                                }
//                                sendMessage(0x18);
//                                sendMessage(0x13);
//                            }catch (Exception e){
//                                minfo.setStatus("返回信息有误，请稍候重试。");
//                                sendMessage(0x13);
//                                sendMessage(0x14);
//                            }
//                        }else{
//                            minfo.setStatus("服务器繁忙，请稍候重试。");
//                            sendMessage(0x13);
//                            sendMessage(0x14);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
//                        minfo.setStatus("网络出错，请稍后再试。");
//                        sendMessage(0x13);
//                        sendMessage(0x14);
//                    }
//                });
//
//        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
//        request.setTag("getWeather");
//        //设置超时时间
//        request.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        //将请求加入全局队列中
//        MyApplication.getHttpQueues().add(request);
    }
    public void SearchCity(String city){
//        JsonObjectRequest request = new JsonObjectRequest( DataDeal.url2+city+ DataDeal.password, null,
//                new Response.Listener<org.json.JSONObject>() {
//                    @Override
//                    public void onResponse(org.json.JSONObject jsonObject) {//jsonObject为请求返回的Json格式数据
//                        //Toast.makeText(MainActivity.this,jsonObject.toString(),Toast.LENGTH_LONG).show();
//                        JSONObject jb = JSONObject.fromObject(jsonObject.toString());
//                        String msg=jb.getString("msg");
//                        if(msg.equals("查询成功")){
//                            JSONObject jb2 = JSONObject.fromObject(jb.getString("result"));
//                            String weather=jb2.getString("HeWeather5");
//                            String json=weather.substring(1,weather.length()-1);
//                            System.out.println(json);
//                            JSONObject jb4 = JSONObject.fromObject(json);
//                            if(jb4.getString("status").equals("ok")){
//                                String json2=jb4.getString("basic");
//                                Gson gson=new Gson();
//                                City_basic basic=gson.fromJson(json2,City_basic.class);
//                                String cityname=null;
//                                if(basic.getProv().equals(basic.getCity())){
//                                    cityname=basic.getCity();
//                                }else{
//                                    cityname=basic.getProv()+" "+basic.getCity();
//                                }
//
//                                volleyGet(cityname,basic.getId());
//
//                            }else{
//                                Toast.makeText(WeatherActivity.this,"查找不到城市名", Toast.LENGTH_LONG).show();
//                            }
//                        }else{
//                            Toast.makeText(WeatherActivity.this,"查询失败", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
//                        minfo.setStatus("网络出错，请稍后再试。");
//                        sendMessage(0x13);
//                        sendMessage(0x14);
//                    }
//                });

        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        //request.setTag("getCity");
        //设置超时时间
        //request.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //将请求加入全局队列中
        //MyApplication.getHttpQueues().add(request);
    }
    public void InitGetui(){
        //PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        //PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), IntentService.class);
    }
    //天气接口函数
    private void getWeather(LocationInfo info) {
        String temp0;
        System.out.println(info.getProvince()+" "+info.getCity()+" "+info.getDistrict());
        String cn_code= DataDeal.cnCode(this,info);
        if(cn_code.equals("")){
            String tem=info.getDistrict();
            info.setDistrict(info.getCity());
            cn_code= DataDeal.cnCode(this,info);
            temp0=info.getCity()+" "+tem;
        }else{
            if(info.getCity().equals(info.getDistrict())){
                temp0=info.getDistrict();
            }else{
                temp0=info.getProvince()+" "+info.getDistrict();
            }
        }
        System.out.println("城市："+temp0+" code:"+cn_code);
        DataDeal.temp= temp0;
        DataDeal.citycode=cn_code;
        Log.i("城市：", DataDeal.temp);

        if (cn_code.equals("")){
            sendMessage(0x13);
            tip("此地区暂时无法查询");
        }else{
            volleyGet(DataDeal.temp, DataDeal.citycode);
        }
    }
    SwipeRefreshLayout.OnRefreshListener refreshListener=new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.d("下拉更新","哈哈哈哈");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    distinguish(minfo);
                    swipeRefreshLayout.setRefreshing(false);
                }
            },1000);
            //sendMessage(0x16);
        }
    };
    private void swipeRefresh(){
        distinguish(minfo);
        swipeRefreshLayout.setRefreshing(false);
    }
    private void showShare() {
        //OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        //oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        //oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        //oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        //oks.setText(getTodayText());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        //oks.show(this);
    }
    private String getTodayText(){
        return "城市:"+ncity.get(0)+"\n天气:"+mtxt.get(0)+"\n当前温度:"+ntmp.get(0)+"\n当天温度:"+mtmp.get(0)+"\n风力:"+mwind.get(0)+"\n空气质量:"+naqi.get(0);
    }
    private void InitRain(int num,int min_s,int max_s){
        //mDynamicWeatherView =new DynamicWeatherView(this);
        //mDynamicWeatherView.setType(new RainTypeImpl(this, mDynamicWeatherView,num,min_s,max_s));
        Log.d("InitRain",Width+" "+DataDeal.height.get(0));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Width,DataDeal.height.get(0));
        //mDynamicWeatherView.setLayoutParams(lp);
        //relativeLayout.addView(mDynamicWeatherView);
    }
    private void InitSnow(int num,int min_s,int max_s){
        //mDynamicWeatherView =new DynamicWeatherView(this);
        //mDynamicWeatherView.setType(new SnowTypeImpl(this, mDynamicWeatherView,num,min_s,max_s));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Width,DataDeal.height.get(0));
        //mDynamicWeatherView.setLayoutParams(lp);
        //relativeLayout.addView(mDynamicWeatherView);
    }
    private void RemoveWeatherView(){
//        if(mDynamicWeatherView!=null){
//            //relativeLayout.removeViewInLayout(mDynamicWeatherView);
//        }
    }
    private  void setWeatherView(String weather){
        if(weather.equals("晴")){

        }else if(weather.equals("多云")){

        }else if(weather.equals("晴间多云")){

        }else if(weather.equals("雷阵雨")||weather.equals("雷阵雨伴有冰雹")||weather.equals("雨夹雪")||weather.equals("暴雨")||weather.equals("大暴雨")||weather.equals("特大暴雨")||weather.equals("暴雨到大暴雨")){
            InitRain(120,5,9);
        }else if(weather.equals("阵雨")||weather.equals("小雨")||weather.equals("中雨")||weather.equals("大雨")||weather.equals("小到中雨")||weather.equals("中到大雨")||weather.equals("大到暴雨")){
            InitRain(60,5,9);
        }else if(weather.equals("阵雪")||weather.equals("小雪")||weather.equals("中雪")||weather.equals("大雪")||weather.equals("暴雪")||weather.equals("小到中雪")||weather.equals("中到大雪")||weather.equals("大到暴雪")){
            InitSnow(40,1,4);
        }else if(weather.equals("雾")){

        }else if(weather.equals("霾")){

        }else if(weather.equals("沙尘暴")||weather.equals("浮尘")||weather.equals("扬沙")||weather.equals("强沙尘暴")){

        }else if(weather.equals("阴")){

        }else if(weather.equals("冻雨")){

        }else{

        }
    }
    private void sendWindowChange(){
        Intent intent=new Intent();
        intent.setAction(WINDOWCHANGE);
        sendBroadcast(intent);
    }
    private void jugdeChange(){
        if(wc1&&wc2){
            sendWindowChange();
            wc1=false;
            wc2=false;
        }
    }
}
