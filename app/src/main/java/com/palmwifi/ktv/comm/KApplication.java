package com.palmwifi.ktv.comm;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.palmwifi.base.BaseApplication;
import com.palmwifi.http.CacheInterceptor;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by David on 2017/3/27.
 */

public class KApplication extends BaseApplication {

    private static KApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Fresco.initialize(this);
        CrashReport.initCrashReport(getApplicationContext(), "011530db24", false);
        initOkHttpClient(true,"huang",new CacheInterceptor(this));
        UserManager.getInstance();

    }


    public static KApplication getInstance(){
        return mInstance;
    }

}
