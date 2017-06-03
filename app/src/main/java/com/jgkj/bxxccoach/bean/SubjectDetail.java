package com.jgkj.bxxccoach.bean;

import java.util.List;

/**
 * Created by fangzhou on 2017/1/16.
 * 学员预约的课程
 */

public class SubjectDetail {
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
        /**
         * 时间段
         */
        private String time;
        private List<Res> res;

        public String getTime() {
            return time;
        }

        public List<Res> getRes() {
            return res;
        }

        public class Res{
            /**
             * 学员姓名
             */
            private String name;
            /**
             * 学员id
             */
            private String uid;
            /**
             * 学员状态
             */
            private String state;
            /**
             * 学员报名时间
             */
            private String notify_time;
            /**
             * 学员号码
             */
            private String phone;
            /**
             * 学员头像
             */
            private String file;
            /**
             * 0代表教练没有判断
             * 1代表确定来了
             * 2代表确定没来
             */
            private String statement;

            public String getName() {
                return name;
            }

            public String getUid() {
                return uid;
            }

            public String getState() {
                return state;
            }

            public String getNotify_time() {
                return notify_time;
            }

            public String getPhone() {
                return phone;
            }

            public String getFile() {
                return file;
            }

            public String getStatement() {
                return statement;
            }
        }
    }

}
