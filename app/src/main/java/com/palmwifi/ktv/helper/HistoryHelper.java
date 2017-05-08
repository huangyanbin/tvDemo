package com.palmwifi.ktv.helper;

import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.db.DBHelper;
import com.palmwifi.ktv.db.SongDao;

import java.util.List;

/**
 * Created by David on 2017/4/1.
 */

public class HistoryHelper {

    public static void addHistory(Song song) {
        if (song != null) {
            SongDao dao =  DBHelper.getInstance().getSongDao();
            List<Song> songs = dao.queryBuilder()
                    .where(SongDao.Properties.Url.eq(song.getUrl())).list();
            if(songs== null || songs.size() == 0){
                dao.insert(song);
            }
        }
    }
}
