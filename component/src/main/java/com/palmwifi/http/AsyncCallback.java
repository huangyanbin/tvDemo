package com.palmwifi.http;

import com.google.gson.Gson;
import com.palmwifi.helper.ILoading;
import com.trello.rxlifecycle.LifecycleProvider;

import java.lang.reflect.Type;

import okhttp3.Call;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by David on 2016/12/27.
 * T 为网络请求返回类型
 * K 为返回到UI线程我们需要的类型
 */
public abstract class AsyncCallback<T,K> extends JsonCallback<K> {

    public AsyncCallback(LifecycleProvider lifecycleProvider) {
        super(lifecycleProvider);
    }

    public AsyncCallback(LifecycleProvider lifecycleProvider, ILoading loading) {
        super(lifecycleProvider, loading);
    }

    public abstract K covert(T t);

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


    @Override
    protected void parseResponseData(final boolean isFromCache, final int id, final Call call, final String response) {
        Observable.just(response).map(new Func1<String, K>() {
            @Override
            public K call(String s) {
                T t =  getJsonResponse(response);
                if(t == null){
                    return  null;
                }else{
                    return covert(t);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.immediate())
                .compose(lifecycleProvider.<K>bindToLifecycle())
                .subscribe(new Action1<K>() {
                    @Override
                    public void call(K k) {
                        handlerParserResult(k,isFromCache,call,id);
                    }
                });
    }


}
