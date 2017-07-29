package com.jgkj.bxxccoach.bean.entity.UserRouteEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/6/7.
 */

public class UserRouteResult {

    private int code;

    private String reason;

    private List<UserRouteEntity> result ;

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

    public List<UserRouteEntity> getResult() {
        return result;
    }

    public void setResult(List<UserRouteEntity> result) {
        this.result = result;
    }
}
