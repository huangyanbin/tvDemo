package com.palmwifi.ktv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.open.androidtvwidget.recycler.BaseRecyclerViewHolder;
import com.open.androidtvwidget.recycler.OnItemClickListener;
import com.open.androidtvwidget.recycler.OnItemFocusListener;
import com.open.androidtvwidget.recycler.TvLinearLayoutManager;
import com.open.androidtvwidget.recycler.TvRecyclerView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.palmwifi.base.BaseActivity;
import com.palmwifi.helper.BaseEmptyView;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.constact.SongContract;
import com.palmwifi.ktv.adapter.SongAdapter;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.event.FavEvent;
import com.palmwifi.ktv.event.TimeEvent;
import com.palmwifi.ktv.helper.FavHelper;
import com.palmwifi.ktv.helper.ItemFocusHelper;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.presenter.SongPresenter;
import com.palmwifi.ktv.views.VerticalProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by David on 2017/3/27.
 */

public class SongListActivity extends BaseActivity<SongContract.Presenter> implements OnItemFocusListener, SongContract.View {

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
    @BindView(R.id.rv_video_list)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.verticalProgressBar)
    VerticalProgressBar verticalProgressBar;
    @BindView(R.id.tv_pager_num)
    TextView tvPagerNum;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    private ItemFocusHelper mHelper;
    private SongAdapter mAdapter;
    private BaseEmptyView emptyView;
    private int pageType;
    private ViewFocusHelper mSharkHelper;


    public static void startActivity(Context context, int type) {

        Intent i = new Intent(context, SongListActivity.class);
        i.putExtra("pageType", type);
        context.startActivity(i);
    }

    public static void startActivity(Context context, String singerID) {

        Intent i = new Intent(context, SongListActivity.class);
        i.putExtra("pageType", SongPresenter.SINGER);
        i.putExtra("singerID", singerID);
        context.startActivity(i);
    }

    @Override
    protected int setLayoutID() {
        return R.layout.activity_song_list;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mSharkHelper = new ViewFocusHelper();
        rlHomeContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                mSharkHelper.onFocusView(newFocus);
            }
        });
        mHelper = new ItemFocusHelper();
        pageType = getIntent().getIntExtra("pageType", SongPresenter.HISTORY);
        initRecyclerView();
        ViewCompat.setRotation(verticalProgressBar, 180);
    }

    private void initRecyclerView() {

        mRecyclerView.setFocusable(false);
        mRecyclerView.setItemAnimator(null);
        TvLinearLayoutManager mLayoutManager = new TvLinearLayoutManager(this);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setSelectedItemAtCentered(true);
        mRecyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (!mPresenter.isNoMore()) {
                    mPresenter.requestData();
                }
            }
        });
        mAdapter = new SongAdapter(this, mRecyclerView, new ArrayList<Song>());
        mAdapter.setOnItemFocusListener(this);
        mAdapter.setFavPager(pageType == SongPresenter.FAV);
        mRecyclerView.setAdapter(mAdapter);
        emptyView = new BaseEmptyView.Builder(this, rlContent).setEmptyIcon(R.drawable.fav_empty).build();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder, int position) {
               SongListActivity.this.onItemClick(holder,position);
            }
        });
    }

    @Override
    protected void initData() {
        long currentTimeMillis = System.currentTimeMillis();
        tvHomeDate.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_YEAR));
        tvHomeWeek.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_WEEKDAY));
        tvHomeTime.setText(DateUtils.formatDateTime(this, currentTimeMillis, DateUtils.FORMAT_SHOW_TIME));
        if (pageType == SongPresenter.SINGER) {
            String singerID = getIntent().getStringExtra("singerID");
            new SongPresenter(this, this, this, singerID);
        } else {
            new SongPresenter(this, this, this, pageType);
        }
        emptyView.startLoading();
        mPresenter.requestData();
    }


    @OnClick({R.id.tv_home_personal_center})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_home_personal_center:
                PersonalActivity.startActivity(this);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mHelper.onSoftKeyDown(keyCode, event)) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mHelper.onSoftKeyUp(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void changedPagerNum(int position) {
        position += 1;
        int total = mAdapter.getItemCount();
        verticalProgressBar.setProgress(position);
        tvPagerNum.setText(position + "/" + total);
    }

    @Override
    public void onFocusItem(BaseRecyclerViewHolder viewHolder, int position) {
        changedPagerNum(position);
    }

    @Override
    public boolean onLeftItem(BaseRecyclerViewHolder viewHolder, int position) {
        return false;
    }

    @Override
    public boolean onRightItem(BaseRecyclerViewHolder viewHolder, int position) {
        tvHomePersonalCenter.requestFocus();
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
            if (mAdapter.isFavPager()) {
                song.setFav(false);
                FavHelper.deleteFav(song);
                mAdapter.deletePosition(position);
                EventBus.getDefault().post(new FavEvent(song));
                if (mAdapter.getItemCount() == 0) {
                    emptyView.showNoData();
                }
            } else {
                if (song.isFav()) {
                    FavHelper.deleteFav(song);
                    mAdapter.notifyItemChanged(position);
                    Toast.makeText(this,getString(R.string.cancel_fav), Toast.LENGTH_SHORT).show();
                } else {
                    FavHelper.addFav(song);
                    mAdapter.notifyItemChanged(position);
                    Toast.makeText(this, getString(R.string.has_fav), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if(UserManager.getInstance().isVip()){
                mPresenter.addHistory(song);
                VideoActivity.startActivity(this, song);
            }else{
                Toast.makeText(this,getString(R.string.vip_tip),Toast.LENGTH_SHORT).show();
               PersonalActivity.startActivity(this);
            }
        }
        return true;
    }

    @Override
    public boolean onBottomItem(BaseRecyclerViewHolder viewHolder, int position) {
        return false;
    }




    @Override
    public void setPresenter(SongContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void noData() {
        emptyView.showNoData();
    }

    @Override
    public void requestDataSuc(List<Song> songs) {

        emptyView.stopLoading();
        mAdapter.addDataList(songs);
        mAdapter.notifyDataSetChanged();
       verticalProgressBar.setMax(mAdapter.getItemCount());
        tvPagerNum.setText(0 + "/" + mAdapter.getItemCount());

    }

    @Override
    public void getTotalCount(int totalCount) {
        if (totalCount == 0) {
            verticalProgressBar.setVisibility(View.INVISIBLE);
            tvPagerNum.setVisibility(View.INVISIBLE);
        } else {
            verticalProgressBar.setVisibility(View.VISIBLE);
            tvPagerNum.setVisibility(View.VISIBLE);
           // verticalProgressBar.setMax(totalCount);

        }

    }


}
