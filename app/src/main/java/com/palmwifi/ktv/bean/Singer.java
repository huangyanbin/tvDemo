package com.palmwifi.ktv.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David on 2017/3/27.
 */

public class Singer {

    @SerializedName("singername")
    public String name;

    private String tid;
    @SerializedName("headicon")
    public String url;

    public Singer(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
