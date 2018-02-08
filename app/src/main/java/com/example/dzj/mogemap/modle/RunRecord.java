package com.example.dzj.mogemap.modle;

/**
 * Created by dzj on 2018/1/21.
 */

public class RunRecord {
    private String date;
    private double distance;

    public RunRecord(){}

    public RunRecord(String date, double distance){
        this.date = date;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
