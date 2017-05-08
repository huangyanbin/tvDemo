package com.palmwifi.ktv.constact;/**
 * Created by David on 2017/4/1.
 */

import com.palmwifi.ktv.bean.Song;
import com.palmwifi.mvp.IPresenter;
import com.palmwifi.mvp.IView;

import java.util.List;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/04/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface SongContract {

    interface View extends IView<Presenter> {

        void noData();
        void requestDataSuc(List<Song> songs);
        void getTotalCount(int totalCount);
    }

    interface Presenter extends IPresenter {
         void requestData();
         void addHistory(Song song);
          boolean isNoMore();

    }
}