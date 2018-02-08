package com.example.dzj.mogemap.modle;

/**
 * Created by dzj on 2017/12/18.
 */

public class MyPoint {
    private double x;
    private double y;
    private double offsetX;
    private double offsetY;
    private float angle;
    private boolean isLeft;

    public MyPoint(){
        this.x = 0;
        this.y = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.isLeft = true;
        this.angle = 0;
    }

    public MyPoint(double x, double y, double offsetX, double offsetY){
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.isLeft = true;
        this.angle = 0;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public float getFloatX() {
        return (float)x;
    }
    public float getFloatY() {
        return (float)y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getOffsetX() {
        return offsetX;
    }
    public double getOffsetY() {
        return offsetY;
    }
    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }
    public boolean isLeft() {
        return isLeft;
    }
    public void setLeft(boolean left) {
        isLeft = left;
    }
    public float getAngle() {
        return angle;
    }
    public void setAngle(float angle) {
        this.angle = angle;
    }
    public String toString(){
        return "x="+x+" y="+y;
    }
}
