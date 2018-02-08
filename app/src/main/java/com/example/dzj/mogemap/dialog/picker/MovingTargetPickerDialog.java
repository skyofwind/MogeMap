package com.example.dzj.mogemap.dialog.picker;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.utils.SystemUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dzj on 2018/2/1.
 */

public class MovingTargetPickerDialog extends Dialog {

    private MovingTargetPickerDialog.Params params;

    public MovingTargetPickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void setParams(MovingTargetPickerDialog.Params params) {
        this.params = params;
    }


    public interface OnMovingTargetSelectedListener {
        void onMovingTargetSelected(String[] cityAndArea);
    }


    private static final class Params {
        private boolean shadow = true;
        private boolean canCancel = true;
        private LoopView loopType;
        private LoopView loopValue;
        private int initSelection;
        private OnMovingTargetSelectedListener callback;
        private Map<String, List<String>> dataList;

    }

    public static class Builder {
        private final Context context;
        private final MovingTargetPickerDialog.Params params;

        public Builder(Context context) {
            this.context = context;
            params = new MovingTargetPickerDialog.Params();

            try {
                InputStreamReader inputReader = new InputStreamReader(context.getAssets().open("target_data.json"));
                BufferedReader bufReader = new BufferedReader(inputReader);
                String line = "";
                StringBuffer result = new StringBuffer();
                while ((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                params.dataList = new Gson().fromJson(result.toString(), new TypeToken<Map<String, List<String>>>() {
                }.getType());

            } catch (Exception e) {
                Log.e("RegionPickerDialog", "The Region source file does not exist or has been damaged");
                params.dataList = new HashMap<>();
            }
        }

        private final String[] getCurrRegionValue() {
            return new String[]{params.loopType.getCurrentItemValue(), params.loopValue.getCurrentItemValue()};
        }

        public Builder setSelection(int initSelection) {
            params.initSelection = initSelection;
            return this;
        }

        public Builder setOnMovingTargetSelectedListener(OnMovingTargetSelectedListener onMovingTargetSelectedListener) {
            params.callback = onMovingTargetSelectedListener;
            return this;
        }

        public MovingTargetPickerDialog create() {
            final MovingTargetPickerDialog dialog = new MovingTargetPickerDialog(context, params.shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_picker_moving_target, null);

            view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    params.callback.onMovingTargetSelected(getCurrRegionValue());
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            final LoopView loopType = (LoopView) view.findViewById(R.id.loop_type);
            loopType.setArrayList(new ArrayList(params.dataList.keySet()));
            Log.d("result",loopType.arrayList.toString());
            loopType.setNotLoop();
            if (params.dataList.size() > 0){
                loopType.setCurrentItem(params.initSelection);
            }
            Log.d("result",params.initSelection+"");
            final LoopView loopValue = (LoopView) view.findViewById(R.id.loop_value);
            String selectedCity = loopType.getCurrentItemValue();

            Log.d("result",loopType.getCurrentItemValue()+" "+loopType.getCurrentItem());
            loopValue.setArrayList(params.dataList.get(selectedCity));
            Log.d("result",loopValue.arrayList.toString()+"");
            loopValue.setNotLoop();

            loopType.setListener(new LoopListener() {
                @Override
                public void onItemSelect(int item) {
                    String selectedCity = loopType.getCurrentItemValue();
                    loopValue.setArrayList(params.dataList.get(selectedCity));
                }
            });

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            if (SystemUtils.MAX_WIDTH != 0){
                lp.width = SystemUtils.MAX_WIDTH-100;
            }else {
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            }

            //Log.d("match",""+WindowManager.LayoutParams.MATCH_PARENT);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            win.setGravity(Gravity.CENTER);
            win.setWindowAnimations(R.style.Animation_Bottom_Rising);

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(params.canCancel);
            dialog.setCancelable(params.canCancel);

            params.loopType = loopType;
            params.loopValue = loopValue;
            dialog.setParams(params);

            return dialog;
        }
    }
}
