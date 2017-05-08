package com.palmwifi.ktv.manager;

import com.palmwifi.ktv.bean.Song;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 2017/3/23.
 */

public class VideoManager {

    private List<Song> songList;
    private int playPosition;
    private long seekPosition;
    private GSYVideoPlayer videoPlayer;
    public static boolean isRelease = false;


    public VideoManager(GSYVideoPlayer videoPlayer, List<Song> songs) {
        this.videoPlayer = videoPlayer;
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        videoPlayer.setLooping(true);
        videoPlayer.setNeedShowWifiTip(false);
        videoPlayer.setIsTouchWiget(false);
        videoPlayer.setLockLand(true);
        songList = songs;

    }


    public VideoManager(GSYVideoPlayer videoPlayer, Song song, long seek) {
        this.videoPlayer = videoPlayer;
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        videoPlayer.setLooping(true);
        videoPlayer.setNeedShowWifiTip(false);
        videoPlayer.setIsTouchWiget(false);
        videoPlayer.setLockLand(true);
        if (seek != -1) {
            videoPlayer.setSeekOnStart(seek);
        }

        songList = new ArrayList<>();
        songList.add(song);

    }

    public void play(int position) {
        seekPosition = 0;
        if (songList.size() > 0) {
            if (songList.size() <= position) {
                position = 0;
            } else if (position < 0) {
                position = songList.size() - 1;
            }
            playPosition = position;
            Song s = songList.get(position);
            String url = s.getUrl();
            videoPlayer.setUp(url, true, s.getName());
            videoPlayer.startPlayLogic();
        }
    }



    public void seekTo(long seconds) {
        videoPlayer.setSeekOnStart(seconds);
    }

    public void next() {

        play(playPosition + 1);
    }

    public void per() {

        play(playPosition - 1);
    }

    public void play() {
        play(0);
    }


    public void onPause() {
        seekPosition = GSYVideoManager.instance().getMediaPlayer().getCurrentPosition();
        videoPlayer.onVideoPause();
    }

    public boolean isPlaying() {
        return GSYVideoManager.instance().getMediaPlayer().isPlaying();
    }

    public void onResume() {

        videoPlayer.onVideoResume();

    }

    public void reset() {
        if (isRelease) {
            videoPlayer.setUp(getPlaySong().getUrl(), true, getPlaySong().getName());
            videoPlayer.setSeekOnStart(seekPosition);
            videoPlayer.startPlayLogic();
            isRelease = false;
        } else {
            videoPlayer.onVideoResume();
        }
    }


    public void setSongList(List<Song> songList) {
        playPosition = 0;
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public int getPlayPosition() {
        return playPosition;
    }

    public Song getPlaySong() {
        return songList.get(playPosition);
    }

    public long getPlaySeek() {
        return videoPlayer.getSeekOnStart();
    }


    public void onDestroy() {
        videoPlayer.removeCallbacks(null);
        videoPlayer.release();
        isRelease = true;
        videoPlayer = null;
    }

}
