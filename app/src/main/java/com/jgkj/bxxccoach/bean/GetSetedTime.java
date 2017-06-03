package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/17.
 * 获取教练已排课的时间段
 */

public class GetSetedTime {
    private int code;
    private String reason;
    private List<Result> result;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public class  Result{
        private String  day;
        private List<String> time_slot;

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

}
