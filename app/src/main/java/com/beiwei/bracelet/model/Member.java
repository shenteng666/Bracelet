package com.beiwei.bracelet.model;

public class Member {
    private Long id;
    private String name;
    private int sex;// 1：男  2：女  3：未知
    private String code;//学号
    private String pname;//班级名称
    private String time;//时间
    private String data;//日期
    private Double temperature;//温度
    private int isWear;//0：未佩戴  1：已佩戴  2：已离线
    private String devmac;//设备mac地址
    private int battery;//设备mac地址

    public Member(Long id, String name, int sex, String code, String pname, String time, String data, Double temperature, int isWear, String devmac, int battery) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.code = code;
        this.pname = pname;
        this.time = time;
        this.data = data;
        this.temperature = temperature;
        this.isWear = isWear;
        this.devmac = devmac;
        this.battery = battery;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public int getIsWear() {
        return isWear;
    }

    public void setIsWear(int isWear) {
        this.isWear = isWear;
    }

    public String getDevmac() {
        return devmac;
    }

    public void setDevmac(String devmac) {
        this.devmac = devmac;
    }
}
