package com.palmwifi.base;

import android.app.Application;
import android.content.Context;

import com.palmwifi.http.CacheInterceptor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by David on 2016/12/1.
 */

public class BaseApplication extends Application {

    private static  BaseApplication mApplication;


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    /**
     * 返回上下文
     *
     * @return
     */
    public static Context getContext() {
        return mApplication;
    }



    /**
     * 初始化OKHttp
     */
    public void initOkHttpClient(boolean isLogDisable, String logName,CacheInterceptor cacheInterceptor){

        File cacheFile = new File(getExternalCacheDir(),"httpCache");
        Cache cache = new Cache(cacheFile,1024*1024*50);
         OkHttpClient.Builder builder= new OkHttpClient.Builder();
                if(isLogDisable){
                    builder.addInterceptor(new LoggerInterceptor(logName));
                }
        OkHttpClient okHttpClient =builder.addNetworkInterceptor(cacheInterceptor)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .writeTimeout(10000,TimeUnit.MILLISECONDS)
                .readTimeout(10000,TimeUnit.MILLISECONDS)
                .addInterceptor(cacheInterceptor)
                .cache(cache)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }



}
