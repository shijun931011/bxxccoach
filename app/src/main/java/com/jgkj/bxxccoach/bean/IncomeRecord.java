package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/8.
 */

public class IncomeRecord {
    private int code;
    private String reason;                //200入账成功
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
        private String time;
        private String p1;
        private String name;
        private String p2;
        private String state;
        private String p3;
        private String time_slot;
        private String p4;
        private String amount_source;


        public String getTime() {
            return time;
        }

        public String getP1() {
            return p1;
        }

        public String getName() {
            return name;
        }

        public String getP2() {
            return p2;
        }

        public String getP3() {
            return p3;
        }

        public String getState() {
            return state;
        }

        public String getTime_slot() {
            return time_slot;
        }

        public String getP4() {
            return p4;
        }

        public String getAmount_source() {
            return amount_source;
        }
    }
}
