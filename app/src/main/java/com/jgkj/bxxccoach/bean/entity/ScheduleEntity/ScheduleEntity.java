package com.jgkj.bxxccoach.bean.entity.ScheduleEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/6/7.
 */

public class ScheduleEntity {

    private String day;

    private List<String> time_slot ;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getTime_slot() {
        return time_slot;
    }

    public void setTime_slot(List<String> time_slot) {
        this.time_slot = time_slot;
    }
}
