package com.palmwifi.ktv.comm;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.palmwifi.base.BaseApplication;
import com.palmwifi.http.CacheInterceptor;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;


import okhttp3.OkHttpClient;

/**
 * Created by David on 2017/3/27.
 */

public class KApplication extends BaseApplication {

    private static KApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CrashReport.initCrashReport(getApplicationContext(), "011530db24", false);
        OkHttpClient okHttpClient = initOkHttpClient(true,Contract.LOG_NAME,
                Contract.PROXY_HOST,Contract.PROXY_PORT,new CacheInterceptor(this));
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();
        Fresco.initialize(this,config);
        OkHttpUtils.initClient(okHttpClient);
        UserManager.getInstance();


    }


    public static KApplication getInstance(){
        return mInstance;
    }

}
