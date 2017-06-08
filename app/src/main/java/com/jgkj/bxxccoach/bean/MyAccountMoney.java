package com.jgkj.bxxccoach.bean;

/**
 * Created by Administrator on 2017/6/8.
 */

public class MyAccountMoney {
    private int code;

    private String reason;

    private Result result;

    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setReason(String reason){
        this.reason = reason;
    }
    public String getReason(){
        return this.reason;
    }
    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
        return this.result;
    }

    public class Result
    {
        private String totalMoney;

        private String todayMoney;

        public void setTotalMoney(String totalMoney){
            this.totalMoney = totalMoney;
        }
        public String getTotalMoney(){
            return this.totalMoney;
        }
        public void setTodayMoney(String todayMoney){
            this.todayMoney = todayMoney;
        }
        public String getTodayMoney(){
            return this.todayMoney;
        }
    }

}
