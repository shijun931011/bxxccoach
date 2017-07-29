package com.jgkj.bxxccoach.bean.entity.GroupMemberEntity;


import com.jgkj.bxxccoach.bean.entity.AllRouteEntity.AllRouteEntity;

import java.util.List;

/**
 * Created by tongshoujun on 2017/6/7.
 */

public class GroupMemberResult {

    private int code;

    private String reason;

    private List<GroupMemberEntity> result ;

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

    public List<GroupMemberEntity> getResult() {
        return result;
    }

    public void setResult(List<GroupMemberEntity> result) {
        this.result = result;
    }
}
