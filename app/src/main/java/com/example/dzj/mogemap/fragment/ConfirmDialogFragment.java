package com.example.dzj.mogemap.fragment;


import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.utils.SystemUtils;

/**
 * Created by dzj on 2018/2/25.
 */

public class ConfirmDialogFragment extends DialogFragment {
    private View root;
    private Button keep, finish;
    private OnDialogListener k, f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        root = inflater.inflate(R.layout.confirm_layout, null);
        init();
        return root;
    }
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
    private void init(){
        keep = (Button)root.findViewById(R.id.submit);
        finish = (Button)root.findViewById(R.id.cancle);
        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(k != null){
                    k.onDialogClick();
                    dismiss();
                }
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finish != null){
                    f.onDialogClick();
                    dismiss();
                }
            }
        });
    }
    public interface OnDialogListener {
        void onDialogClick();
    }
    public void setKeep(OnDialogListener listener){
        k = listener;
    }
    public void setFinish(OnDialogListener listener){
        f = listener;
    }
}
