package com.example.dzj.mogemap.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.BindPhoneActivity;
import com.example.dzj.mogemap.activity.FriendsActivity;
import com.example.dzj.mogemap.activity.MainActivity;
import com.example.dzj.mogemap.activity.MonthCountActivity;
import com.example.dzj.mogemap.activity.PersonalInformationActivity;
import com.example.dzj.mogemap.activity.RankingActivity;
import com.example.dzj.mogemap.activity.WeekCountActivity;
import com.example.dzj.mogemap.modle.Mogemap_user;
import com.example.dzj.mogemap.modle.QQUser;
import com.example.dzj.mogemap.reciver.BindAccoutBroadcastReciver;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.MyPrefs;
import com.example.dzj.mogemap.utils.UserManager;
import com.example.dzj.mogemap.view.RoundImageView;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import okhttp3.Call;

/**
 * Created by dzj on 2017/12/14.
 */

public class MineManagerFragment extends Fragment {
    private static final String TAG = "MineManagerFragment";
    //private static final int MY_CODE = 20002;
    private View rootView;
    private RoundImageView roundImageView;
    private TextView username;
    private ImageView qq, xinlang;
    private LinearLayout rightArrow, mine;
    private LinearLayout ranking, friends, statisticsWeek, statisticsMonth, signOut;
    //qq登录相关
    private UserInfo userInfo;
    private IUiListener listener = new BaseUiListener();
    private String QQ_uid;//qq_openid
    private String scope = "all";
    //微博登录相关
    private SsoHandler mSsoHandler;

    private WbAuthListener wbAuthListener = new SelfWbAuthListener();
    private BindAccoutBroadcastReciver bindReciver;

    private int loginType = 0;
    private int loginResult = 0;
    private String myName, myId, myHead;
    private org.json.JSONObject qqJson;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        rootView=inflater.inflate(R.layout.mine_manager,null);
        initView();
        initLoginType();
        registerReciver();
        return rootView;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unRegisterReciver();
    }

    private void initView(){
        roundImageView = (RoundImageView)rootView.findViewById(R.id.head_picture);
        username = (TextView)rootView.findViewById(R.id.username_name);
        qq = (ImageView)rootView.findViewById(R.id.qq);
        xinlang = (ImageView)rootView.findViewById(R.id.xinlang);
        rightArrow = (LinearLayout)rootView.findViewById(R.id.right_arrow);
        mine = (LinearLayout)rootView.findViewById(R.id.mine);
        ranking = (LinearLayout)rootView.findViewById(R.id.ranking);
        friends = (LinearLayout)rootView.findViewById(R.id.friends);
        statisticsWeek = (LinearLayout)rootView.findViewById(R.id.statistics_week);
        statisticsMonth = (LinearLayout)rootView.findViewById(R.id.statistics_month);
        signOut = (LinearLayout)rootView.findViewById(R.id.sign_out);

        roundImageView.setOnTouchListener(touchListener);
        //username.setOnTouchListener(touchListener);
        qq.setOnTouchListener(touchListener);
        xinlang.setOnTouchListener(touchListener);
        //rightArrow.setOnTouchListener(touchListener);
        mine.setOnTouchListener(touchListener);
        ranking.setOnTouchListener(touchListener);
        friends.setOnTouchListener(touchListener);
        statisticsMonth.setOnTouchListener(touchListener);
        statisticsWeek.setOnTouchListener(touchListener);
        signOut.setOnTouchListener(touchListener);
    }
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            if(event.getAction() == MotionEvent.ACTION_DOWN){
//                if(Build.VERSION.SDK_INT >= 23){
//                    v.setBackgroundColor(getActivity().getColor(R.color.deviding));
//                }else {
//                    v.setBackgroundColor(getActivity().getResources().getColor(R.color.deviding));
//                }
//
//            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                if(Build.VERSION.SDK_INT >= 23){
                    v.setBackgroundColor(getActivity().getColor(R.color.white));
                }else {
                    v.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                }

            }
            switch (v.getId()){
                case R.id.head_picture:

                    break;
                case R.id.username_name:

                    break;
                case R.id.qq:
                    login();
                    loginType = 0;
                    break;
                case R.id.xinlang:
                    loginSina();
                    loginType = 1;
                    break;
                case R.id.right_arrow:

                    break;
                case R.id.mine:
                    if(qq.getVisibility() == View.GONE){
                        startActivity(new Intent(getActivity(), PersonalInformationActivity.class));
                    }

                    break;
                case R.id.ranking:
                    startActivity(new Intent(getActivity(), RankingActivity.class));
                    break;
                case R.id.friends:
                    startActivity(new Intent(getActivity(), FriendsActivity.class));
                    break;
                case R.id.statistics_week:
                    Intent intent = new Intent(getActivity(), WeekCountActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.statistics_month:
                    Intent intent1 = new Intent(getActivity(), MonthCountActivity.class);
                    getActivity().startActivity(intent1);
                    break;
                case R.id.sign_out:
                    loginResult = 0;
                    if (loginType == 0){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                qqLogout();
                            }
                        }, 200);

                    }else if (loginType == 1){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                logoutSina();
                            }
                        }, 200);

                    }
                    log("loginType="+loginType+"");
                    break;
            }
            return true;
        }
    };
    private void login(){
        //log("我惦记了登录");
        if(!MainActivity.mTencent.isSessionValid()){
            MainActivity.mTencent.login(getActivity(), scope, listener);
        }
    }
    Bitmap bitmap;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    com.alibaba.fastjson.JSONObject response = com.alibaba.fastjson.JSONObject.parseObject(String.valueOf(msg.obj));//JSONObject.parseObject(String.valueOf(msg.obj));
                    log("UserInfo:"+ JSON.toJSONString(response));
                    QQUser user=com.alibaba.fastjson.JSONObject.parseObject(response.toJSONString(),QQUser.class);
                    if (user!=null) {
                        log("userInfo:昵称："+user.getNickname()+"  性别:"+user.getGender()+"  地址："+user.getProvince()+user.getCity());
                        log("头像路径："+user.getFigureurl_qq_2());
                        //username.setText(user.getNickname());
                        myName = user.getNickname();
                        if(user.getFigureurl_qq_2() == null || user.getFigureurl_qq_2().equals("")){
                            //setHeadImage(user.getFigureurl_qq_1());
                            myHead = user.getFigureurl_qq_1();
                        }else {
                            //setHeadImage(user.getFigureurl_qq_2());
                            myHead = user.getFigureurl_qq_2();
                        }

                    }
                    isQQBind(myId);
                    log("wodecishu"+"jianting ");
                    break;
                case 1:
                    log("case 1");
                    if(bitmap != null){
                        log("bitmapset");
                        roundImageView.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    };
    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener{
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            loginResult = 1;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.mAccessToken = token;
                    if (MainActivity.mAccessToken.isSessionValid()) {
                        log(MainActivity.mAccessToken.toString());

                        getWeiBoUserMessage(MainActivity.mAccessToken.getToken(), MainActivity.mAccessToken.getUid());
                    }
                }
            });
        }

        @Override
        public void cancel() {
            //Toast.makeText(WBAuthActivity.this,
            //        R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
            loginResult = 0;
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            //Toast.makeText(WBAuthActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
            loginResult = 0;
        }
    }
    class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            log("json=:"+o.toString());
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(o.toString());
                qqJson = jsonObject;
                myId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                MainActivity.mTencent.setAccessToken(token, expires);
                MainActivity.mTencent.setOpenId(openId);
                updateUserInfo();
                loginResult = 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            log("onError:code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
            loginResult = 0;
        }
        @Override
        public void onCancel() {
            log("onCancel");
            loginResult = 0;
        }
    }
    public void initOpenidAndToken(org.json.JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            myId = openId;
            long s = System.currentTimeMillis() + Long.parseLong(expires) * 1000;
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                MainActivity.mTencent.setAccessToken(token, expires);
                MainActivity.mTencent.setOpenId(openId);
                QQ_uid = openId;
                saveQQAcount(openId, token, s+"");
                log("tencent="+token+" expires="+expires+" openid="+openId+" s="+s);
            }
        } catch(Exception e) {
        }
    }
    private void updateUserInfo() {
        if (MainActivity.mTencent != null && MainActivity.mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }
                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    log("................"+response.toString());
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
                @Override
                public void onCancel() {
                    log("登录取消..");
                }
            };
            userInfo = new UserInfo(getContext(), MainActivity.mTencent.getQQToken());
            userInfo.getUserInfo(listener);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(TAG, "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,listener);
            log("wodecishu"+"回调");
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//            if (loginResult == 1){
//                Intent intent = new Intent(getActivity(), BindPhoneActivity.class);
//                log("mydata="+myName+" "+myHead);
//                intent.putExtra("name", myName);
//                intent.putExtra("head", myHead);
//                intent.putExtra("type", "weibo");
//                intent.putExtra("id", myId);
//                getActivity().startActivityForResult(intent, MY_CODE);
//            }
        }
    }
    private void log(String s){
        Log.i(TAG, s);
    }
    private void qqLogout(){
        if(MainActivity.mTencent != null){
            MainActivity.mTencent.logout(getContext());
            signOut();
            removeQQAcount();
            UserManager.getInstance().setUser(new Mogemap_user());
        }
    }
    private void loginSina(){
        mSsoHandler = new SsoHandler(getActivity());
        mSsoHandler.authorize(wbAuthListener);
        //signIn();
    }
    private void logoutSina(){
        AccessTokenKeeper.clear(getContext().getApplicationContext());
        mSsoHandler = null;
        MainActivity.mAccessToken = new Oauth2AccessToken();
        signOut();
        removeSinaAcount();
        UserManager.getInstance().setUser(new Mogemap_user());
    }
    private void signIn(){
        qq.setVisibility(View.GONE);
        xinlang.setVisibility(View.GONE);
        rightArrow.setVisibility(View.VISIBLE);
        signOut.setVisibility(View.VISIBLE);
        Mogemap_user user = UserManager.getInstance().getUser();
        if(user != null){
            username.setText(user.getName());
            setHeadImage(user.getHeadurl());
        }

    }
    private void signOut(){
        signOut.setVisibility(View.GONE);
        username.setText(R.string.defualt_username);
        qq.setVisibility(View.VISIBLE);
        xinlang.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.GONE);
        roundImageView.setImageDrawable(getActivity().getDrawable(R.drawable.head_defualt));
    }
    private void setHeadImage(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(url != null){
                    bitmap = HttpUtil.getHttpBitmap(url);
                    mHandler.sendEmptyMessage(0x01);
                }
            }
        }).start();
    }
    private void saveQQAcount(String openid, String accessToken, String expiresIn){
        MyPrefs.getInstance().writeString(MyPrefs.QQ_OPEN_ID, openid, 0);
        MyPrefs.getInstance().writeString(MyPrefs.QQ_ACCESS_TOKEN, accessToken, 0);
        MyPrefs.getInstance().writeString(MyPrefs.QQ_EXPIRES_IN, expiresIn, 0);
    }
    private void removeQQAcount(){
        MyPrefs.getInstance().writeString(MyPrefs.QQ_OPEN_ID, "", 0);
        MyPrefs.getInstance().writeString(MyPrefs.QQ_ACCESS_TOKEN, "", 0);
        MyPrefs.getInstance().writeString(MyPrefs.QQ_EXPIRES_IN, "", 0);
    }
    private void saveSinaAcount(String openid, String accessToken, String expiresIn){
        MyPrefs.getInstance().writeString(MyPrefs.SINA_UID, openid, 1);
        MyPrefs.getInstance().writeString(MyPrefs.SINA_ACCESS_TOKEN, accessToken, 1);
        MyPrefs.getInstance().writeString(MyPrefs.SINA_EXPIRES_IN, expiresIn, 1);
    }
    private void removeSinaAcount(){
        MyPrefs.getInstance().writeString(MyPrefs.SINA_UID, "", 1);
        MyPrefs.getInstance().writeString(MyPrefs.SINA_ACCESS_TOKEN, "", 1);
        MyPrefs.getInstance().writeString(MyPrefs.SINA_EXPIRES_IN, "", 1);
    }
    //
    private void initLoginType(){
        if (MyPrefs.getInstance().readString(MyPrefs.QQ_OPEN_ID, 0) == null ||
                MyPrefs.getInstance().readString(MyPrefs.QQ_OPEN_ID, 0).equals("")){
        }else {
            if(MainActivity.mTencent.isSessionValid()){
                loginType = 0;
                //updateUserInfo();
                setHAT();

            }else {
                qqLogout();
            }
        }
        if (MyPrefs.getInstance().readString(MyPrefs.SINA_UID, 1) == null ||
                MyPrefs.getInstance().readString(MyPrefs.SINA_UID, 1).equals("")) {
        } else {
            if(MainActivity.mAccessToken.isSessionValid()){
                loginType = 1;
                setHAT();
                //signIn();
                //String token = MyPrefs.getInstance().readString(MyPrefs.SINA_ACCESS_TOKEN, 1);
                //String uid = MyPrefs.getInstance().readString(MyPrefs.SINA_UID, 1);
                //getWeiBoUserMessage(token, uid);
            }else {
                logoutSina();
            }
        }
    }
    private void getWeiBoUserMessage(String token, String uid){
        myId = uid;
        OkHttpUtils.get()
                .url("https://api.weibo.com/2/users/show.json")
                .addParams("access_token",token)
                .addParams("uid",uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        log("获取失败："+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log("response:"+response);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response);
                        myName = jsonObject.getString("name");
                        myHead = jsonObject.getString("profile_image_url");
                        isWeiboBind(myId);
                    }
                });
    }
    private void isQQBind(String openid){
        log("qqbind");
        OkHttpUtils.get()
                .url(HttpUtil.GET_USER_BY_QQ_URL+"/"+openid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        log("获取失败："+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log("response:"+response);
                        Mogemap_user user = JSON.parseObject(response, Mogemap_user.class);
                        if(user != null){
                            UserManager.getInstance().setUser(user);
                            signIn();
                            username.setText(user.getName());
                            setHeadImage(user.getHeadurl());
                            initOpenidAndToken(qqJson);
                        }else {
                            Intent intent = new Intent(getActivity(), BindPhoneActivity.class);
                            log("mydata="+myName+" "+myHead);
                            intent.putExtra("name", myName);
                            intent.putExtra("head", myHead);
                            intent.putExtra("type", "qq");
                            intent.putExtra("id", myId);
                            getActivity().startActivity(intent);
                        }
                    }
                });
    }

    private void isWeiboBind(String uid){
        OkHttpUtils.get()
                .url(HttpUtil.GET_USER_BY_WEIBO_URL+"/"+uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        log("获取失败："+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        log("response:"+response);
                        Mogemap_user user = JSON.parseObject(response, Mogemap_user.class);
                        if(user != null){
                            UserManager.getInstance().setUser(user);
                            signIn();
                            username.setText(user.getName());
                            setHeadImage(user.getHeadurl());
                            saveSinaAcount(MainActivity.mAccessToken.getUid(), MainActivity.mAccessToken.getToken(), MainActivity.mAccessToken.getExpiresTime()+"");
                            AccessTokenKeeper.writeAccessToken(getContext(), MainActivity.mAccessToken);
                        }else {
                            Intent intent = new Intent(getActivity(), BindPhoneActivity.class);
                            log("mydata="+myName+" "+myHead);
                            intent.putExtra("name", myName);
                            intent.putExtra("head", myHead);
                            intent.putExtra("type", "weibo");
                            intent.putExtra("id", myId);
                            getActivity().startActivity(intent);
                        }
                    }
                });
    }
    private void setHAT(){
        Mogemap_user user = UserManager.getInstance().getUser();
        signIn();
        username.setText(user.getName());
        setHeadImage(user.getHeadurl());
    }
    private void registerReciver(){
        if(null == bindReciver){
            IntentFilter filter = new IntentFilter();
            filter.addAction(BindAccoutBroadcastReciver.BIND_ACCOUT);
            bindReciver = new BindAccoutBroadcastReciver();
            getActivity().registerReceiver(bindReciver, filter);
            bindReciver.setListener(new BindAccoutBroadcastReciver.BindAccoutListener() {
                @Override
                public void onUpdate(String s) {
                    setHAT();
                    if(s.equals("qq")){
                        initOpenidAndToken(qqJson);
                    }else {
                        saveSinaAcount(MainActivity.mAccessToken.getUid(), MainActivity.mAccessToken.getToken(), MainActivity.mAccessToken.getExpiresTime()+"");
                        AccessTokenKeeper.writeAccessToken(getContext(), MainActivity.mAccessToken);
                    }
                }
            });
        }
    }
    private void unRegisterReciver(){
        if(bindReciver != null){
            getActivity().unregisterReceiver(bindReciver);
            bindReciver = null;
        }
    }
}
