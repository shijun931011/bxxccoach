package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by tongshoujun on 2017/5/26.
 */

public class BannerResult {
    private int code;

    private String reason;

    private List<String> result;

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

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
