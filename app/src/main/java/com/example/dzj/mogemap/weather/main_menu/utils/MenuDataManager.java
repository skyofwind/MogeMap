package com.example.dzj.mogemap.weather.main_menu.utils;

import android.content.Context;

import com.example.dzj.mogemap.weather.main_menu.bean.MenuData;

import java.util.List;

/**
 * Created by LaiYingtang on 2016/5/22.
 * 菜单数据管理类
 */
public final class MenuDataManager {
    //使用枚举找
    public enum MenuType {
        POSITION_FUNCTION("position_function.txt");//菜单数据放在txt

        String fileName;

        private MenuType(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    //单例
    private static MenuDataManager mInstance;

    //构造器
    public MenuDataManager() {
    }

    public static MenuDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new MenuDataManager();
        }
        return mInstance;
    }


    //获取列数据
    public List<MenuData> getTripleColumnData(Context mContext, int flag) {
        //把所有的menu的filName给MenuUtil解析
        return MenuUtil.getPositions(mContext, flag, MenuType.POSITION_FUNCTION.getFileName());
    }
}
