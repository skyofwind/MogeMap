package com.example.dzj.mogemap.utils;

import com.example.dzj.mogemap.modle.Mogemap_user;

/**
 * Created by dzj on 2018/2/23.
 */

public class UserManager {
    private static UserManager userManager;
    private Mogemap_user user;
    private UserManager(){}
    public static UserManager getInstance(){
        if(userManager == null){
            userManager = new UserManager();
        }
        return userManager;
    }

    public Mogemap_user getUser() {
        if(user == null){
            user = new Mogemap_user();
        }
        return user;
    }

    public void setUser(Mogemap_user user) {
        this.user = user;
    }
}
