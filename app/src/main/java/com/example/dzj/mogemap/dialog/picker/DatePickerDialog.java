package com.example.dzj.mogemap.dialog.picker;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.utils.SystemUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;



public class DatePickerDialog extends Dialog {

    private static final int MIN_YEAR = 1900;
    private Params params;

    public DatePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void setParams(DatePickerDialog.Params params) {
        this.params = params;
    }

    public interface OnDateSelectedListener {
         void onDateSelected(int[] dates);
    }



    private static final class Params {
        private boolean shadow = true;
        private boolean canCancel = true;
        private LoopView loopYear, loopMonth, loopDay;
        private OnDateSelectedListener callback;
        private int[] mydata;
    }

    public static class Builder {
        private final Context context;
        private final DatePickerDialog.Params params;


        public Builder(Context context) {
            this.context = context;
            params = new DatePickerDialog.Params();
        }
        public Builder setChoose(int[] datas) {
            params.mydata = datas;
            return this;
        }
        /**
         * 获取当前选择的日期
         *
         * @return int[]数组形式返回。例[1990,6,15]
         */
        private final int[] getCurrDateValues() {
            int currYear = Integer.parseInt(params.loopYear.getCurrentItemValue());
            int currMonth = Integer.parseInt(params.loopMonth.getCurrentItemValue());
            int currDay = Integer.parseInt(params.loopDay.getCurrentItemValue());
            return new int[]{currYear, currMonth, currDay};
        }

        public Builder setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
            params.callback = onDateSelectedListener;
            return this;
        }


        public DatePickerDialog create() {
            final DatePickerDialog dialog = new DatePickerDialog(context, params.shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.layout_picker_date, null);

            final LoopView loopDay = (LoopView) view.findViewById(R.id.loop_day);
            List<String> list = d(1, 30);
            loopDay.setArrayList(list);
            if(params.mydata != null){
                int mDay = params.mydata[2];
                for(int i = 0; i < list.size(); i++){
                    int temp = Integer.parseInt(list.get(i));
                    if (mDay == temp){
                        loopDay.setCurrentItem(i);
                        break;
                    }
                }
            }else {
                loopDay.setCurrentItem(15);
            }
            loopDay.setNotLoop();

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            final LoopView loopYear = (LoopView) view.findViewById(R.id.loop_year);
            list = d(MIN_YEAR, year - MIN_YEAR + 1);
            loopYear.setArrayList(list);
            if(params.mydata != null){
                int mYear = params.mydata[0];
                for(int i = 0; i < list.size(); i++){
                    int temp = Integer.parseInt(list.get(i));
                    if (mYear == temp){
                        loopYear.setCurrentItem(i);
                        break;
                    }
                }
            }else {
                loopYear.setCurrentItem(year - MIN_YEAR - 25);
            }
            //
            loopYear.setNotLoop();

            final LoopView loopMonth = (LoopView) view.findViewById(R.id.loop_month);
            list = d(1, 12);
            loopMonth.setArrayList(list);
            if(params.mydata != null) {
                int mMonth = params.mydata[1];
                for(int i = 0; i < list.size(); i++){
                    int temp = Integer.parseInt(list.get(i));
                    if (mMonth == temp){
                        loopMonth.setCurrentItem(i);
                        break;
                    }
                }
            }else {
                loopMonth.setCurrentItem(6);
            }
            loopMonth.setNotLoop();

            final LoopListener maxDaySyncListener = new LoopListener() {
                @Override
                public void onItemSelect(int item) {
                    Calendar c = Calendar.getInstance();
                    c.set(Integer.parseInt(loopYear.getCurrentItemValue()), Integer.parseInt(loopMonth.getCurrentItemValue()) - 1, 1);
                    c.roll(Calendar.DATE, false);
                    int maxDayOfMonth = c.get(Calendar.DATE);
                    int fixedCurr = loopDay.getCurrentItem();
                    loopDay.setArrayList(d(1, maxDayOfMonth));
                    // 修正被选中的日期最大值
                    if (fixedCurr > maxDayOfMonth) fixedCurr = maxDayOfMonth - 1;
                    loopDay.setCurrentItem(fixedCurr);
                }
            };
            loopYear.setListener(maxDaySyncListener);
            loopMonth.setListener(maxDaySyncListener);

            view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    params.callback.onDateSelected(getCurrDateValues());
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = SystemUtils.MAX_WIDTH-50;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.y = 30;
            win.setAttributes(lp);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.Animation_Bottom_Rising);

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(params.canCancel);
            dialog.setCancelable(params.canCancel);

            params.loopYear = loopYear;
            params.loopMonth = loopMonth;
            params.loopDay = loopDay;
            dialog.setParams(params);

            return dialog;
        }

        /**
         * 将数字传化为集合，并且补充0
         *
         * @param startNum 数字起点
         * @param count    数字个数
         * @return
         */
        private static List<String> d(int startNum, int count) {
            String[] values = new String[count];
            for (int i = startNum; i < startNum + count; i++) {
                String tempValue = (i < 10 ? "0" : "") + i;
                values[i - startNum] = tempValue;
            }
            return Arrays.asList(values);
        }
    }
}
