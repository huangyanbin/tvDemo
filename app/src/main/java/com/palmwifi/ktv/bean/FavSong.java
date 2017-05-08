package com.palmwifi.ktv.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by David on 2017/3/23.
 */
@Entity
public class FavSong {

    @Id(autoincrement = true)
    private Long id;
    private String tid;
    private String url;
    private String singer;
    private String name;




    @Generated(hash = 1730355209)
    public FavSong(Long id, String tid, String url, String singer, String name) {
        this.id = id;
        this.tid = tid;
        this.url = url;
        this.singer = singer;
        this.name = name;
    }

    public FavSong(String url, String singer, String name) {
        this.url = url;
        this.singer = singer;
        this.name = name;
    }

    public FavSong(String tid,String url, String singer, String name) {
        this.url = url;
        this.singer = singer;
        this.name = name;
    }

    @Generated(hash = 268062656)
    public FavSong() {
    }




    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSinger() {
        return this.singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Song convertSong(){
        Song song = new Song(tid,url,singer,name);
        song.setFav(true);
        return song;
    }

}
