package com.example.dzj.mogemap.fragment;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.modle.UserRunPK;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.OtherUtil;
import com.example.dzj.mogemap.utils.SystemUtils;
import com.example.dzj.mogemap.utils.ToastUtil;
import com.example.dzj.mogemap.view.RoundImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static com.example.dzj.mogemap.utils.HttpUtil.GET_PK_DAY;

/**
 * Created by dzj on 2018/3/4.
 */

public class PKDialogFragment extends DialogFragment {

    private View root;
    private RoundImageView winBig;
    private RoundImageView winIcon, loseIcon;
    private TextView winName, winCount, loseName, loseCount;
    private LinearLayout back;
    private String mPhone = "", fPhone = "";
    private UserRunPK userRunPK = new UserRunPK();
    private Bitmap bitmap1, bitmap2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        root = inflater.inflate(R.layout.pk_dialog, null);
        init();
        Bundle bundle = getArguments();
        if(bundle != null){
            mPhone = bundle.getString("mPhone");
            fPhone = bundle.getString("fPhone");
        }
        getData();
        return root;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0x01:
                    updateUI();
                    break;
                case 0x02:
                    winBig.setImageBitmap(bitmap1);
                    winIcon.setImageBitmap(bitmap1);
                    break;
                case 0x03:
                    loseIcon.setImageBitmap(bitmap2);
                    break;
            }
        }
    };
    @Override
    public void onStart(){
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = SystemUtils.MAX_WIDTH-50;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //设置背景透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private void init(){
        winBig = (RoundImageView)root.findViewById(R.id.win_big);
        winIcon = (RoundImageView)root.findViewById(R.id.win_icon);
        winName = (TextView)root.findViewById(R.id.win_name);
        winCount = (TextView)root.findViewById(R.id.win_count);
        loseIcon = (RoundImageView)root.findViewById(R.id.lose_icon);
        loseName = (TextView)root.findViewById(R.id.lose_name);
        loseCount = (TextView)root.findViewById(R.id.lose_count);
        back = (LinearLayout)root.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void getData(){

        OkHttpUtils
                .get()
                .url(GET_PK_DAY+mPhone+"/PK/"+fPhone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ToastUtil.tip(getContext(), "请求出错", 1);
                        }
                        dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        userRunPK = JSON.parseObject(response, UserRunPK.class);
                        Log.d("response", response);
                        handler.sendEmptyMessage(0x01);
                    }
                });
    }
    private void updateUI(){

        setHeadImage(userRunPK.getWin().getHead(), 0);
        setHeadImage(userRunPK.getLose().getHead(), 1);
        winName.setText(userRunPK.getWin().getName());
        winCount.setText(OtherUtil.getKM(userRunPK.getWin().getDistance())+" 公里");

        loseName.setText(userRunPK.getLose().getName());
        loseCount.setText(OtherUtil.getKM(userRunPK.getLose().getDistance())+" 公里");
    }
    private void setHeadImage(final String url, final int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(url != null){
                    if(type == 0){
                        bitmap1 = HttpUtil.getHttpBitmap(url);
                        handler.sendEmptyMessage(0x02);
                    }else {
                        bitmap2 = HttpUtil.getHttpBitmap(url);
                        handler.sendEmptyMessage(0x03);
                    }

                }
            }
        }).start();
    }


}
