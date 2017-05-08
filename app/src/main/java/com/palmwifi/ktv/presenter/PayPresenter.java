package com.palmwifi.ktv.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.palmwifi.http.JsonCallback;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.comm.Contract;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.constact.PayContract;
import com.sdk.commplatform.Commplatform;
import com.sdk.commplatform.entry.AppInfo;
import com.sdk.commplatform.entry.AuthResult;
import com.sdk.commplatform.entry.CyclePayment;
import com.sdk.commplatform.entry.ErrorCode;
import com.sdk.commplatform.entry.PayResult;
import com.sdk.commplatform.listener.CallbackListener;
import com.trello.rxlifecycle.LifecycleProvider;
import com.zhy.http.okhttp.OkHttpUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class PayPresenter implements PayContract.Presenter {
    private Activity context;
    private LifecycleProvider provider;
    private PayContract.View view;
    private boolean isPaying;
    private String tradeNo;

    public PayPresenter(Activity context, LifecycleProvider provider, PayContract.View view) {
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
                    public void callback(final int code, final AuthResult result) {
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


    private CyclePayment getCyclePayment() {//校验商品信息
        tradeNo = getSecurityRandom().toString();//订单号
        CyclePayment cyclePayment = new CyclePayment();
        cyclePayment.setTradeNo(tradeNo);
        cyclePayment.setProductId(Contract.PRODUCT_ID);
        cyclePayment.setNote("娱乐无限VIP 15元/月");
        return cyclePayment;
    }

    @Override
    public void pay() {
        final CyclePayment buyInfo = getCyclePayment();
        isPaying = true;
        int ret = Commplatform.getInstance().subsPay(buyInfo, context,
                new CallbackListener<PayResult>() {

                    @Override
                    public void callback(final int arg0, final PayResult arg1) {
                        isPaying = false;
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (arg0 == ErrorCode.COM_PLATFORM_SUCCESS) {
                                    view.paySuccess();
                                    UserManager.getInstance().saveVip(buyInfo.getTradeNo());
                                    sendPayRecord(true);
                                } else if (arg0 == ErrorCode.COM_PLATFORM_ERROR_PAY_FAILURE) {
                                    view.cancelPayFailure("已取消");
                                    sendPayRecord(false);
                                } else {
                                  /*  view.paySuccess();
                                    UserManager.getInstance().saveVip(buyInfo.getTradeNo());
                                    sendPayRecord(true);*/
                                    view.cancelPayFailure("很抱歉，支付失败！" );
                                    sendPayRecord(false);
                                }
                            }
                        });
                    }
                });

        if (ret != 0) {
            isPaying = false;
            view.cancelPayFailure("很抱歉，支付失败！");
            sendPayRecord(false);
        }

    }


    //发送到服务器付费记录
    private void sendPayRecord(boolean isSuc) {
        OkHttpUtils.get().url(ConstantUrl.PAY_RECORD).addParams("userid", UserManager.getInstance().getUserID())
                .addParams("rtype", "2").addParams("issuc", isSuc ? "1" : "0").build().execute(
                new JsonCallback<BaseResult>(provider) {


                    @Override
                    public void onGetDataSuccess(BaseResult data) {

                    }
                });
    }

    /**
     * 得到随机数
     *
     * @return
     */
    private byte[] getSecurityRandom() {
        final int offset = 123456; // offset为固定值，避免被猜到种子来源（和密码学中的加salt有点类似）
        long seed = System.currentTimeMillis() + offset;
        SecureRandom secureRandom1;
        byte[] bytes = new byte[48];
        try {
            secureRandom1 = SecureRandom.getInstance("SHA1PRNG");
            secureRandom1.setSeed(seed);
            secureRandom1.nextBytes(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * 取消付费
     */
    @Override
    public void cancelPay() {
        Commplatform.getInstance().cancelCyclePay(UserManager.getInstance().getTradeNo(),
                context,
                new CallbackListener<PayResult>() {

                    @Override
                    public void callback(final int arg0, final PayResult arg1) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (arg0 == ErrorCode.COM_PLATFORM_SUCCESS) {
                                    UserManager.getInstance().setVip(false);
                                    view.cancelPaySuccess();
                                } else {
                                   /* UserManager.getInstance().setVip(false);
                                    view.cancelPaySuccess();*/
                                    view.cancelPayFailure("退订失败");
                                }
                            }
                        });
                    }
                });
    }
}