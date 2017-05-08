package com.palmwifi.ktv.event;

import com.palmwifi.ktv.bean.Song;

/**
 * Created by David on 2017/4/5.
 */

public class FavEvent {

    public Song song;

    public FavEvent(Song song) {
        this.song = song;
    }
}
