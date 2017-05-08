package com.palmwifi.ktv.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by David on 2017/3/23.
 */
@Entity
public class Song implements Parcelable {

    @Id(autoincrement = true)
    private Long id;
    private String tid;
    @SerializedName("payaddress")
    private String url;
    private String singer;
    @SerializedName("songname")
    private String name;
    @Transient
    private boolean isFav;


    @Generated(hash = 1748274225)
    public Song(Long id, String tid, String url, String singer, String name) {
        this.id = id;
        this.tid = tid;
        this.url = url;
        this.singer = singer;
        this.name = name;
    }

    public Song(String url, String singer, String name) {
        this.url = url;
        this.singer = singer;
        this.name = name;
    }

    public Song(String url, String singer, String name,boolean isFav) {
        this.url = url;
        this.isFav = isFav;
        this.singer = singer;
        this.name = name;
    }

    public Song(String tid,String url, String singer, String name) {
        this.url = url;
        this.singer = singer;
        this.name = name;
    }

    @Generated(hash = 87031450)
    public Song() {
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FavSong convertFavSong() {
        FavSong song = new FavSong(tid,url,singer,name);
        return  song;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.tid);
        dest.writeString(this.url);
        dest.writeString(this.singer);
        dest.writeString(this.name);
        dest.writeByte(this.isFav ? (byte) 1 : (byte) 0);
    }

    protected Song(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.tid = in.readString();
        this.url = in.readString();
        this.singer = in.readString();
        this.name = in.readString();
        this.isFav = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
