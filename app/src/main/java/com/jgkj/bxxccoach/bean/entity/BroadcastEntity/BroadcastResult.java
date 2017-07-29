package com.jgkj.bxxccoach.bean.entity.BroadcastEntity;


import java.util.List;

/**
 * Created by tongshoujun on 2017/6/7.
 */

public class BroadcastResult {

    private int code;

    private String reason;

    private List<BroadcastEntity> result ;

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

    public List<BroadcastEntity> getResult() {
        return result;
    }

    public void setResult(List<BroadcastEntity> result) {
        this.result = result;
    }
}
