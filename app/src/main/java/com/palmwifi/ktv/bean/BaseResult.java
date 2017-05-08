package com.palmwifi.ktv.bean;

/**
 * Created by David on 2017/4/6.
 */

public class BaseResult<T> {

    public static final String SUC = "1";
    public static final String FAIL = "0";

    private String resultCode;
    private String resultDesc;

    private T data;
    private int totalCount;


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
