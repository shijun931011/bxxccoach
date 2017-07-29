package com.jgkj.bxxccoach.bean.entity.ManagementCreditEntity;

/**
 * Created by tongshoujun on 2017/5/15.
 */

public class ManagementCreditResult {

    private int code;

    private String reason;

    private ManagementEntity result;

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

    public ManagementEntity getResult() {
        return result;
    }

    public void setResult(ManagementEntity result) {
        this.result = result;
    }
}
