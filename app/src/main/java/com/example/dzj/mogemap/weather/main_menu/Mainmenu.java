package com.example.dzj.theweather.main_menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dzj.theweather.R;
import com.example.dzj.theweather.main_menu.adapter.MenuDialogAdapter;
import com.example.dzj.theweather.main_menu.adapter.MyPagerAdapter;
import com.example.dzj.theweather.main_menu.bean.MenuData;
import com.example.dzj.theweather.main_menu.utils.MenuDataManager;
import com.example.dzj.theweather.main_menu.view.MyViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mainmenu extends Activity {

    private Context mContext;
    public MenuDataManager menuDataManager = MenuDataManager.getInstance();//获得txt文档数据
    private MyViewPager mViewPager;
    private View view1,view2,view3;
    private ListView mListView1,mListView2,mListView3;
    private MenuDialogAdapter mListView1Adapter, mListView2Adapter, mListView3Adapter;
    private List<View> views = new ArrayList<View>();
    private MenuData resultDate;//结果集

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_menu);
        mContext = this;
        initViews();

    }

    //操作控件
    private void initViews() {
        //一级
        mViewPager = (MyViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.pager_number, null);
        view2 = inflater.inflate(R.layout.pager_number, null);
        view3 = inflater.inflate(R.layout.pager_number, null);
        mListView1 = (ListView) view1.findViewById(R.id.listview);
        mListView2 = (ListView) view2.findViewById(R.id.listview);
        mListView3 = (ListView) view3.findViewById(R.id.listview);

        List<MenuData> list1=menuDataManager.getTripleColumnData(this, 0);//关键赋值
        mListView1Adapter = new MenuDialogAdapter(this, list1);
        mListView1Adapter.setSelectedBackgroundResource(R.drawable.select_white);//选中时
        mListView1Adapter.setHasDivider(false);
        mListView1Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
        mListView1.setAdapter(mListView1Adapter);

        views.add(view1);
        views.add(view2);//加载了一二级菜单
        mViewPager.setAdapter(new MyPagerAdapter(views));

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView1Adapter != null)
                    mListView1Adapter.setSelectedPos(position);
                if (mListView2Adapter != null)
                    mListView2Adapter.setSelectedPos(-1);

                if (views.contains(view3)) {
                    views.remove(view3);
                    mViewPager.getAdapter().notifyDataSetChanged();
                }
                MenuData menuData = (MenuData) parent.getItemAtPosition(position);
                if (menuData.id == 0) {//不限
                    if (mListView2Adapter != null) {
                        mListView2Adapter.setData(new ArrayList<MenuData>());
                        mListView2Adapter.notifyDataSetChanged();
                    }

                    setResultDate(menuData);
                } else {
                    List<MenuData> list2 = menuDataManager.getTripleColumnData(mContext, menuData.id);
                    if (mListView2Adapter == null) {
                        mListView2Adapter = new MenuDialogAdapter(mContext, list2);
                        mListView2Adapter.setNormalBackgroundResource(R.color.white);
                        mListView2.setAdapter(mListView2Adapter);
                    } else {
                        mListView2Adapter.setData(list2);
                        mListView2Adapter.notifyDataSetChanged();
                    }


//                    mRootView.invalidate();
                }

            }
        });

        //二级
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (mListView2Adapter != null) {
                    mListView2Adapter.setSelectedPos(position);
                    mListView2Adapter.setSelectedBackgroundResource(R.drawable.select_gray);//选中时
                }

                if (views.contains(view3)) {
                    views.remove(view3);
                }

                MenuData dictUnit = (MenuData) parent.getItemAtPosition(position);
                List<MenuData> list3 = menuDataManager.getTripleColumnData(mContext, dictUnit.id);
                if (mListView3Adapter == null) {
                    mListView3Adapter = new MenuDialogAdapter(mContext, list3);
                    mListView3Adapter.setHasDivider(false);
                    mListView3Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
                    mListView3.setAdapter(mListView3Adapter);
                } else {
                    mListView3Adapter.setData(list3);
                    mListView3Adapter.notifyDataSetChanged();
                }

                views.add(view3);
                mViewPager.getAdapter().notifyDataSetChanged();
                mViewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(views.size()-1);
                    }
                }, 300);
            }
        });
        mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuData menuData = (MenuData) parent.getItemAtPosition(position);
                setResultDate(menuData);
            }
        });

    }

    //传递值
    private void setResultDate(MenuData menuData){
        Intent intent=new Intent();
        intent.putExtra("menu",(Serializable)menuData);
        setResult(0, intent);
        finish();
    }
}
