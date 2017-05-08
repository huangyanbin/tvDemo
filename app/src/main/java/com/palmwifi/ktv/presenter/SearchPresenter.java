package com.palmwifi.ktv.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.palmwifi.http.AsyncCallback;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.comm.Contract;
import com.palmwifi.ktv.constact.SearchContract;
import com.palmwifi.ktv.helper.FavHelper;
import com.trello.rxlifecycle.LifecycleProvider;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
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
public class SearchPresenter implements SearchContract.Presenter {
    private Context context;
    private LifecycleProvider provider;
    private SearchContract.View view;
    private int pager;
    private boolean isSearch;
    private String content;
    private boolean isNoMore;

    public SearchPresenter(Context context, LifecycleProvider provider, SearchContract.View view) {
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
    public void requestMore() {
        if (isSearch) {
            requestSearchSong(false, content);
        } else {
            reqHotSongList(false);
        }
    }

    @Override
    public boolean isNoMore() {
        return isNoMore;
    }

    @Override
    public void reqHotSongList(final boolean isRefresh) {
        pager = isRefresh ? 0 : (pager + 1);
        if(isRefresh){
            isNoMore = false;
        }
        isSearch = false;
        OkHttpUtils.get().url(ConstantUrl.NEW_SONG_URL).addParams("page", pager+"").addParams("songType", SongPresenter.NEW + "")
                .addParams("pageSize", Contract.PAGE_SIZE + "").build().execute(
                new AsyncCallback<BaseResult<List<Song>>, List<Song>>(provider) {

                    @Override
                    public List<Song> covert(final BaseResult<List<Song>> result) {
                        return checkFavSong(result);
                    }

                    @Override
                    public void onGetDataSuccess(List<Song> data) {
                        view.getSongListSuc(false, isRefresh, data);
                    }
                });

    }

    @Nullable
    private List<Song> checkFavSong(final BaseResult<List<Song>> result) {
        if (TextUtils.equals(result.getResultCode(), BaseResult.SUC)) {
            List<Song> songs = result.getData();
            if(songs !=null && songs.size() >0) {
                for (Song song : songs) {
                    //检查是否收藏了
                    song.setFav(FavHelper.checkFav(song));
                }
                if (songs.size() < Contract.PAGE_SIZE) {
                    isNoMore = true;
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.getTotalCount(result.getTotalCount());
                    }
                });
                return songs;
            }else{
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.getTotalCount(0);
                    }
                });
                isNoMore = true;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void requestSearchSong(final boolean isRefresh, String content) {
        pager = isRefresh ? 0 : (pager + 1);
        isSearch = true;
        this.content = content;
        OkHttpUtils.get().url(ConstantUrl.SEARCH_SONG).addParams("song_pingyin", content)
                .addParams("page", pager+"").addParams("pageSize", Contract.PAGE_SIZE + "").build().execute(
                new AsyncCallback<BaseResult<List<Song>>, List<Song>>(provider) {

                    @Override
                    public List<Song> covert(BaseResult<List<Song>> result) {
                        return checkFavSong(result);
                    }

                    @Override
                    public void onGetDataSuccess(List<Song> data) {
                        view.getSongListSuc(true, isRefresh, data);
                    }
                }
        );
    }
}