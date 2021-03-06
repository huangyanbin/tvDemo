package com.palmwifi.ktv;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.open.androidtvwidget.recycler.BaseRecyclerViewHolder;
import com.open.androidtvwidget.recycler.OnItemClickListener;
import com.open.androidtvwidget.recycler.OnItemFocusListener;
import com.open.androidtvwidget.recycler.TvLinearLayoutManager;
import com.open.androidtvwidget.recycler.TvRecyclerView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.palmwifi.base.BaseActivity;
import com.palmwifi.ktv.constact.HotFreeContract;
import com.palmwifi.ktv.adapter.HotFreeAdapter;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.event.FavEvent;
import com.palmwifi.ktv.event.TimeEvent;
import com.palmwifi.ktv.helper.FavHelper;
import com.palmwifi.ktv.helper.HistoryHelper;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.helper.ViewFocusHelper.OnSharkListener;
import com.palmwifi.ktv.manager.VideoManager;
import com.palmwifi.ktv.presenter.HotFreePresenter;
import com.palmwifi.ktv.presenter.SongPresenter;
import com.palmwifi.ktv.utils.ShakeUtils;
import com.palmwifi.utils.BaseUtils;
import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.sdk.commplatform.Commplatform;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/03/14
 *     desc   :首页
 *     version: 1.0
 * </pre
 */
public class HomeActivity extends BaseActivity<HotFreeContract.Presenter> implements HotFreeContract.View
        ,OnSharkListener, OnItemFocusListener,IMediaController {


    @BindView(R.id.rl_home_content)
    RelativeMainLayout rlHomeContent;
    @BindView(R.id.tv_home_date)
    TextView tvHomeDate;
    @BindView(R.id.tv_home_week)
    TextView tvHomeWeek;
    @BindView(R.id.tv_home_time)
    TextView tvHomeTime;
    @BindView(R.id.tv_home_personal_center)
    TextView tvHomePersonalCenter;
    @BindView(R.id.cv_home_new_recommend)
    ImageView cvHomeNewRecommend;
    @BindView(R.id.cv_home_rank)
    ImageView cvHomeRank;
    @BindView(R.id.cv_home_search)
    ImageView cvHomeSearch;
    @BindView(R.id.cv_home_singer)
    ImageView cvHomeSinger;
    @BindView(R.id.cv_home_fav)
    ImageView cvHomeFav;
    @BindView(R.id.cv_home_history)
    ImageView cvHomeHistory;
    @BindView(R.id.rv_home_hot)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.VideoView)
    PLVideoTextureView mVideoView;
    @BindView(R.id.LoadingView)
    View mLoadingView;
    @BindView(R.id.img_video_pause)
    View mPauseView;
    @BindView(R.id.v_video_shape)
    View videoShapeView;
    private ViewFocusHelper mSharkHelper;
    private HotFreeAdapter mAdapter;
    private VideoManager videoManager;

    private Handler mHandler = new Handler();
    private long mExitTime;


    /**
     * 设置布局ID
     *
     * @return 布局ID
     */
    @Override
    protected int setLayoutID() {
        return R.layout.activity_main;
    }

    /**
     * 初始化布局
     *
     */
    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mSharkHelper = new ViewFocusHelper(this);
        rlHomeContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                if (newFocus != videoShapeView) {
                    mSharkHelper.onFocusView(newFocus);
                } else {
                    mSharkHelper.onFocusView(newFocus, 1.0f);
                }
            }
        });
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoShapeView.requestFocus();
            }
        },500);
        mPauseView.setVisibility(View.INVISIBLE);
    }




    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        long currentTimeMillis = System.currentTimeMillis();
        tvHomeDate.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_YEAR));
        tvHomeWeek.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_WEEKDAY));
        tvHomeTime.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_TIME));
        new HotFreePresenter(this,this,this);
        mPresenter.requestHotFree();
        mPresenter.init();
        TimeService.startService(this);

    }

    private void initRecyclerView() {

        mRecyclerView.setFocusable(false);
        mRecyclerView.setItemAnimator(null);
        TvLinearLayoutManager mLayoutManager = new TvLinearLayoutManager(this);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setSelectedItemAtCentered(true);
        mAdapter.setOnItemFocusListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder, int position) {
                HomeActivity.this.onItemClick(holder,position);
            }
        });
    }


    @OnClick({R.id.tv_home_personal_center, R.id.cv_home_fav, R.id.cv_home_history,
            R.id.cv_home_new_recommend, R.id.cv_home_rank, R.id.cv_home_search, R.id.cv_home_singer, R.id.v_video_shape})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_home_personal_center:
                PersonalActivity.startActivity(this);
                break;
            case R.id.cv_home_search:
                SearchActivity.startActivity(this);
                break;
            case R.id.cv_home_rank:
                SongListActivity.startActivity(this, SongPresenter.HOT);
                break;
            case R.id.cv_home_history:
                SongListActivity.startActivity(this, SongPresenter.HISTORY);
                break;
            case R.id.cv_home_new_recommend:
                SongListActivity.startActivity(this, SongPresenter.NEW);
                break;
            case R.id.cv_home_fav:
                SongListActivity.startActivity(this, SongPresenter.FAV);
                break;
            case R.id.cv_home_singer:
                SearchSingerActivity.startActivity(this);
                break;
            case R.id.v_video_shape:
                if(videoManager.isPlaying()){
                    videoManager.onPause();
                    mPauseView.setVisibility(View.VISIBLE);
                }else{
                    videoManager.onResume();
                    mPauseView.setVisibility(View.INVISIBLE);
                }
                break;

        }
    }




    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDayChangedEvent(TimeEvent.DayChangedEvent event) {
        long currentTimeMillis = System.currentTimeMillis();
        tvHomeDate.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_YEAR));
        tvHomeWeek.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_WEEKDAY));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeChangedEvent(TimeEvent.TimeChangedEvent event) {
        long currentTimeMillis = System.currentTimeMillis();
        tvHomeTime.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_TIME));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavEvent(FavEvent event) {
        mAdapter.cancelFav(event.song);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (mSharkHelper.onSoftKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean toLeft(View view) {
        return false;
    }

    @Override
    public boolean toRight(View view) {
        switch (view.getId()) {
            case R.id.cv_home_new_recommend:
            case R.id.cv_home_rank:
            case R.id.cv_home_history:
            case R.id.tv_home_personal_center:
                ShakeUtils.simpleShakeBgPropertyAnim(view, mSharkHelper.getScaleSize());
                return true;
            case R.id.v_video_shape:
                mRecyclerView.setSelectedPosition(0);
               return true;

        }
        return false;
    }

    @Override
    public boolean toTop(View view) {
        return false;
    }

    @Override
    public boolean toDown(View view) {

        switch (view.getId()) {
            case R.id.cv_home_search:
            case R.id.cv_home_singer:
            case R.id.cv_home_history:
            case R.id.cv_home_fav:
                ShakeUtils.simpleShakeBgPropertyAnim(view, mSharkHelper.getScaleSize());
                return true;

        }
        return false;
    }

    @Override
    public boolean toBack(View view) {

        if ((System.currentTimeMillis() - mExitTime) > 3000) {
            Toast.makeText(getApplicationContext(), getString(R.string.exit_tip)
                    +getString(R.string.app_name),Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
        return true;
    }

    @Override
    public boolean toMenu(View view) {
        return false;
    }


    @Override
    public void onFocusItem(BaseRecyclerViewHolder viewHolder, int position) {

        mSharkHelper.onFocusView(viewHolder.getItemView(), 1.0f);
    }

    @Override
    public boolean onLeftItem(BaseRecyclerViewHolder viewHolder, int position) {
        videoShapeView.requestFocus();
        return true;
    }

    @Override
    public boolean onRightItem(BaseRecyclerViewHolder viewHolder, int position) {
        cvHomeNewRecommend.requestFocus();
        return true;
    }

    @Override
    public boolean onTopItem(BaseRecyclerViewHolder viewHolder, int position) {
        tvHomePersonalCenter.requestFocus();
        return true;
    }

    @Override
    public boolean onBackItem(BaseRecyclerViewHolder viewHolder, int position) {
        finish();
        return true;
    }

    @Override
    public boolean onItemClick(BaseRecyclerViewHolder viewHolder, int position) {
        Song song = mAdapter.getItemData(position);
        if (mAdapter.isSelectFav()) {
            if (song.isFav()) {
                FavHelper.deleteFav(song);
                mAdapter.notifyItemChanged(position);
                Toast.makeText(this,getString(R.string.cancel_fav), Toast.LENGTH_SHORT).show();
            } else {
                FavHelper.addFav(song);
                mAdapter.notifyItemChanged(position);
                Toast.makeText(this, getString(R.string.has_fav), Toast.LENGTH_SHORT).show();
            }
        } else {
            HistoryHelper.addHistory(song);
            mPauseView.setVisibility(View.INVISIBLE);
            mLoadingView.setVisibility(View.VISIBLE);
            videoManager.play(position);
        }
        return true;
    }

    @Override
    public boolean onBottomItem(BaseRecyclerViewHolder viewHolder, int position) {
        cvHomeFav.requestFocus();
        return true;
    }


    @Override
    public void setPresenter(HotFreeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getHotFreeSuc(List<Song> songList) {
        mAdapter = new HotFreeAdapter(this, mRecyclerView, songList);
        videoManager = new VideoManager(this,mVideoView,songList);
        videoManager.setLoadingView(mLoadingView);
        videoManager.play();

        initRecyclerView();

    }

    @Override
    public void initSuccess() {
        //Toast.makeText(this, "初始化成功！", Toast.LENGTH_SHORT).show();
        mPresenter.authPay();
    }

    @Override
    public void initFailure(String msg) {
    }

    @Override
    public void isHasPay(boolean isPay) {
        if(isPay) {
            Toast.makeText(this, R.string.welcome_vip_tip, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoManager != null) {
            videoManager.onPause();
            mLoadingView.setVisibility(View.INVISIBLE);
            mPauseView.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commplatform.getInstance().destroy();
        if(videoManager != null) {
            videoManager.onDestroy();
        }
        videoManager = null;
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {

    }

    @Override
    public void show() {
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void show(int i) {
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public void setAnchorView(View view) {

    }
}