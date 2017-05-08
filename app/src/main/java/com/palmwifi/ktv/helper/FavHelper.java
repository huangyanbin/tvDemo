package com.palmwifi.ktv.helper;

import com.palmwifi.ktv.bean.FavSong;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.db.DBHelper;
import com.palmwifi.ktv.db.FavSongDao;

import java.util.List;

/**
 * Created by David on 2017/4/1.
 */

public class FavHelper {

    public static void addFav(Song song) {
        if (song != null&& song.getUrl() != null) {
            FavSong favSong = song.convertFavSong();
            DBHelper.getInstance().getFavSongDao().insert(favSong);
            song.setFav(true);
        }
    }

    public static boolean checkFav(Song song) {
        if(song != null && song.getUrl() != null){
           List<FavSong> songs =  DBHelper.getInstance().getFavSongDao().queryBuilder()
                   .where(FavSongDao.Properties.Url.eq(song.getUrl())).list();
            if(songs != null && songs.size() >= 1){
                return true;
            }
        }
        return false;
    }

    public static void deleteFav(Song song) {
        if (song != null && song.getUrl() != null) {
            List<FavSong> songs =  DBHelper.getInstance().getFavSongDao().queryBuilder()
                    .where(FavSongDao.Properties.Url.eq(song.getUrl())).list();
            if(songs != null && songs.size() >= 1){
                DBHelper.getInstance().getFavSongDao().deleteInTx(songs);
            }
            song.setFav(false);
        }
    }
}
