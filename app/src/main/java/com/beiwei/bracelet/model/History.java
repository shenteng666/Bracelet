package com.beiwei.bracelet.model;


/**
 * 
 * @author sunfreeter
 * @desc 历史记录
 */
public class History{
    private int isWear;
    private String temperature;
    private String time;
    private String date;

    public History(int isWear, String temperature, String time, String date) {
        this.isWear = isWear;
        this.temperature = temperature;
        this.time = time;
        this.date = date;
    }
    public History() {
        super();
    }

    public int getIsWear() {
        return isWear;
    }

    public void setIsWear(int isWear) {
        this.isWear = isWear;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}