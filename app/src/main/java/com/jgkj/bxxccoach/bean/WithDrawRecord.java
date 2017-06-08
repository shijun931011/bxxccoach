package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/8.
 */

public class WithDrawRecord {
    private int code;

    private String reason;

    private List<Result> result;

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
    public void setResult(List<Result> result){
        this.result = result;
    }
    public List<Result> getResult(){
        return this.result;
    }

    public class Result{
        private String time;

        private String money;

        private String status;

        private String toAccount;

        public void setTime(String time){
            this.time = time;
        }
        public String getTime(){
            return this.time;
        }
        public void setMoney(String money){
            this.money = money;
        }
        public String getMoney(){
            return this.money;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setToAccount(String toAccount){
            this.toAccount = toAccount;
        }
        public String getToAccount(){
            return this.toAccount;
        }
    }

}
