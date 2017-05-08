package com.palmwifi.http;

import android.widget.Toast;

import com.google.gson.Gson;
import com.palmwifi.base.BaseApplication;
import com.palmwifi.fragmention.R;
import com.palmwifi.helper.ILoading;
import com.palmwifi.utils.NetUtils;
import com.trello.rxlifecycle.LifecycleProvider;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by David on 2016/10/17.
 */

public abstract class JsonCallback<T> extends StringCallback {

    public static final int NET_CONNECTION_TOAST_COUNT = 2;

    public static int netConnectionToast = 0;
    protected ILoading mLoading;
    protected int responseCode;
    public static final String NET_NO_CONNECT = "net_no_connect";
    protected LifecycleProvider lifecycleProvider;



    public JsonCallback(LifecycleProvider lifecycleProvider){
        this.lifecycleProvider = lifecycleProvider;
    }

    public JsonCallback(LifecycleProvider lifecycleProvider, ILoading loading){
        this.lifecycleProvider = lifecycleProvider;
        mLoading = loading;

    }


    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if(mLoading != null){
            mLoading.startLoading();
        }
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        onFinish();
        if(mLoading != null){
            mLoading.stopLoading();
        }
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
        if(mLoading != null){
            mLoading.onProgress(progress);
        }
    }

    @Override
    public boolean validateReponse(Response response, int id) {
        responseCode = response.code();
        return super.validateReponse(response, id);
    }

    @Override
    public void onError(Call call, final Exception e, int id) {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                parseError(subscriber);
            }
        }).subscribeOn(Schedulers.immediate())
        .observeOn(AndroidSchedulers.mainThread())
                //解绑
                .compose(lifecycleProvider.<String>bindToLifecycle())
        .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s != null) {
                    onFailure(responseCode,NET_NO_CONNECT);
                    Toast.makeText(BaseApplication.getContext(), s, Toast.LENGTH_SHORT).show();
                }else {
                    onFailure(responseCode,e.getMessage());
                }
            }
        });
    }

    private void parseError(Subscriber<? super String> subscriber) {
        if(!NetUtils.isNetworkConnected(BaseApplication.getContext())){
            if(netConnectionToast < NET_CONNECTION_TOAST_COUNT) {
                netConnectionToast++;
                subscriber.onNext(BaseApplication.getContext().getString(R.string.net_connection_error));
            }else{
                subscriber.onNext(null);
            }
        }else{
            netConnectionToast = 0;
            subscriber.onNext(null);
        }
    }

    @Override
    public void onResponse(final String response, int id) {
        parseResponseData(false,id,null,response);
    }


    @Override
    public void onCacheResponse(final Call call, final String response, final int id) {
        parseResponseData(true,id,call,response);
    }


    /**
     * 解析网络
     * @param response
     */
    protected void parseResponseData(final boolean isFromCache, final int id,final Call call, final String response) {
        Observable.just(response).map(new Func1<String, T>() {
            @Override
            public T call(String s) {
                T t = getJsonResponse(response);
                return t;
            }
        }).observeOn(AndroidSchedulers.mainThread())
           .subscribeOn(Schedulers.immediate())
            .compose(lifecycleProvider.<T>bindToLifecycle())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        handlerParserResult(t, isFromCache, call, id);
                    }
                });
    }

    /**
     * 处理返回的结果
     * @param t
     * @param isFromCache
     * @param call
     * @param id
     */
    protected void handlerParserResult(T t, boolean isFromCache, Call call, int id) {
        if(t == null){
            parseDataError(isFromCache,call,id);
        }else{
            if(isFromCache) {
                onGetCacheDataSuccess(t);
            }else{
                onGetDataSuccess(t);
            }
        }
    }

    /**
     * json解析
     * @param response
     * @return
     */
    private T getJsonResponse(String response) {
        T t = null;
        try{
            if(response != null){
                Gson gson = new Gson();
                Type type = getType();
                if(type == String.class || type == Object.class){
                    t = (T)response;
                }else{
                    t = gson.fromJson(response,type);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return t;
    }

    public void onFailure(int responseCode,String response){

    }


    public void onFinish(){

    }

    /**
     * 获取数据错误
     * @param data
     */
    public  abstract void onGetDataSuccess(T data);

    /**
     * 获取缓存数据 (如果想单独处理缓存，重新该方法)
     * @param data
     */
    public  void onGetCacheDataSuccess(T data){
       onGetDataSuccess(data);
    }
    /**
     * 获取当前类型
     * @return
     */
    public Type getType(){

        Type type = String.class;
        Type mySuperClass = this.getClass().getGenericSuperclass();
        if(mySuperClass instanceof ParameterizedType){
            type = ((ParameterizedType)mySuperClass).getActualTypeArguments()[0];
        }
        return type;
    }


    /**
     * 解析数据失败
     * @param call
     * @param id
     */
    protected void parseDataError(boolean isFromCache,Call call, int id) {
        if(isFromCache) {
            onNoCache(call, new IOException(BaseApplication.getContext().getString(R.string.json_parse_error)), id);
        }else {
            onFailure(responseCode, BaseApplication.getContext().getString(R.string.json_parse_error));
        }
    }

    /**
     * 没有缓存或者缓存解析错误
     * @param call
     * @param e
     * @param id
     */
    @Override
    public void onNoCache(Call call, Exception e, int id){

    }


}
