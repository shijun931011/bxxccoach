package com.jgkj.bxxccoach.bean;

/**
 * Created by fangzhou on 2017/1/17.
 */

public class CreateDay_Time {

    private String day;
    private String time1;
    private String time2;
    private String time3;
    private String time4;
    private String time5;
    private boolean isApp4;
    private boolean isApp5;
    private boolean isApp1;
    private boolean isApp2;
    private boolean isApp3;

    public CreateDay_Time(String day, String time1, String time2, String time3, String time4,
                          String time5, boolean isApp1, boolean isApp2, boolean isApp3
            , boolean isApp4, boolean isApp5) {
        this.day = day;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time2 = time4;
        this.time3 = time5;
        this.isApp1 = isApp4;
        this.isApp2 = isApp5;
        this.isApp1 = isApp1;
        this.isApp2 = isApp2;
        this.isApp3 = isApp3;
    }

    public String getTime4() {
        return time4;
    }

    public void setTime4(String time4) {
        this.time4 = time4;
    }

    public boolean isApp4() {
        return isApp4;
    }

    public void setApp4(boolean app4) {
        isApp4 = app4;
    }

    public String getTime5() {
        return time5;
    }

    public void setTime5(String time5) {
        this.time5 = time5;
    }

    public boolean isApp5() {
        return isApp5;
    }

    public void setApp5(boolean app5) {
        isApp5 = app5;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTime3() {
        return time3;
    }

    public void setTime3(String time3) {
        this.time3 = time3;
    }

    public boolean isApp1() {
        return isApp1;
    }

    public void setApp1(boolean app1) {
        isApp1 = app1;
    }

    public boolean isApp2() {
        return isApp2;
    }

    public void setApp2(boolean app2) {
        isApp2 = app2;
    }

    public boolean isApp3() {
        return isApp3;
    }

    public void setApp3(boolean app3) {
        isApp3 = app3;
    }
}
