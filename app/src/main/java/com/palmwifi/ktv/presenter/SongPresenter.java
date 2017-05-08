package com.palmwifi.ktv.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.palmwifi.http.AsyncCallback;
import com.palmwifi.ktv.comm.Contract;
import com.palmwifi.ktv.constact.SongContract;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.bean.FavSong;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.db.DBHelper;
import com.palmwifi.ktv.helper.FavHelper;
import com.palmwifi.ktv.helper.HistoryHelper;
import com.trello.rxlifecycle.LifecycleProvider;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/04/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SongPresenter implements SongContract.Presenter {

    public static final int HISTORY = 3;
    public static final int FAV = 2;
    public static final int NEW = 0;
    public static final int HOT = 1;
    public static final int SINGER = 4;
    private boolean isNoMore;
    private Context context;
    private LifecycleProvider provider;
    private int pager;
    private String singerID;
    private SongContract.View view;
    private int type;


    public SongPresenter(Context context, LifecycleProvider provider, SongContract.View view, int type) {
        this.type = type;
        this.context = context;
        this.provider = provider;
        this.view = view;
        this.view.setPresenter(this);
    }

    public SongPresenter(Context context, LifecycleProvider provider, SongContract.View view, String singerID) {
        this.singerID = singerID;
        this.type = SINGER;
        this.context = context;
        this.provider = provider;
        this.view = view;
        this.view.setPresenter(this);
    }


    public void requestData() {
        switch (type) {
            case HISTORY:
                getHistoryData();
                break;
            case FAV:
                getFavData();
                break;
            case NEW:
                getNewData();
                break;
            case HOT:
                getHotData();
                break;
            case SINGER:
                getSingerData();
                break;
        }
    }


    /**
     * 获取历史记录
     */
    private void getHistoryData() {
        Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                List<Song> historySongs = DBHelper.getInstance().getSongDao().queryBuilder().list();
                for (Song song : historySongs) {
                    boolean isFav = FavHelper.checkFav(song);
                    song.setFav(isFav);

                }
                subscriber.onNext(historySongs);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        if (songs == null || songs.size() == 0) {
                            view.noData();
                            view.getTotalCount(0);
                        } else {
                            view.requestDataSuc(songs);
                            view.getTotalCount(songs.size());
                        }
                        isNoMore = true;
                    }
                });


    }


    /**
     * 获取收藏
     */
    private void getFavData() {
        List<Song> songs = new ArrayList<>();
        List<FavSong> favSongs = DBHelper.getInstance().getFavSongDao().queryBuilder().list();
        if (favSongs == null || favSongs.size() == 0) {
            view.noData();
            view.getTotalCount(0);
        } else {
            for (FavSong favSong : favSongs) {
                Song song = favSong.convertSong();
                songs.add(song);
            }
            view.getTotalCount(songs.size());
            view.requestDataSuc(songs);
        }
        isNoMore = true;
    }


    /**
     * 获取最新歌曲
     */
    private void getNewData() {
        getNetData(NEW);

    }

    /**
     * 获取热门歌曲
     */
    private void getHotData() {
        getNetData(HOT);
    }

    private void getNetData(int songType) {

        OkHttpUtils.get().url(ConstantUrl.NEW_SONG_URL).addParams("page", pager + "").addParams("songType", songType + "")
                .addParams("pageSize", Contract.PAGE_SIZE + "").build().execute(
                new AsyncCallback<BaseResult<List<Song>>, List<Song>>(provider) {

                    @Override
                    public List<Song> covert(final BaseResult<List<Song>> result) {

                        return  checkFavSong(result);
                    }

                    @Override
                    public void onGetDataSuccess(List<Song> data) {
                        if (data != null) {
                            view.requestDataSuc(data);
                        } else {
                            view.noData();
                            view.getTotalCount(0);
                        }
                    }
                });
    }


    private void getSingerData() {
        OkHttpUtils.get().url(ConstantUrl.SINGER_SONG_LIST).addParams("page", pager + "").addParams("singerid", singerID)
                .addParams("pageSize", Contract.PAGE_SIZE + "").build().execute(
                new AsyncCallback<BaseResult<List<Song>>, List<Song>>(provider) {

                    @Override
                    public List<Song> covert(final BaseResult<List<Song>> result) {

                        return  checkFavSong(result);
                    }

                    @Override
                    public void onGetDataSuccess(List<Song> data) {
                        if (data != null) {
                            view.requestDataSuc(data);
                        } else {
                            view.noData();
                            view.getTotalCount(0);
                        }
                    }
                });

    }

    @Nullable
    private List<Song> checkFavSong(final BaseResult<List<Song>> result) {
        if (TextUtils.equals(result.getResultCode(), BaseResult.SUC)) {
            List<Song> songs = result.getData();
            if (songs != null && songs.size() > 0) {
                for (Song song : songs) {
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
                pager++;
                return songs;
            } else {
                isNoMore = true;
            }
        }
        return new ArrayList<>();
    }


    @Override
    public void unSubscribe() {
        this.context = null;
        this.provider = null;
        this.view = null;
    }



    public void addHistory(Song song) {
        HistoryHelper.addHistory(song);
    }

    @Override
    public boolean isNoMore() {
        return isNoMore;
    }
}