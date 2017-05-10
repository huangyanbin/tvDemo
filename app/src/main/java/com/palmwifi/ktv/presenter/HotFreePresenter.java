package com.palmwifi.ktv.presenter;/**
 * Created by David on 2017/4/5.
 */

import android.app.Activity;
import android.util.Log;
import android.text.TextUtils;

import com.palmwifi.http.AsyncCallback;
import com.palmwifi.ktv.comm.Contract;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.constact.HotFreeContract;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.helper.FavHelper;
import com.sdk.commplatform.Commplatform;
import com.sdk.commplatform.entry.AppInfo;
import com.sdk.commplatform.entry.AuthResult;
import com.sdk.commplatform.entry.ErrorCode;
import com.sdk.commplatform.listener.CallbackListener;
import com.trello.rxlifecycle.LifecycleProvider;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/04/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HotFreePresenter implements HotFreeContract.Presenter {
    private Activity context;
    private LifecycleProvider provider;
    private HotFreeContract.View view;

    public HotFreePresenter(Activity context, LifecycleProvider provider, HotFreeContract.View view) {
        this.context = context;
        this.provider = provider;
        this.view = view;
        this.view.setPresenter(this);
    }



    @Override
    public void unSubscribe() {
        this.context = null;
        this.provider = null;
        this.view = null;
    }

    @Override
    public void init() {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(Contract.APPID);// 应用ID
        appInfo.setAppKey(Contract.APPKey);// 应用Key
        appInfo.setCtx(context);
        appInfo.setVersionCheckStatus(0);

        Commplatform.getInstance().Init(0, appInfo, new CallbackListener<Integer>() {
            @Override
            public void callback(final int paramInt, final Integer paramT) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (paramInt == ErrorCode.COM_PLATFORM_SUCCESS) {
                            view.initSuccess();
                        } else {
                            view.initFailure("初始化失败");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void authPay() {
        Commplatform.getInstance().authPermission(Contract.PRODUCT_ID,
                Contract.PRODUCT_TYPE,Contract.CONTENT_ID,
                new CallbackListener<AuthResult>() {

                    @Override
                    public void callback(final int code, AuthResult result) {
                        //获取当前productID
                        if(result != null && result.productInfos.length >0) {
                            String productID =result.productInfos[0].productId;
                            if(!TextUtils.isEmpty(productID)){
                                Contract.PRODUCT_ID = productID;
                            }
                        }
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (code == ErrorCode.COM_PLATFORM_SUCCESS) {
                                    UserManager.getInstance().setVip(true);
                                    view.isHasPay(true);
                                } else {
                                    UserManager.getInstance().setVip(false);
                                    view.isHasPay(false);
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public void requestHotFree() {

        OkHttpUtils.get().url(ConstantUrl.HOME_FREE_URL).build().execute(
                new AsyncCallback<BaseResult<List<Song>>,List<Song>>(provider) {

            @Override
            public List<Song> covert(BaseResult<List<Song>> result) {
                if(TextUtils.equals(result.getResultCode(),BaseResult.SUC)){
                    List<Song> songs = result.getData();
                    for(Song song:songs){
                        //检查是否收藏了
                        song.setFav(FavHelper.checkFav(song));
                    }
                    return songs;
                }
                return null;
            }

            @Override
            public void onGetDataSuccess(List<Song> data) {
                if(data != null) {
                    view.getHotFreeSuc(data);
                }
            }
        });
    }
}