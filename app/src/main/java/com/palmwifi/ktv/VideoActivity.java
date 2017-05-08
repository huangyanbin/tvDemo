package com.palmwifi.ktv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.palmwifi.base.BaseActivity;
import com.palmwifi.http.JsonCallback;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.manager.VideoManager;
import com.palmwifi.ktv.views.FullVideoView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.zhy.http.okhttp.OkHttpUtils;


import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by David on 2017/3/23.
 */

public class VideoActivity extends BaseActivity implements ViewFocusHelper.OnSharkListener  {



    @BindView(R.id.img_video_pause)
    ImageView imgVideoPause;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.video_view)
    FullVideoView videoPlayer;
    @BindDimen(R.dimen.w_562)
    int videoListWidth;
    private VideoManager videoManager;
    private ViewFocusHelper mHelper;
    private  final int perTime = 200;
    private Handler mHandler = new Handler();

    public static void startActivity(Activity context, Song song, long seek) {

        Intent i = new Intent(context, VideoActivity.class);
        i.putExtra("song",song);
        i.putExtra("seek",seek);
        context.startActivityForResult(i,1);
    }
    public static void startActivity(Context context,Song song) {

        Intent i = new Intent(context, VideoActivity.class);
        i.putExtra("song",song);
        context.startActivity(i);
    }

    @Override
    protected int setLayoutID() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mHelper = new ViewFocusHelper(this);
        Song song = getIntent().getParcelableExtra("song");
        long seek = getIntent().getLongExtra("seek",-1);
        videoManager = new VideoManager(videoPlayer,song,seek);
        videoManager.play();
        rlContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                if (newFocus != null)
                    newFocus.bringToFront();
                mHelper.onFocusView(newFocus);

            }
        });
        for (int i = 0; i < rlContent.getChildCount(); i++) {
            rlContent.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.requestFocus();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void initData() {

        OkHttpUtils.get().url(ConstantUrl.PLAY_RECORD).addParams("userid", UserManager.getInstance().getUserID())
                .addParams("isvip",UserManager.getInstance().isVip() ?"1":"0").addParams("songid",videoManager.getPlaySong().getTid()).build().execute(
                new JsonCallback<BaseResult>(this) {


                    @Override
                    public void onGetDataSuccess(BaseResult data) {

                    }
                });
    }


    @OnClick(R.id.img_video_pause)
    public void onClick(View view){
        onPauseOrResume();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(mHelper.onSoftKeyDown(keyCode,event)){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mHandler.removeCallbacks(forward);
        mHandler.removeCallbacks(back);
        return super.onKeyUp(keyCode, event);

    }

    //后退
    @Override
    public boolean toLeft(View view) {
        if(videoPlayer != null &&videoPlayer.getCurrentPositionWhenPlaying()>=20){
            mHandler.postDelayed(back,perTime);
            return true;
        }
        return false;
    }
    //前进
    @Override
    public boolean toRight(View view) {
        if(videoPlayer != null &&videoPlayer.getDuration() -videoPlayer.getCurrentPositionWhenPlaying() >=20){
            mHandler.postDelayed(forward,perTime);
            return true;
        }

        return false;

    }

    public Runnable forward = new Runnable() {
        @Override
        public void run() {
            int position = videoPlayer.getCurrentPositionWhenPlaying();
            int forward = videoPlayer.getDuration()/100;
            int start = position +forward;
            if(start >= videoPlayer.getDuration() ){
                start = videoPlayer.getDuration()-10;
                mHandler.removeCallbacks(this);
            }else {
                mHandler.postDelayed(this,perTime);
            }
            GSYVideoManager.instance().getMediaPlayer().seekTo(start);
        }
    };


    public Runnable back = new Runnable() {
        @Override
        public void run() {
            int position = videoPlayer.getCurrentPositionWhenPlaying();
            int forward = videoPlayer.getDuration()/100;
            int start = position - forward;
            if(start <= 0){
                start = 10;
                mHandler.removeCallbacks(this);
            }else {
                mHandler.postDelayed(this,perTime);
            }
            GSYVideoManager.instance().getMediaPlayer().seekTo(start);
        }
    };




    @Override
    public boolean toTop(View view) {
        return false;
    }

    @Override
    public boolean toDown(View view) {
        return false;
    }

    @Override
    public boolean toBack(View view) {
        finish();
        return true;
    }

    @Override
    public boolean toMenu(View view) {
        onPauseOrResume();
        return true;
    }

    private void onPauseOrResume() {
        if(!imgVideoPause.isSelected()){
            videoManager.onPause();
            imgVideoPause.setSelected(true);
        }else{
            videoManager.onResume();
            imgVideoPause.setSelected(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoManager.onDestroy();
        mHandler.removeCallbacks(null);
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra("seek",videoManager.getPlaySeek());
        setResult(2,i);
        super.finish();
    }
}
