package com.palmwifi.ktv.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David on 2017/4/6.
 */

public class SingerType {

    @SerializedName("singername")
    private String name;
    private String tid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
