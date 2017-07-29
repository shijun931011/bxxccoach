package com.jgkj.bxxccoach.bean.entity.UserRouteEntity;

/**
 * Created by tongshoujun on 2017/7/24.
 */

public class UserRouteEntity {
    private String lat_lon;//经纬度
    private String name;
    private String content;
    private String pic;

    public String getLat_lon() {
        return lat_lon;
    }

    public void setLat_lon(String lat_lon) {
        this.lat_lon = lat_lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
