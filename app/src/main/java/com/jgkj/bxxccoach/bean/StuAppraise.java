package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/14.
 * 学员评价
 */

public class StuAppraise {
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

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public class Result{
        /**
         * 评论内容
         */
        private String comment;
        /**
         * 评论时间
         */
        private String comment_time;
        /**
         * 教学质量
         */
        private String teach;
        /**
         * 服务态度
         */
        private String wait;

        public String getComment() {
            return comment;
        }

        public String getComment_time() {
            return comment_time;
        }

        public String getWait() {
            return wait;
        }

        public String getTeach() {
            return teach;
        }

    }

}
