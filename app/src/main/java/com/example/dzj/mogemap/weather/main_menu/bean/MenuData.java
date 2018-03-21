package com.example.dzj.theweather.main_menu.bean;

import java.io.Serializable;

/**
 * Created by LaiYingtang on 2016/5/22.
 * 列数据的bean
 */
public class MenuData implements Serializable {
    public int id;
    public String name;
    public int flag;

    public MenuData(int id, String name, int flag) {
        this.id = id;
        this.name = name;
        this.flag = flag;
    }

    public MenuData() {
    }
}
