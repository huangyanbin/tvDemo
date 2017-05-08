package com.palmwifi.ktv.comm;

/**
 * Created by David on 2017/4/6.
 */

public class ConstantUrl {

    public static final String HOST = "http://118.212.137.132:8093/fdp/";

    /**
     * 首页歌曲
     */
    public static final String HOME_FREE_URL = HOST + "openAPIAction_songList";

    public static final String NEW_SONG_URL = HOST + "openAPIAction_newOrHotSongList";

    public static final String SINGER_TYPE_URL = HOST + "openAPIAction_singerSortList";


    public static final String SINGER_LIST_URL = HOST + "openAPIAction_sortSingerList";

    public static final String SINGER_SONG_LIST = HOST + "openAPIAction_songListBySinger";

    public static final String SEARCH_SINGER = HOST + "openAPIAction_singListByPingyin";


    public static final String SEARCH_SONG = HOST +"openAPIAction_songListByPingyin";

   public static final String PLAY_RECORD =HOST +"openAPIAction_songPaySave";

    public static final String PAY_RECORD =HOST+"openAPIAction_rechargeSave";
}
