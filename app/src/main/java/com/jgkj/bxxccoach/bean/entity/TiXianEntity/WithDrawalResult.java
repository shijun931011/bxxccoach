package com.jgkj.bxxccoach.bean.entity.TiXianEntity;

/**
 * Created by Administrator on 2017/6/9.
 */

public class WithDrawalResult {
    private int code;

    private String reason;

    private WithDrawalEntity withDrawalEntity;

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
    public void setWithDrawalEntity(WithDrawalEntity withDrawalEntity){
        this.withDrawalEntity = withDrawalEntity;
    }
    public WithDrawalEntity getWithDrawalEntity(){
        return this.withDrawalEntity;
    }
}
