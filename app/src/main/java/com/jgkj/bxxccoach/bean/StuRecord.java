package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/14.
 *
 * 学员学车记录
 */

public class StuRecord {
    /**
     * 返回码
     */
    private int code;
    /**
     * 返回原因
     */
    private String reason;
    /**
     * 返回结果
     */
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

    public class Result {
        /**
         * 预约天数
         */
        private String day;
        /**
         * 预约时间段
         */
        private String time_slot;
        /**
         * 预约是否预约过
         */
        private String statement;
        /**
         * 0 教学 1 陪练
         */
        private String class_style;

        public String getClass_style() {
            return class_style;
        }

        public void setClass_style(String class_style) {
            this.class_style = class_style;
        }

        public String getDay() {
            return day;
        }

        public String getTime_slot() {
            return time_slot;
        }

        public String getStatement() {
            return statement;
        }
    }

}
