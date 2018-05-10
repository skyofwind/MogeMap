package com.example.dzj.mogemap.activity;

import android.app.Dialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dzj.mogemap.R;

/**
 * Created by dzj on 2018/2/28.
 */

public class BaseActivty extends AppCompatActivity{
    private ImageView icon, iconRight;
    private TextView title;
    //定时器相关
    private Dialog progressDialog;
    private boolean  progress=false;

    public void initTitle(){
        icon = (ImageView)findViewById(R.id.icon);
        iconRight = (ImageView)findViewById(R.id.icon_right);
        title = (TextView)findViewById(R.id.titleText);
    }
    public void setIcon(int drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icon.setImageDrawable(getDrawable(drawable));
        }else {
            icon.setImageDrawable(getResources().getDrawable(drawable));
        }
    }
    public void setIconRight(int drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconRight.setImageDrawable(getDrawable(drawable));
        }else {
            iconRight.setImageDrawable(getResources().getDrawable(drawable));
        }
    }
    public void setTitle(String s){
        title.setText(s);
    }
    public void setIconListener(View.OnClickListener listener){
        icon.setOnClickListener(listener);
    }
    public void seticonRightListener(View.OnClickListener listener){
        iconRight.setOnClickListener(listener);
    }
    public void statrProgressDialog(){
        if(progressDialog == null){
            progressDialog = new Dialog(this,R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("正在加载中");
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
    @Override
    protected void onDestroy(){
        super.onDestroy();
        cancel();
    }

}
