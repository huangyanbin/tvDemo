package com.palmwifi.ktv.constact;/**
 * Created by David on 2017/4/5.
 */

import com.palmwifi.ktv.bean.Song;
import com.palmwifi.mvp.IPresenter;
import com.palmwifi.mvp.IView;

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
public interface HotFreeContract {

    interface View extends IView<Presenter> {
       void getHotFreeSuc(List<Song> songList);
        //初始化成功
        void initSuccess();
        //初始化失败
        void initFailure(String msg);
        //是否付费
        void isHasPay(boolean isPay);
    }

    interface Presenter extends IPresenter {
       void requestHotFree();
        /**
         * 初始化
         */
        void init();


        /**
         * 验证是否付费
         */
        void authPay();
    }
}