package com.palmwifi.ktv.comm;

import android.database.Cursor;
import android.net.Uri;

import com.palmwifi.ktv.utils.PreferenceUtil;


/**
 * Created by David on 2017/3/29.
 */

public class UserManager {


    private String userInfo;
    private boolean isLogin;
    private static UserManager mInstance;
    private String tradeNo;
    private boolean isVip;

    public static UserManager getInstance() {
        if (mInstance == null) {
            synchronized (UserManager.class) {
                if (mInstance == null) {
                    mInstance = new UserManager();
                }
            }
        }
        return mInstance;
    }


    private UserManager(){
        initUserInfo();
    }

    private void initUserInfo() {
        Uri uri = Uri.parse("content://stbconfig/authentication");
        Cursor mCursor = KApplication.getContext().getContentResolver().query(uri, null, null, null, null);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                String name = mCursor.getString(mCursor.getColumnIndex("name"));
                if (name.equals("username")) {
                    userInfo = mCursor.getString(mCursor.getColumnIndex("value"));
                    isLogin = true;
                    break;
                }
            }
            mCursor.close();
        }
        tradeNo = PreferenceUtil.getString("tradeNO");
        if(tradeNo != null){
            isVip = true;
        }
    }

    public void setVip(boolean isVip){
        this.isVip = isVip;
    }

    public boolean isLogin(){
        return isLogin;
    }

    public String getUserName(){

        if(userInfo == null){
            return "游客";
        }
        return userInfo;
    }


    public String getUserID(){

        if(userInfo == null){
            return "-1";
        }
        return userInfo;
    }

    public boolean isVip() {
        return isVip;
    }

    public void saveVip(String tradeNo){

        if(tradeNo != null){
            this.tradeNo = tradeNo;
            this.isVip = true;
            PreferenceUtil.putString("tradeNO",tradeNo);
        }
    }

    public String getTradeNo() {
        return tradeNo;
    }

}
