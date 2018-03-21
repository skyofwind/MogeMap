package com.example.dzj.theweather.json_analysis;

import java.util.List;

/**
 * Created by dzj on 2017/2/23.
 */

public class HEWeather5 {
    private Basic basic;
    private List<DailyForecast> daily_forecast;
    private Now now;
    private Aqi aqi;
    private Suggestion suggestion;
    private String status;

    public Basic getBasic(){
        return basic;
    }
    public void setBasic(Basic basic){
        this.basic=basic;
    }
    public List<DailyForecast> getForecast(){
        return daily_forecast;
    }
    public void setForecast(List<DailyForecast> daily_forecast){
        this.daily_forecast=daily_forecast;
    }
    public Now getNow(){return now;}
    public void setNow(Now now){this.now=now;}
    public Aqi getAqi(){return aqi;}
    public void setAqi(Aqi aqi){this.aqi=aqi;}
    public Suggestion getSuggestion(){return suggestion;}
    public void setSuggestion(Suggestion suggestion){this.suggestion=suggestion;}
    public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;}
}
