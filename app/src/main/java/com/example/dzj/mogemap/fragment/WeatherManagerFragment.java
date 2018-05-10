package com.example.dzj.mogemap.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.MainActivity;
import com.example.dzj.mogemap.utils.ToastUtil;
import com.example.dzj.mogemap.weather.json_analysis.City_basic;
import com.example.dzj.mogemap.weather.main.BaiDuLocation;
import com.example.dzj.mogemap.weather.main.DataDeal;
import com.example.dzj.mogemap.weather.main.DynamicBroadcastReceiver;
import com.example.dzj.mogemap.weather.main.LimitQueue;
import com.example.dzj.mogemap.weather.main.LimitQueue_Repeat;
import com.example.dzj.mogemap.weather.main.LocationInfo;
import com.example.dzj.mogemap.weather.main_menu.DB_code;
import com.example.dzj.mogemap.weather.main_menu.DBmanager;
import com.example.dzj.mogemap.weather.main_menu.bean.MenuData;
import com.example.dzj.mogemap.weather.recylerview.DividerItemDecoration;
import com.example.dzj.mogemap.weather.recylerview.RecyclerViewAdapter;
import com.example.dzj.mogemap.weather.recylerview.hRecyclerViewAdapter;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.example.dzj.mogemap.activity.MainActivity.edit_text;
import static com.example.dzj.mogemap.weather.main.DataDeal.bgpic;
import static com.example.dzj.mogemap.weather.main.DataDeal.ncity;
import static com.example.dzj.mogemap.weather.main.DataDeal.url;
import static com.example.dzj.mogemap.weather.main.WeatherActivity.DYNAMICACTION;
import static com.example.dzj.mogemap.weather.main.WeatherActivity.WINDOWCHANGE;

/**
 * Created by dzj on 2017/12/14.
 */

public class WeatherManagerFragment extends Fragment{

    private final static String TAG = "WeatherManagerFragment";
    public static final int REQUEST_CODE = 100;//请求码

    private View rootView;
    private RelativeLayout mainBody;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    public static RecyclerViewAdapter mAdapter;

    //popupwindow
    private PopupWindow window=null;
    private RecyclerView hRecyclerView;
    public hRecyclerViewAdapter hAdapter;
    private List<String> history_city=new ArrayList<String>();

    //数据库
    public static DBmanager dbHelper;
    public static DB_code dbcode;
    public static Cursor cursor;

    private BaiDuLocation mLocation;
    private LocationInfo minfo;
    private DynamicBroadcastReceiver broadcastReceiver;

    private boolean isFirst = true;

    //定时器相关
    private Dialog progressDialog;
    private boolean  progress=false;


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

    private int qSize = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView=inflater.inflate(R.layout.weather_manager,null);
        initView(rootView);
        DataDeal.initData();
        //initLocation();
        startRecycleyvie();

        queue_city_name= new LimitQueue<>(10);
        queue_city_id= new LimitQueue_Repeat<>(10);
        history_deal();

        //DataDeal.height.add(SystemUtils.HEIGHT);
        return rootView;
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    public void onResume(){
        super.onResume();
        if(minfo == null){
            initLocation();
        }

        registerBroadcastReceiver();
    }
    public void onPause(){
        super.onPause();
        unregisterBroadcastReceiver();
        if(mLocation!=null&&mLocation.isLocationStart()){
            mLocation.Stop();
        }
    }
    public void onStop(){
        super.onStop();
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
    public void onDestroy(){
        unregisterBroadcastReceiver();
        super.onDestroy();
    }
    private android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0x01:
                    //DataDeal.logData();
                    //cancel();
                    mAdapter.notifyDataSetChanged();
                    //log("更新ui");
                    //logQueue();
                    break;
            }
        }
    };
    private void initView(View rootView){
        mainBody = (RelativeLayout)rootView.findViewById(R.id.main_body);
        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swiperRefresh);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.id_recyclerview);
        swipeRefresh.setOnRefreshListener(refreshListener);
    }
    //初始化百度定位组件
    private void initLocation(){
        mLocation=new BaiDuLocation(getContext());
        mLocation.Start();
        //statrProgressDialog();
    }
    public void statrProgressDialog(){
        progressDialog = new Dialog(getContext(),R.style.progress_dialog);
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

    /**
     *
     * @param name
     * @param id
     * @param url
     *
     */
    private void getData(final String name, String id, String url){
        if(window!=null){
            if(window.isShowing()){
                window.dismiss();
            }
        }

        dataDelete();
            //statrProgressDialog();

        if(queue_city_name.offer(name)){
            queue_city_id.offer(id);
            logQueue();
        }
        setHistory_list();
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        log(e.toString());
                        ToastUtil.tip(getContext(), "查询失败", 1);
                        //cancel();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log(response);
                        JSONObject object = JSON.parseObject(response);
                        String msg = object.getString("msg");
                        if (msg.equals("查询成功")){
                            object = JSON.parseObject(object.getString("result"));
                            String weather = object.getString("HeWeather5");
                            weather = weather.substring(1,weather.length()-1);
                            log(weather);
                            DataDeal.Json_deal(weather, name, getHour());
                            handler.sendEmptyMessage(0x01);
                        }else {
                            ToastUtil.tip(getContext(), "查询失败", 1);
                        }
                    }
                });
    }
    public int getHour(){
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int Hour=mCalendar.get(Calendar.HOUR_OF_DAY);
        return  Hour;
    }
    //注册动态广播
    private void registerBroadcastReceiver(){
        if(broadcastReceiver==null){
            IntentFilter filter = new IntentFilter();
            filter.addAction(DYNAMICACTION);
            filter.addAction(WINDOWCHANGE);
            broadcastReceiver = new DynamicBroadcastReceiver();
            getActivity().registerReceiver(broadcastReceiver, filter);
            broadcastReceiver.setGetLocation(new DynamicBroadcastReceiver.GetLocation() {
                @Override
                public void getLocation(LocationInfo info) {
                    minfo=new LocationInfo();
                    minfo.setDistrict(info.getDistrict());
                    minfo.setCity(info.getCity());
                    minfo.setProvince(info.getProvince());
                    //distinguish(info);
                    getWeather(minfo);
                    mLocation=null;
                    Log.i("info",info.toString());
                }
            });
            broadcastReceiver.setWindowChange(new DynamicBroadcastReceiver.WindowChange() {
                @Override
                public void onChange() {
                    if(DataDeal.mtxt.size()>0){
                        //sendMessage(0x17);
                        //Log.i("mtxt",DataDeal.mtxt.get(0));
                    }
                }
            });
        }
    }
    private void getWeather(LocationInfo info){
        String temp0;
        String cn_code= DataDeal.cnCode(getActivity(), info);
        if(cn_code.equals("")){
            String tem=info.getDistrict();
            info.setDistrict(info.getCity());
            cn_code= DataDeal.cnCode(getActivity(), info);
            temp0=info.getCity()+" "+tem;
        }else{
            if(info.getCity().equals(info.getDistrict())){
                temp0=info.getDistrict();
            }else{
                temp0=info.getProvince()+" "+info.getDistrict();
            }
        }
        DataDeal.temp = temp0;
        DataDeal.citycode = cn_code;
        if (cn_code.equals("")){
            //sendMessage(0x13);
            //tip("此地区暂时无法查询");
            cancel();
            ToastUtil.tip(getContext(), "此地区暂时无法查询", 1);
        }else{
            getData(temp0, cn_code, url+cn_code+ DataDeal.password);
            //volleyGet(DataDeal.temp, DataDeal.citycode);
        }
    }
    //注销动态广播
    private void unregisterBroadcastReceiver(){
        if(broadcastReceiver!=null){
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }
    public void startRecycleyvie(){
        //调用RecyclerView初始化
        //recyclerView=(RecyclerView)findViewById(R.id.id_recyclerview);
        mAdapter=new RecyclerViewAdapter(getContext(), DataDeal.mtime, DataDeal.mtmp, DataDeal.micon, DataDeal.mwind, DataDeal.mtxt, DataDeal.ntmp, ncity, DataDeal.height, DataDeal.naqi, DataDeal.ntrav, DataDeal.nflu, bgpic, DataDeal.bg_min, DataDeal.tcolor);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
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
    //popupwindow相应函数
    public void initRecyclerview(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hRecyclerView.setLayoutManager(layoutManager);
        hRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
    }
    public void initAdapter(){
        hAdapter=new hRecyclerViewAdapter(getContext(),history_city);
        hRecyclerView.setAdapter(hAdapter);
        hAdapter.setOnItemClickListener(new hRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int tag) {
                if(tag==0){
                    if(edit_text!=null&&!edit_text.equals("")){
                        searchCity(edit_text);
                    }else{
                        Toast.makeText(getContext(),"请输入城市名后再搜索",Toast.LENGTH_LONG).show();
                    }

                }else{
                    String name = "",id = "";
                    queue1=queue_city_name.clone();
                    queue2=queue_city_id.clone();
                    int y=queue_city_name.size()-tag;
                    log("tag="+tag);
                    for(int i=0;i<=y;i++){
                        if(i==y){
                            name=queue1.peek();
                            id=queue2.peek();
                            break;
                        }
                        log("i="+i+" name="+name+" id="+id);
                        queue1.poll();
                        queue2.poll();
                    }
                    if(!queue1.isEmpty()){
                        queue1.clear();
                        queue2.clear();
                    }
                    log("name="+name+" id="+id);
                    getData(name, id, url+id+ DataDeal.password);
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
                            searchCity(MainActivity.edit_text);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    private void searchCity(String city){
        OkHttpUtils
                .get()
                .url(DataDeal.url2+city+ DataDeal.password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        log(e.toString());
                        ToastUtil.tip(getContext(), "查询失败", 1);
                        //cancel();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log(response);
                        JSONObject object = JSON.parseObject(response);
                        String msg = object.getString("msg");
                        if (msg.equals("查询成功")){
                            object = JSON.parseObject(object.getString("result"));
                            String weather = object.getString("HeWeather5");
                            weather = weather.substring(1,weather.length()-1);
                            object = JSON.parseObject(weather);
                            if(object.getString("status").equals("ok")){
                                String json2=object.getString("basic");
                                Gson gson=new Gson();
                                City_basic basic=gson.fromJson(json2,City_basic.class);
                                String cityname=null;
                                if(basic.getProv().equals(basic.getCity())){
                                    cityname=basic.getCity();
                                }else{
                                    cityname=basic.getProv()+" "+basic.getCity();
                                }
                                getData(cityname, basic.getId(), url+basic.getId()+ DataDeal.password);
                            }else {

                            }

                        }else {
                            ToastUtil.tip(getContext(), "查询失败", 1);
                        }
                    }
                });

    }
    private void dataDelete(){
        try{
            if(recyclerView != null){
                recyclerView.removeAllViews();
            }
            DataDeal.mtime.clear();
            DataDeal.mtmp.clear();
            DataDeal.micon.clear();
            DataDeal.mwind.clear();
            DataDeal.mtxt.clear();
            ncity.clear();
            DataDeal.ntmp.clear();
            DataDeal.naqi.clear();
            DataDeal.ntrav.clear();
            DataDeal.nflu.clear();
            bgpic.clear();
            DataDeal.bg_min.clear();
            DataDeal.tcolor.clear();
            DataDeal.height.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if (data !=null){
                MenuData menuData= (MenuData) data.getSerializableExtra("menu");
                String str1=menuData.name;
                if (menuData!=null){
                    //获取省市
                    dbHelper = new DBmanager(getContext());
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
                    getWeather(minfo);
                }
            }
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
            System.out.println("队列长度："+queue3.size());
            System.out.println("queue_city_name队列长度："+queue_city_name.size());
        }
        for(int i=str.size()-1;i>=0;i--){
            history_city.add(str.get(i));
        }
    }
    public void history_deal(){
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
                    //int mb = 0;
                    if(!name.equals("")){
                        queue_city_name.offer(name);
                        queue_city_id.offer(id);
                        //mb++;
                    }
                    //qSize = mb;
                }
                add_order=true;
            }
            //DataDeal.mvoice=settings.getBoolean("voice",true);
        }
    }
    private void log(String s){
        Log.d(TAG, s);
    }
    private void logQueue(){
        queue1=queue_city_name.clone();
        queue2=queue_city_id.clone();
        for (int i = 0; i <10; i++){
            if (queue1.peek() != ""){
                log("name="+queue1.peek());
                queue1.poll();
                log("id="+queue2.peek());
                queue2.poll();
            }
        }
    }
    SwipeRefreshLayout.OnRefreshListener refreshListener=new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.d("下拉更新","哈哈哈哈");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWeather(minfo);
                    swipeRefresh.setRefreshing(false);
                }
            },1000);
            //sendMessage(0x16);
        }
    };
}
