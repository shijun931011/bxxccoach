package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/14.
 *
 * 学员信息
 */

public class StuMsg {

    private int code;
    private String reason;
    private List<Result> result;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public class Result{
        private String file;
        private String name;
        private String uid;
        private String state;
        private String time;
        private String phone;
        private int countTime;

        public String getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public String getUid() {
            return uid;
        }

        public String getState() {
            return state;
        }

        public String getTime() {
            return time;
        }

        public String getPhone() {
            return phone;
        }

        public int getCountTime() {
            return countTime;
        }

    }

}
