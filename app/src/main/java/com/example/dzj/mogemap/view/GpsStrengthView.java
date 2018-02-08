package com.example.dzj.mogemap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.modle.RunRecord;

import java.util.List;

/**
 * Created by dzj on 2018/2/8.
 */

public class GpsStrengthView extends View {
    private final static String TAG = "GpsStrengthView";
    private Context context;
    private int mWidth;
    private int mHeight;
    private float columnWidth;
    private float columnInterval;
    private int strength = 0;
    public GpsStrengthView(Context context) {
        super(context);
        this.context = context;
    }

    public GpsStrengthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public GpsStrengthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        columnInterval = 5;
        drawAll(canvas, paddingLeft, paddingRight, paddingTop, paddingBottom, strength);

    }
    private void drawAll(Canvas canvas, int pl, int pr, int pt, int pb, int strength){
        for(int i=0;i<strength;i++){
            drawModule(canvas, (int)(pl+columnInterval*i), pr, pt, pb);
        }
    }

    private void drawModule(Canvas canvas, int pl, int pr, int pt, int pb){

    }
    private float getOffset(float width){
        float offset = (width < columnInterval)?(columnInterval-width)/2:0;
        return offset;
    }
    private void log(String str){
        Log.i(TAG,str);
    }
    private int getColor(int id){
        return context.getColor(id);
    }
}
