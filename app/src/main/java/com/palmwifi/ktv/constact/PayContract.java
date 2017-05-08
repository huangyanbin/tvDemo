package com.palmwifi.ktv.constact;

import com.palmwifi.mvp.IPresenter;
import com.palmwifi.mvp.IView;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface PayContract {

    interface View extends IView<Presenter> {
        //初始化成功
        void initSuccess();
        //初始化失败
        void initFailure(String msg);
        //是否付费
        void isHasPay(boolean isPay);
        //支付成功
        void paySuccess();
        //支付失败
        void payFailure(String msg);
        //取消成功
        void cancelPaySuccess();
        //取消失败
        void cancelPayFailure(String msg);
    }

    interface Presenter extends IPresenter {
        /**
         * 初始化
         */
         void init();


        /**
         * 验证是否付费
         */
        void authPay();

        /**
         * 付费
         */
        void pay();

        /**
         * 取消付费
         */
        void cancelPay();

    }
}