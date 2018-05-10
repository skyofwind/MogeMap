package com.example.dzj.mogemap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.modle.Mogemap_run_record;
import com.example.dzj.mogemap.utils.OtherUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dzj on 2018/1/19.
 */

public class RunRecordView extends View {
    private final static String TAG = "RunRecordView";
    private Context context;
    private int mWidth;
    private int mHeight;
    private float columnWidth;
    private float columnInterval;
    private List<Mogemap_run_record> records;
    private List<Float> heightProportion;

    public RunRecordView(Context context) {
        super(context);
        this.context = context;
    }

    public RunRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RunRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int WIDTH_DEFAULT = 400;
        int HEIGHT_DEFAULT = 400;
        if (widthMeasureSpec == MeasureSpec.AT_MOST && heightSpecSize == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WIDTH_DEFAULT, HEIGHT_DEFAULT);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WIDTH_DEFAULT, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, HEIGHT_DEFAULT);
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        columnWidth = w*26/1071;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        

        columnInterval = getColumnInterval(mWidth, paddingLeft, paddingRight);
        drawAll(canvas, paddingLeft, paddingRight, paddingTop, paddingBottom);

    }
    private void drawAll(Canvas canvas, int pl, int pr, int pt, int pb){
        if(records !=null && records.size() > 0){
            for(int i = 0;i < 7; i++){
                if(i < records.size()){
                    drawModule(canvas, (int)(pl+columnInterval*i), pr, pt, pb, records.get(records.size()-1-i), heightProportion.get(records.size()-1-i));
                }else {
                    drawNullColumn(canvas, (int)(pl+columnInterval*i), pr, pt, pb, i);
                }
            }
        }else {
            for(int i = 0;i < 7; i++){
                drawNullColumn(canvas, (int)(pl+columnInterval*i), pr, pt, pb, i);
            }
        }
    }

    private void drawModule(Canvas canvas, int pl, int pr, int pt, int pb, Mogemap_run_record record, float proportion){
        //低端日期
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setColor(getColor(R.color.colorDarkGray));
        paint.setTextSize(30f);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseY = mHeight-pb;
        float textHeight = fontMetrics.bottom-fontMetrics.top;
        String myDate = getMonthDay(record.getDate());
        float strWidth = paint.measureText(myDate);
        canvas.drawText(myDate, pl+getOffset(strWidth), baseY, paint);
        //圆柱
        int[] colors = new int[]{getColor(R.color.colorYellowStart), getColor(R.color.colorYellowMiddle), getColor(R.color.colorYellowEnd)};
        float cOffset = getOffset(columnWidth);
        float height = (mHeight-pb-pt-3*textHeight)*proportion;
        Shader lg = new LinearGradient(pl+cOffset, mHeight-pb-textHeight-height-columnWidth/2, pl+cOffset+columnWidth, baseY-textHeight, colors, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.REPEAT);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //paint.setStrokeWidth(1f);
        //paint.setColor(context.getColor(R.color.red));
        paint.setShader(lg);
        RectF rectF = new RectF(pl+cOffset, mHeight-pb-textHeight-height, pl+cOffset+columnWidth, baseY-textHeight);
        canvas.drawRect(rectF, paint);
        //paint.setColor(context.getColor(R.color.black));
        rectF = new RectF((float) (pl+cOffset-0.5), (float) (mHeight-pb-textHeight-height-columnWidth/2+0.5), (float)(pl+cOffset+columnWidth-0.5), (float) (mHeight-pb-textHeight-height+columnWidth/2+0.5));
        canvas.drawArc(rectF, 180, 180, false, paint);
        //顶端路程
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30f);
        baseY = mHeight-pb-textHeight-height-columnWidth/2-10;
        strWidth = paint.measureText(OtherUtil.getKM(record.getDistance()));
        canvas.drawText(OtherUtil.getKM(record.getDistance()), pl+getOffset(strWidth), baseY, paint);

    }
    private float getColumnInterval(int w, int pl, int pr){
        float ci = (w-pl-pr)/7;
        return ci;
    }
    private float getOffset(float width){
        float offset = (width < columnInterval)?(columnInterval-width)/2:0;
        return offset;
    }
    private void log(String str){
        Log.i(TAG,str);
    }
    private int getColor(int id){
        if(Build.VERSION.SDK_INT >= 23){
            return context.getColor(id);
        }else {
            return context.getResources().getColor(id);
        }
    }
    public void setRecords(List<Mogemap_run_record> records){
        this.records = records;
        dealHeight();
        invalidate();
    }
    private void drawNullColumn(Canvas canvas, int pl, int pr, int pt, int pb, int postion){
        //低端日期
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setColor(getColor(R.color.colorDarkGray));
        paint.setTextSize(30f);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseY = mHeight-pb;
        float textHeight = fontMetrics.bottom-fontMetrics.top;

        //圆柱
        //int[] colors = new int[]{getColor(R.color.colorYellowStart), getColor(R.color.colorYellowMiddle), getColor(R.color.colorYellowEnd)};
        float cOffset = getOffset(columnWidth);
        float height = (mHeight-pb-pt-3*textHeight);
        //Shader lg = new LinearGradient(pl+cOffset, mHeight-pb-textHeight-height-columnWidth/2, pl+cOffset+columnWidth, baseY-textHeight, colors, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.REPEAT);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //paint.setStrokeWidth(1f);
        paint.setColor(getColor(R.color.colorBgYellow));
        //paint.setShader(lg);
        height = getSY(postion, height);

        RectF rectF = new RectF(pl+cOffset, mHeight-pb-textHeight-height, pl+cOffset+columnWidth, baseY-textHeight);
        canvas.drawRect(rectF, paint);
        rectF = new RectF((float) (pl+cOffset-0.5), (float) (mHeight-pb-textHeight-height-columnWidth/2+0.5), (float)(pl+cOffset+columnWidth-0.5), (float) (mHeight-pb-textHeight-height+columnWidth/2+0.5));
        canvas.drawArc(rectF, 180, 180, false, paint);
    }
    private float getSY(int postion, float height){
        double h = 0;
        switch (postion){
            case 0:
                h = height;
                break;
            case 1:
                h = height*0.9;
                break;
            case 2:
                h = height*0.5;
                break;
            case 3:
                h = height;
                break;
            case 4:
                h = height*0.7;
                break;
            case 5:
                h = height*0.5;
                break;
            case 6:
                h = height*0.7;
                break;
        }
        return (float)h;
    }
    private String getMonthDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return month+"/"+day;
    }
    private void dealHeight(){
        if(records.size() > 0){
            heightProportion = new ArrayList<>();
            double max = records.get(0).getDistance();
            for(int i = 1; i < records.size(); i++){
                if(max < records.get(i).getDistance()){
                    max = records.get(i).getDistance();
                }
            }
            for(int i = 0; i < records.size(); i++){
                heightProportion.add((float)(records.get(i).getDistance()/max));
            }
            for (float f: heightProportion){
                //log("myFloat="+f);
            }
            //log("records.size()="+records.size());
            //log("heightProportion.size()="+heightProportion.size());
        }
    }
}
