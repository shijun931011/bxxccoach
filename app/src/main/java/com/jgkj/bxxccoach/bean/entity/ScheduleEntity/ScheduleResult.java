package com.jgkj.bxxccoach.bean.entity.ScheduleEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/6/7.
 */

public class ScheduleResult {

    private int code;

    private String reason;

    private List<ScheduleEntity> result ;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ScheduleEntity> getResult() {
        return result;
    }

    public void setResult(List<ScheduleEntity> result) {
        this.result = result;
    }
}
