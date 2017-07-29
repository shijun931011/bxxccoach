package com.jgkj.bxxccoach.bean.entity.AllRouteEntity;


import com.jgkj.bxxccoach.bean.entity.BroadcastEntity.BroadcastEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/6/7.
 */

public class AllRouteResult {

    private int code;

    private String reason;

    private List<AllRouteEntity> result ;

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

    public List<AllRouteEntity> getResult() {
        return result;
    }

    public void setResult(List<AllRouteEntity> result) {
        this.result = result;
    }
}
