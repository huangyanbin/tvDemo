package com.palmwifi.http;

import android.content.Context;

import com.palmwifi.utils.NetUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by David on 2016/10/22.
 * 缓存拦截器 可以配置
 * 没有网络状态 使用缓存
 * 有网络的情况下
 */

public class CacheInterceptor implements Interceptor {
    private Context context;


    /**
     * 建议传入Applcation 以免内存泄漏
     *
     * @param context
     */
    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (!NetUtils.isNetworkConnected(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        return originalResponse.newBuilder()
       /*         .header("User-Agent", "Mozilla/5.0;" + (interceptor != null ? interceptor.getUA() : ""))*/
                .build();
            /*if (NetUtils.isNetworkConnected(context)) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .header("User-Agent","Mozilla/5.0;"+(interceptor != null ?interceptor.getUA():""))
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                        .removeHeader("Pragma")
                        .build();
            }*/

    }

}
