package com.jgkj.bxxccoach.bean;

/**
 * Created by fangzhou on 2017/1/13.
 *
 * 用户信息
 */

public class UserInfo {
    /**
     * 返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String reason;
    /**
     * 返回结果
     */
    private Result result;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }


    public Result getResult() {
        return result;
    }


    public class Result{
        /**
         * id
         */
        private String pid;
        /**
         * class_type
         */
        private String class_type;

        public String getClass_type() {
            return class_type;
        }

        public void setClass_type(String class_type) {
            this.class_type = class_type;
        }

        /**

         * name
         */
        private String cname;

        public void setMaxnum(String maxnum) {
            this.maxnum = maxnum;
        }

        /**

         * 信用
         */
        private String credit;
        /**
         * 好评率
         */
        private String praise;
        /**
         * 通过率
         */
        private String pass;
        /**
         * 教学质量
         */
        private String teach;
        /**
         * 服务质量
         */
        private String wait;
        /**
         * 最大学员数
         */
        private String maxnum;
        /**
         * 头像url
         */
        private String coafile;
        /**
         * 当前学员数
         */
        private String nowStuNum;
        /**
         * 累计学员数
         */
        private String count_stu;
        /**
         * 手机号码
         */
        private String phonenum;
        /**
         * token值
         */
        private String token;

        /**
         * 角色
         */
        private String roles;

        public String getRoles() {
            return roles;
        }

        public void setRoles(String roles) {
            this.roles = roles;
        }

        public String getCenter_id() {
            return tid;
        }

        public void setCenter_id(String center_id) {
            this.tid = center_id;
        }

        /**
         * 角色id
         */

        private String tid;

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        private String toAccount;

        public String getToAccount() {
            return toAccount;
        }

        public String getToken() {
            return token;
        }

        public String getPhonenum() {
            return phonenum;
        }

        public String getCname() {
            return cname;
        }

        public String getPid() {
            return pid;
        }


        public String getCredit() {
            return credit;
        }

        public String getPraise() {
            return praise;
        }

        public String getPass() {
            return pass;
        }

        public String getTeach() {
            return teach;
        }

        public String getWait() {
            return wait;
        }

        public String getMaxnum() {
            return maxnum;
        }

        public String getCoafile() {
            return coafile;
        }

        public String getNowStuNum() {
            return nowStuNum;
        }

        public String getCount_stu() {
            return count_stu;
        }
    }

}
