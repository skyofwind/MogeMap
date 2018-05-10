package com.example.dzj.mogemap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.modle.MyPoint;


/**
 * Created by dzj on 2017/12/14.
 */

public class RainbowView extends View {

    private Paint mPaint;
    private int WIDTH_DEFAULT = 400;
    private int HEIGHT_DEFAULT = 400;
    private float bigPaintWidth = 0;
    private float bigRadius = 0;//大圆弧半径
    private float smallPaintWidth = 0;
    private float smallRadius = 0;
    private int countColor;
    private int intensityColor;
    private int textDefaultColor;
    private int textCountColor;
    private int rainbowBgColor;

    private int mWidth, mHeight;

    private int stepCount = 0;
    private int instensityTime = 0;

    public RainbowView(Context context) {
        this(context, null);
    }

    public RainbowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainbowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RainbowView);
        countColor = a.getColor(R.styleable.RainbowView_count_color, getColor(context, R.color.colorBluish));
        intensityColor = a.getColor(R.styleable.RainbowView_intensity_color, getColor(context,R.color.colorGlassGreen));
        textDefaultColor = a.getColor(R.styleable.RainbowView_text_default_color, getColor(context,R.color.colorDarkGray));
        textCountColor = a.getColor(R.styleable.RainbowView_text_count_color, getColor(context,R.color.colorSapphire));
        rainbowBgColor = a.getColor(R.styleable.RainbowView_rainbow_bg_color, getColor(context,R.color.colorBackgroundRainbow));
        bigPaintWidth = a.getFloat(R.styleable.RainbowView_big_paint_width, 40f);
        bigRadius = a.getFloat(R.styleable.RainbowView_big_radius, 300f);
        a.recycle();
        init();
    }

    private void init() {
        if(mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }
        if(bigPaintWidth != 0 ) {
            smallPaintWidth = getSmallPaintWidth(bigPaintWidth);
        }
        if(smallPaintWidth != 0 && bigRadius != 0) {
            smallRadius = getSmallRadius(bigRadius, bigPaintWidth, smallPaintWidth);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
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
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        //canvas.drawARGB(85,0,0,0);
        canvas.translate(mWidth/2, bigPaintWidth+smallPaintWidth);
        drawBigRainbow(canvas, bigPaintWidth, bigRadius, rainbowBgColor, 180, 180);
        drawCountRainbow(canvas, bigPaintWidth, bigRadius, countColor, 180, stepCount);
        canvas.save();
        canvas.translate(0, bigPaintWidth/2+smallPaintWidth/2+10);
        drawSmallRainbow(canvas, smallPaintWidth, smallRadius, rainbowBgColor, 180, 180);
        drawInstensityRainbow(canvas, smallPaintWidth, smallRadius, intensityColor, 180, instensityTime);
        canvas.restore();
        drawText(canvas, stepCount,instensityTime);
    }
    //绘制大圆端弧圈
    private void drawBigRainbow(Canvas canvas, float paintWidth, float radius, int color, float startAngle, float sweepAngle){
        drawArc(canvas, paintWidth, radius, color, startAngle, sweepAngle);
        drawEndpoint(canvas, paintWidth, radius, color, startAngle,sweepAngle);
    }
    //绘制小圆端弧圈
    private void drawSmallRainbow(Canvas canvas, float paintWidth, float radius, int color, float startAngle, float sweepAngle){
        drawArc(canvas, paintWidth, radius, color, startAngle, sweepAngle);
        drawSmallEndpoint(canvas, paintWidth, radius, color, startAngle, sweepAngle);
    }
    //绘制步数圆弧进度条的实现进度
    private void drawCountRainbow(Canvas canvas, float paintWidth, float radius, int color, float startAngle, int stepCount){
        float sweepAngle = (float) (stepCount)/10000*180;
        Log.i("sw","count="+sweepAngle);
        drawBigRainbow(canvas, paintWidth, radius, color, startAngle, sweepAngle);
    }
    //绘制运动强度圆弧进度条的实现进度
    private void drawInstensityRainbow(Canvas canvas, float paintWidth, float radius, int color, float startAngle, int instensityTime){
        float sweepAngle = (float)instensityTime/60*180;
        Log.i("sw","time="+sweepAngle);
        drawSmallRainbow(canvas, paintWidth, radius, color, startAngle, sweepAngle);
    }

    //绘制一个半圆弧
    private void drawArc(Canvas canvas, float paintWidth, float radius, int color, float startAngle, float sweepAngle){
        if(sweepAngle == 0){
            return;
        }
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(paintWidth);
        RectF rectFb = new RectF(-radius, 0, radius, radius*2);
        canvas.drawArc(rectFb, startAngle, sweepAngle, false, mPaint);
    }
    //绘制圆弧的圆端
    private void drawEndpoint(Canvas canvas, float paintWidth, float radius, int color, float startAngle,float sweepAngle){
        if(sweepAngle == 0){
            return;
        }
        mPaint.setColor(color);
        mPaint.setStrokeWidth(paintWidth/2);
        mPaint.setStyle(Paint.Style.FILL);

        //画首端半圆
        float angle = getNormalAngle(startAngle);
        Log.i("quadrant","startAngle="+startAngle+" angle="+angle);
        MyPoint point;
        point = getPoint(radius, radius, paintWidth/2, getNormalAngle(startAngle), getQuadrant(startAngle));
        RectF rectFb = new RectF(point.getFloatX()-paintWidth/2, point.getFloatY()-paintWidth/2, point.getFloatX()+paintWidth/2, point.getFloatY()+paintWidth/2);
        //canvas.save();
        //canvas.translate(0, -1);
        canvas.drawArc(rectFb, startAngle, -180, false, mPaint);//首端-180度
        //canvas.restore();
        //画尾端半圆
        angle = getNormalAngle(startAngle+sweepAngle);
        point = getPoint(radius, radius, paintWidth/2, getNormalAngle(startAngle+sweepAngle), getEndQuadrant(startAngle, sweepAngle));
        rectFb = new RectF(point.getFloatX()-paintWidth/2, point.getFloatY()-paintWidth/2, point.getFloatX()+paintWidth/2, point.getFloatY()+paintWidth/2);
        Log.i("quadrant","startAngle+ sweepAngle="+(startAngle+sweepAngle)+" angle="+angle);
        //canvas.save();
        //canvas.translate(0, -1);
        canvas.drawArc(rectFb, startAngle+sweepAngle, 180, false, mPaint);//首端-180度
        //canvas.restore();

    }
    //绘制小圆弧的圆端，因要低端对齐所以比起正常绘制圆端增长了圆端的长度
    private void drawSmallEndpoint(Canvas canvas, float paintWidth, float radius, int color, float startAngle,float sweepAngle){
        if(sweepAngle == 0){
            return;
        }
        mPaint.setColor(color);
        mPaint.setStrokeWidth(paintWidth/2);
        mPaint.setStyle(Paint.Style.FILL);

        //画首端半圆
        float angle = getNormalAngle(startAngle);
        MyPoint point;

        point = getPoint(radius, radius, paintWidth/2, getNormalAngle(startAngle), getQuadrant(startAngle));

        RectF rectFb ;
        if(angle == 0 || angle == 180){
            rectFb = new RectF(point.getFloatX()-paintWidth/2, point.getFloatY()-paintWidth, point.getFloatX()+paintWidth/2, point.getFloatY()+paintWidth);
        }else {
            rectFb = new RectF(point.getFloatX()-paintWidth/2, point.getFloatY()-paintWidth/2, point.getFloatX()+paintWidth/2, point.getFloatY()+paintWidth/2);
        }
        canvas.drawArc(rectFb, angle, -180, false, mPaint);//首端-180度
        //画尾端半圆
        angle = getNormalAngle(startAngle+sweepAngle);
        point = getPoint(radius, radius, paintWidth/2, getNormalAngle(startAngle+sweepAngle), getEndQuadrant(startAngle, sweepAngle));
        Log.i("swdsds",(point.getFloatX()-paintWidth/2)+" "+ (point.getFloatY()-paintWidth/2)+" "+ (point.getFloatX()+paintWidth/2)+" "+ (point.getFloatY()+paintWidth/2));
        if(angle == 0 || angle == 180){
            rectFb = new RectF(point.getFloatX()-paintWidth/2, point.getFloatY()-paintWidth, point.getFloatX()+paintWidth/2, point.getFloatY()+paintWidth);
        }else {
            rectFb = new RectF(point.getFloatX()-paintWidth/2, point.getFloatY()-paintWidth/2, point.getFloatX()+paintWidth/2, point.getFloatY()+paintWidth/2);
        }
        canvas.drawArc(rectFb, angle, 180, false, mPaint);//首端-180度
    }
    //绘制文字
    private void drawText(Canvas canvas, int stepCount, int instensityTime){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setColor(textDefaultColor);
        paint.setTextSize(24f);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseY = bigRadius;
        float textHeight = fontMetrics.bottom-fontMetrics.top;
        canvas.drawText("中高强度 "+instensityTime+"/60分钟",0, baseY, paint);

        //绘制使用clipRect截取显示单个红字
        canvas.save();
        float textAll = paint.measureText("中高强度 "+instensityTime+"/60分钟");
        float textLeft = paint.measureText("中高强度 ");
        float textvalue = paint.measureText(instensityTime+"");
        canvas.clipRect(-textAll/2+textLeft, baseY-textHeight, -textAll/2+textLeft+textvalue, baseY);
        paint.setColor(intensityColor);
        canvas.drawText("中高强度 "+instensityTime+"/60分钟",0, baseY, paint);
        canvas.restore();

        paint.setTextSize(28f);
        float strWidth2 = paint.measureText("步")+10;
        canvas.save();
        canvas.translate(-strWidth2/2,0);

        paint.setColor(textCountColor);
        paint.setTextSize(60f);
        fontMetrics = paint.getFontMetrics();
        baseY = baseY-textHeight-18;
        textHeight = fontMetrics.bottom-fontMetrics.top;
        String countStr = String.valueOf(stepCount);
        float strWidth1 = paint.measureText(countStr);
        canvas.drawText(countStr, 0, baseY, paint);

        paint.setTextSize(30f);
        canvas.restore();
        canvas.drawText("步", strWidth1/2, baseY, paint);

        paint.setColor(textDefaultColor);
        baseY = baseY-textHeight;
        canvas.drawText("目标 "+10000, 0, baseY, paint);

    }
    private float getSmallPaintWidth(float bigPaintWidth){
        return bigPaintWidth/2;
    }
    private float getSmallRadius(float bigRadius, float bigPaintWidth, float smallPaintWidth){
        return bigRadius-bigPaintWidth/2-smallPaintWidth/2-10;
    }
    public synchronized void setStepCount(int stepCount){
        if(this.stepCount == stepCount){
            return;
        }
        this.stepCount = stepCount;
        invalidate();
    }
    public synchronized void setInstensityTime(int instensityTime){
        if(this.instensityTime == instensityTime){
            return;
        }
        this.instensityTime = instensityTime;
        invalidate();
    }

    //获取点坐标
    private MyPoint getPoint(float radius, float oldRadius, float offsetR,float angle, int quadrant){
        MyPoint point = new MyPoint() ;
        switch (quadrant) {
            case 1:
                point = calculatePoint(angle, radius, offsetR);
                point.setX(point.getX());
                point.setY(oldRadius+point.getY());
                point.setAngle(angle);
                point.setLeft(false);
                break;
            case 2:
                point = calculatePoint(180-angle, radius, offsetR);
                point.setX(-point.getX());
                point.setY(oldRadius+point.getY());
                point.setAngle(180-angle);
                break;
            case 3:
                point = calculatePoint(angle-180, radius, offsetR);
                point.setX(-point.getX());
                point.setY(oldRadius-point.getY());
                point.setAngle(angle-180);
                break;
            case 4:
                point = calculatePoint(360-angle, radius, offsetR);
                point.setX(point.getX());
                point.setY(oldRadius-point.getY());
                point.setAngle(360-angle);
                point.setLeft(false);
                break;
        }
        return point;
    }
    private MyPoint calculatePoint(float angle, float radius, float offsetR){
        double angrad = Math.toRadians(angle);
        double x = Math.cos(angrad)*(radius);
        double y = Math.sin(angrad)*(radius);
        double offsetX = Math.sin(angrad)*offsetR;
        double offsetY = Math.cos(angrad)*offsetR;
        if(angle ==0){
            y = 0;
            offsetX = 0;
        }
        if(angle == 90){
            x = 0;
            offsetY = 0;
        }
        if(angle == 180){
            y = 0;
            offsetX = 0;
        }
        if(angle == 270){
            x = 0;
            offsetY = 0;
        }
        MyPoint point = new MyPoint(x, y, offsetX, offsetY);
        return point;
    }
    //获取角度所在象限
    private int getQuadrant(float angle){
        int quadrant;
        angle = getNormalAngle(angle);
        if(angle >= 270 || angle == 0) {
            quadrant = 4;
        } else if(angle >= 180) {
            quadrant = 3;
        } else if(angle >= 90) {
            quadrant = 2;
        } else {
            quadrant = 1;
        }
        return quadrant;
    }
    //获取终点所在象限
    private int getEndQuadrant(float startAngle, float sweepAngle){
        float mAngle = startAngle+sweepAngle;
        int quadrant = getQuadrant(mAngle);
        return quadrant;
    }
    //简化角度为正数
    private float getNormalAngle(float angle){
        while (angle < 0){
            angle = angle+360;
        }
        while(angle >= 360){
            angle = angle-360;
        }
        return angle;
    }
    private void exchangePoint(MyPoint point1, MyPoint point2) {
        MyPoint point = new MyPoint();
        point.setX(point1.getX());
        point.setY(point1.getY());
        point.setLeft(point.isLeft());

        point1.setX(point2.getX());
        point1.setY(point2.getY());
        point1.setLeft(point2.isLeft());

        point2.setX(point.getX());
        point2.setY(point.getY());
        point2.setLeft(point.isLeft());
    }
    private void pointIsLefe(MyPoint point1, MyPoint point2) {
        if(!point1.isLeft() && !point2.isLeft()){
            exchangePoint(point1, point2);
        }
    }
    private int getColor(Context context, int id){
        int rid = -1;
        if(Build.VERSION.SDK_INT >= 23){
            rid = context.getColor(id);
        }else {
            rid = context.getResources().getColor(id);
        }
        return rid;
    }
}
