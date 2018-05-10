package com.example.dzj.mogemap.modle;

import java.util.Date;

public class Mogemap_user {
    private Integer id;

    private String phone;

    private String qqid;

    private String weiboid;

    private String sex;

    private Date birthday;

    private Integer height;

    private Integer weight;

    private String headurl;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        if (phone == null){
            phone = "";
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getQqid() {
        return qqid;
    }

    public void setQqid(String qqid) {
        this.qqid = qqid == null ? null : qqid.trim();
    }

    public String getWeiboid() {
        return weiboid;
    }

    public void setWeiboid(String weiboid) {
        this.weiboid = weiboid == null ? null : weiboid.trim();
    }

    public String getSex() {
        if(sex == null){
            return "无";
        }
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Date getBirthday() {
        if(birthday == null){
            return null;
        }
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getHeight() {
        if(height == null){
            return 0;
        }
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        if (weight == null){
            return 0;
        }
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl == null ? null : headurl.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}