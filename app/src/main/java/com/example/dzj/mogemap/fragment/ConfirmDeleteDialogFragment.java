package com.example.dzj.mogemap.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.FriendsActivity;
import com.example.dzj.mogemap.utils.SystemUtils;
import com.example.dzj.mogemap.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static com.example.dzj.mogemap.utils.HttpUtil.DELETE_FRIEND;

/**
 * Created by dzj on 2018/4/2.
 */

public class ConfirmDeleteDialogFragment extends DialogFragment{
    private View root;
    private String mPhone = "", fPhone = "";
    private int position;
    private Button submit, cancle;
    //定时器相关
    private Dialog progressDialog;
    private boolean  progress=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        root = inflater.inflate(R.layout.confirm_delete, null);
        init();
        Bundle bundle = getArguments();
        if(bundle != null){
            mPhone = bundle.getString("mPhone");
            fPhone = bundle.getString("fPhone");
            position = bundle.getInt("position");
        }
        return root;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 0x01:
                    statrProgressDialog();
                    break;
                case 0x02:
                    cancel();
                    break;
                case 0x03:

                    break;
            }
        }
    };
    @Override
    public void onStart(){
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = SystemUtils.MAX_WIDTH-50;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.y = 30;
        window.setAttributes(params);
        //设置背景透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        cancel();
    }
    private void init(){
        submit = (Button)root.findViewById(R.id.submit);
        cancle = (Button)root.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0x01);
                getData();
            }
        });
    }
    public void statrProgressDialog(){
        if(progressDialog == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                progressDialog = new Dialog(getContext(),R.style.progress_dialog);
            }
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("请稍等。。。");
        }
        progress=true;
        progressDialog.show();
    }
    public void cancel(){
        if(progress){
            progress=false;
            progressDialog.dismiss();
        }
    }
    private void getData(){
        Log.i("deleteURL", DELETE_FRIEND+mPhone+"/"+fPhone);
        OkHttpUtils
                .get()
                .url(DELETE_FRIEND+mPhone+"/"+fPhone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        handler.sendEmptyMessage(0x02);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ToastUtil.tip(getContext(), "请求出错", 1);
                        }
                        dismiss();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response", response);
                        handler.sendEmptyMessage(0x02);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response);
                        String use = jsonObject.getString("use");
                        if(use.equals("1")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ToastUtil.tip(getContext(), "删除成功", 1);
                                ((FriendsActivity)getContext()).deleteItem(position);
                            }
                        }else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ToastUtil.tip(getContext(), "删除失败", 1);
                            }
                        }
                        dismiss();
                    }
                });
    }
}
