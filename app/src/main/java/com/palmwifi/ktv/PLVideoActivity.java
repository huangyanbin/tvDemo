package com.palmwifi.ktv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.palmwifi.base.BaseActivity;
import com.palmwifi.http.JsonCallback;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.manager.VideoManager;
import android.util.Log;
import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * This is a demo activity of PLVideoTextureView
 */
public class PLVideoActivity extends BaseActivity implements ViewFocusHelper.OnSharkListener, IMediaController {


    @BindView(R.id.VideoView)
    PLVideoTextureView mVideoView;
    @BindView(R.id.LoadingView)
    View mLoadingView;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.img_video_pause)
    ImageView imgVideoPause;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.current)
    TextView mCurrentTime;
    @BindView(R.id.total)
    TextView mEndTime;
    @BindView(R.id.progress)
    SeekBar mProgress;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    private VideoManager videoManager;
    private ViewFocusHelper mHelper;
    private final int perTime = 200;
    private static int sDefaultTimeout = 5000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mShowing;
    private long mDuration;


    public static void startActivity(Context context, Song song) {

        Intent i = new Intent(context, PLVideoActivity.class);
        i.putExtra("song", song);
        context.startActivity(i);
    }


    @Override
    protected int setLayoutID() {
        return R.layout.activity_video_texture;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mHelper = new ViewFocusHelper(this);
        mVideoView = (PLVideoTextureView) findViewById(R.id.VideoView);
        mLoadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);
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
        mProgress.setMax(1000);
        imgVideoPause.requestFocus();

    }

    @Override
    protected void initData() {
        Song song = getIntent().getParcelableExtra("song");
        videoManager = new VideoManager(this, mVideoView, song);
        videoManager.setLoadingView(mLoadingView);
        videoManager.setMediaController(this);
        videoManager.play();

        OkHttpUtils.get().url(ConstantUrl.PLAY_RECORD).addParams("userid", UserManager.getInstance().getUserID())
                .addParams("isvip", UserManager.getInstance().isVip() ? "1" : "0").addParams("songid", videoManager.getPlaySong().getTid()).build().execute(
                new JsonCallback<BaseResult>(this) {


                    @Override
                    public void onGetDataSuccess(BaseResult data) {

                    }
                });
        title.setText(song.getName());

    }


    @OnClick(R.id.img_video_pause)
    public void onClick(View view) {
        onPauseOrResume();
    }

    private void onPauseOrResume() {
        show();
        if (videoManager.isPlaying()) {
            videoManager.onPause();
            mLoadingView.setVisibility(View.INVISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            imgVideoPause.setImageResource(R.drawable.video_resume);
        } else {
            videoManager.onResume();
            imgVideoPause.setImageResource(R.drawable.bg_rectangle_unfoucs);
        }
    }

    public void updatePausePlay() {

        if (videoManager.isPlaying()) {
            imgVideoPause.setImageResource(R.drawable.bg_rectangle_unfoucs);
        } else {
            imgVideoPause.setImageResource(R.drawable.video_resume);
            layoutBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoManager != null) {
            videoManager.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoManager != null) {
            videoManager.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoManager != null) {
            videoManager.onDestroy();
        }
        videoManager = null;
        mHandler.removeCallbacks(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mHelper.onSoftKeyDown1(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

   /* @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mHandler.removeCallbacks(forward);
        mHandler.removeCallbacks(back);
        return super.onKeyUp(keyCode, event);

    }*/

    //后退
    @Override
    public boolean toLeft(View view) {
       /* if (mVideoView != null && mVideoView.getCurrentPosition() >= 20) {
            mHandler.postDelayed(back, perTime);
            return true;
        }*/
        return false;
    }

    //前进
    @Override
    public boolean toRight(View view) {
       /* if (mVideoView != null && mVideoView.getDuration() - mVideoView.getCurrentPosition() >= 10) {
            mHandler.postDelayed(forward, perTime);
            return true;
        }*/

        return false;

    }

  /*  public Runnable forward = new Runnable() {
        @Override
        public void run() {
            long position = mVideoView.getCurrentPosition();
            long forward = mVideoView.getDuration() / 100;
            long start = position + forward;
            if (start >= mVideoView.getDuration()) {
                start = mVideoView.getDuration() - 10;
                mHandler.removeCallbacks(this);
            } else {
                mHandler.postDelayed(this, perTime);
            }
            videoManager.seekTo(start);
            show();
        }
    };


    public Runnable back = new Runnable() {
        @Override
        public void run() {
            long position = mVideoView.getCurrentPosition();
            long forward = mVideoView.getDuration() / 100;
            long start = position - forward;
            if (start <= 0) {
                start = 10;
                mHandler.removeCallbacks(this);
            } else {
                mHandler.postDelayed(this, perTime);
            }
            videoManager.seekTo(start);
            show();
        }
    };
*/

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


    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void show(int i) {
       if(!mShowing){
           mShowing = true;
           mHandler.sendEmptyMessage(SHOW_PROGRESS);
           layoutBottom.setVisibility(View.VISIBLE);
           title.setVisibility(View.VISIBLE);
           mHandler.sendEmptyMessageDelayed(FADE_OUT,i);
       }

    }

    @Override
    public void hide() {
        mShowing = false;
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.removeMessages(FADE_OUT);
        layoutBottom.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public void setAnchorView(View view) {

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    updatePausePlay();

                    break;
            }
        }
    };

    private long setProgress() {

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);

            }
            int percent = mVideoView.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));

        return position;
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }



}
