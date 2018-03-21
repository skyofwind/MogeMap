package com.example.dzj.theweather.main;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by dzj on 2017/5/22.
 */

public class LocationInfo implements Parcelable{
    private String Province;
    private String City;
    private String District;
    private String Status;

    protected LocationInfo(Parcel in) {
        Province = in.readString();
        City = in.readString();
        District = in.readString();
        Status=in.readString();
    }

    public static final Creator<LocationInfo> CREATOR = new Creator<LocationInfo>() {
        @Override
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }

        @Override
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };

    public String getCity() {
        return City;
    }

    public String getDistrict() {
        return District;
    }

    public String getProvince() {
        return Province;
    }

    public String getStatus() {
        return Status;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public LocationInfo(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Province);
        dest.writeString(City);
        dest.writeString(District);
        dest.writeString(Status);
    }

    public String toString(){
        return "province:"+Province+" city:"+City+" district"+District+" status:"+Status;
    }
}
