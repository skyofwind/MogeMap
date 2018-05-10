package com.example.dzj.mogemap.weather.json_analysis;

/**
 * Created by dzj on 2017/3/15.
 */

public class City_basic {
    private String city;
    private String cnty;
    private String id;
    private String lat;
    private String lon;
    private String prov;

    public String getCity() {
        return city;
    }

    public String getCnty() {
        return cnty;
    }

    public String getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getProv() {
        return prov;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }
}
