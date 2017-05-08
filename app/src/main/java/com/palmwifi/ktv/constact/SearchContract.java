package com.palmwifi.ktv.constact;

import com.palmwifi.ktv.bean.Song;
import com.palmwifi.mvp.IPresenter;
import com.palmwifi.mvp.IView;

import java.util.List;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/04/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface SearchContract {

    interface View extends IView<Presenter> {
        void getSongListSuc(boolean isSearch,boolean isRefresh,List<Song> songs);
        void getTotalCount(int total);
    }

    interface Presenter extends IPresenter {
        void reqHotSongList(boolean isRefresh);
        void requestSearchSong(boolean isRefresh,String content);
        void requestMore();
        boolean isNoMore();
    }
}