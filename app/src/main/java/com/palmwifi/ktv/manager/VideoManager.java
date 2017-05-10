package com.palmwifi.ktv.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.utils.Utils;
import com.palmwifi.ktv.views.MediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 2017/3/23.
 */

public class VideoManager {

    private List<Song> songList;
    private int playPosition;
    private PLVideoTextureView videoPlayer;
    private Activity activity;
    private Toast mToast = null;
    private boolean mIsActivityPaused = true;
    private String mVideoPath = null;
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    private View mLoadingView;


    public VideoManager(Activity activity,PLVideoTextureView videoPlayer, List<Song> songs) {
        this.activity =activity;
        this.videoPlayer = videoPlayer;
        songList = songs;
        setOptions();
    }


    public VideoManager(Activity activity,PLVideoTextureView videoPlayer, Song song) {
        this.activity =activity;
        this.videoPlayer = videoPlayer;
        songList = new ArrayList<>();
        songList.add(song);
        setOptions();
    }


    public void setLoadingView(View mLoadingView) {
        this.mLoadingView = mLoadingView;
    }

    private void setOptions() {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 5000000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
       // options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_AUTO);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        videoPlayer.setAVOptions(options);

        videoPlayer.setOnCompletionListener(mOnCompletionListener);
        videoPlayer.setOnErrorListener(mOnErrorListener);
    }

    public void setMediaController(IMediaController mediaController){
        videoPlayer.setMediaController(mediaController);
    }

    public void play(int position) {
        if (songList.size() > 0) {
            if (songList.size() <= position) {
                position = 0;
            } else if (position < 0) {
                position = songList.size() - 1;
            }
            playPosition = position;
            Song s = songList.get(position);
            mVideoPath = s.getUrl();
            videoPlayer.setVideoPath(mVideoPath);
            videoPlayer.start();
        }
    }

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };


    private void showToastTips(final String tips) {
        if(activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(activity, tips, Toast.LENGTH_SHORT);
                    mToast.show();
                }
            });
        }
    }

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            next();

        }
    };



    public void seekTo(long seconds) {
        videoPlayer.seekTo(seconds);
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
        mToast = null;
        videoPlayer.pause();
        mIsActivityPaused = true;
    }

    public boolean isPlaying(){
        return videoPlayer.isPlaying();
    }

    public void onResume() {

        mIsActivityPaused = false;
        videoPlayer.start();

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



    public void onDestroy() {

        videoPlayer.stopPlayback();
        activity = null;
        mLoadingView = null;
        mHandler = null;
    }


    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        if(mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
                return;
            }
            if (!Utils.isNetworkAvailable(activity)) {
                sendReconnectMessage();
                return;
            }
            videoPlayer.setVideoPath(mVideoPath);
            videoPlayer.start();
        }
    };

}
