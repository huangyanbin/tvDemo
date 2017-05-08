package com.palmwifi.ktv.constact;

import com.palmwifi.ktv.bean.Singer;
import com.palmwifi.ktv.bean.SingerType;
import com.palmwifi.mvp.IPresenter;
import com.palmwifi.mvp.IView;

import java.util.List;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/04/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface SearchSingerContract {

    interface View extends IView<Presenter> {
        void getSingerType(List<SingerType> singerTypes);
        void getSingerListSuc(boolean isSearch,boolean isRefresh,List<Singer> singers);
        void getTotalCount(int total);
    }

    interface Presenter extends IPresenter {
        void requestSingerType();
        void requestSingerList(boolean isRefresh, int position);
        void searchSingerList(boolean isRefresh,String content);
        void requestMore();
        boolean isNoMore();
        boolean getIsSearch();
    }
}