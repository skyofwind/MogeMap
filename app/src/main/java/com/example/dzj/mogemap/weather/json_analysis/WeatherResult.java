package com.example.dzj.mogemap.weather.json_analysis;

/**
 * Created by dzj on 2017/2/23.
 */

public class WeatherResult {
    private String code;
    private String msg;
    private String charge;
    private Result result;

    public String getCode(){return  code;}
    public void setCode(String code){this.code=code;}
    public String getMsg(){return  msg;}
    public void setMsg(String msg){this.msg=msg;}
    public String getCharge(){return  charge;}
    public void setCharge(String code){this.charge=charge;}
    public Result getResult(){return result;}
    public void setResult(Result result){this.result=result;}
}
