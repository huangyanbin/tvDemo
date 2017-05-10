package com.palmwifi.ktv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.open.androidtvwidget.keyboard.SkbContainer;
import com.open.androidtvwidget.keyboard.SoftKey;
import com.open.androidtvwidget.keyboard.SoftKeyBoardListener;
import com.open.androidtvwidget.recycler.BaseRecyclerViewHolder;
import com.open.androidtvwidget.recycler.OnItemClickListener;
import com.open.androidtvwidget.recycler.TvLinearLayoutManager;
import com.open.androidtvwidget.recycler.TvRecyclerView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.palmwifi.base.BaseActivity;
import com.open.androidtvwidget.recycler.OnItemFocusListener;
import com.palmwifi.helper.BaseEmptyView;
import com.palmwifi.ktv.adapter.SongAdapter;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.Contract;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.constact.SearchContract;
import com.palmwifi.ktv.helper.FavHelper;
import com.palmwifi.ktv.helper.HistoryHelper;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.presenter.SearchPresenter;
import com.palmwifi.ktv.views.VerticalProgressBar;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;

/**
 * Created by David on 2017/3/23.
 */

public class SearchActivity extends BaseActivity<SearchContract.Presenter> implements OnItemFocusListener,SearchContract.View ,ViewFocusHelper.OnSharkListener{
    @BindView(R.id.rl_home_content)
    RelativeMainLayout rlHomeContent;
    @BindView(R.id.tv_input)
    EditText tvInput;
    @BindView(R.id.skbContainer)
    SkbContainer skbContainer;
    @BindView(R.id.rv_home_hot)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.verticalProgressBar)
    VerticalProgressBar verticalProgressBar;
    @BindView(R.id.tv_pager_num)
    TextView tvPagerNum;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    private SoftKey mOldSoftKey;
    private SongAdapter mAdapter;
    TvLinearLayoutManager mLayoutManager;
    private BaseEmptyView emptyView;
    private ViewFocusHelper mSharkHelper;

    public static void startActivity(Context context) {

        Intent i = new Intent(context, SearchActivity.class);
        context.startActivity(i);
    }


    @Override
    protected int setLayoutID() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

        mSharkHelper = new ViewFocusHelper(this);
        rlHomeContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                if (newFocus != skbContainer) {
                    mSharkHelper.onFocusView(newFocus);
                } else {
                    mSharkHelper.onFocusView(newFocus, 1.0f);
                }
            }
        });
        for (int i = 0; i < rlHomeContent.getChildCount(); i++) {
            rlHomeContent.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.requestFocus();
                    }
                    return false;
                }
            });
        }
        skbContainer.setFocusable(true);
        skbContainer.setFocusableInTouchMode(true);
        skbContainer.setMoveSoftKey(false);
        skbContainer.setSelectSofkKeyFront(false);
        skbContainer.setSkbLayout(R.xml.ktv_all_key);
        skbContainer.setKeyScale(1.05f); // 设置按键放大.
        skbContainer.setOnSoftKeyBoardListener(new SoftKeyBoardListener() {
            @Override
            public void onCommitText(SoftKey softKey) {
                int keyCode = softKey.getKeyCode();
                String keyLabel = softKey.getKeyLabel();
                String searchContent = tvInput.getText() + softKey.getKeyLabel();
                if (!TextUtils.isEmpty(keyLabel)) { // 输入文字.
                    tvInput.setText(searchContent);
                    emptyView.startLoading();
                    mPresenter.requestSearchSong(true,searchContent.toLowerCase());
                } else {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        String text = tvInput.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            String str = text.substring(0, text.length() - 1);
                            tvInput.setText(str);
                            if(TextUtils.isEmpty(str)){
                                mPresenter.reqHotSongList(true);
                            }else{
                                mPresenter.requestSearchSong(true,str.toLowerCase());
                            }
                        }
                    }
                }

            }

            @Override
            public void onBack(SoftKey key) {
                finish();
            }

            @Override
            public void onDelete(SoftKey key) {
                String text = tvInput.getText().toString();
                tvInput.setText(text.substring(0, text.length() - 1));
            }

        });

        skbContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    if (mOldSoftKey != null)
                        skbContainer.setKeySelected(mOldSoftKey);
                    else
                        skbContainer.setDefualtSelectKey(0, 0);
                } else {
                    mOldSoftKey = skbContainer.getSelectKey();
                    skbContainer.setKeySelected(null);
                }
            }
        });
        ViewCompat.setRotation(verticalProgressBar, 180);
        emptyView = new BaseEmptyView.Builder(this, mRecyclerView).setEmptyIcon(R.drawable.fav_empty).build();
    }


    @Override
    protected void initData() {
        new SearchPresenter(this,this,this);
        initRecyclerView();
    }

    private void initRecyclerView() {

        mRecyclerView.setFocusable(false);
        mRecyclerView.setItemAnimator(null);
        mLayoutManager = new TvLinearLayoutManager(this);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setSelectedItemAtCentered(true);
        mRecyclerView.setOnLoadMoreListener(new TvRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(!mPresenter.isNoMore()) {
                    mPresenter.requestMore();
                }
            }
        });
        mAdapter = new SongAdapter(this, mRecyclerView, new ArrayList<Song>());
        mAdapter.setOnItemFocusListener(this);
        mAdapter.setShowRecommend(true);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.reqHotSongList(true);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder, int position) {
                SearchActivity.this.onItemClick(holder,position);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (skbContainer.onSoftKeyDown(keyCode, event))
            return true;
        if (mSharkHelper.onSoftKeyDown(keyCode, event)) {
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
        if (skbContainer.onSoftKeyUp(keyCode, event))
            return true;

        return super.onKeyDown(keyCode, event);
    }

    private void changedPagerNum(int position) {
        position += 1;
        verticalProgressBar.setProgress(position);
        tvPagerNum.setText(position + "/" + verticalProgressBar.getMax());
    }

    @Override
    public void onFocusItem(BaseRecyclerViewHolder viewHolder, int position) {
        changedPagerNum(position);
    }

    @Override
    public boolean onLeftItem(BaseRecyclerViewHolder viewHolder, int position) {
        skbContainer.requestFocus();
        return true;
    }

    @Override
    public boolean onRightItem(BaseRecyclerViewHolder viewHolder, int position) {

        return false;
    }

    @Override
    public boolean onTopItem(BaseRecyclerViewHolder viewHolder, int position) {
        return false;
    }

    @Override
    public boolean onBackItem(BaseRecyclerViewHolder viewHolder, int position) {
        if(!mAdapter.isShowRecommend()){
            mPresenter.reqHotSongList(true);
            mAdapter.setShowRecommend(true);
        }
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
            if(Contract.CLOSE_VIP || UserManager.getInstance().isVip()){
                HistoryHelper.addHistory(song);
                PLVideoActivity.startActivity(this, song);
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
    public void setPresenter(SearchContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void getSongListSuc(boolean isSearch, boolean isRefresh, List<Song> songs) {
        emptyView.stopLoading();
        if (songs != null && songs.size() > 0) {
            if (isRefresh) {
                mAdapter.mDataList.clear();
            }
            mAdapter.setShowRecommend(!isSearch);
            mAdapter.addDataList(songs);
            mAdapter.notifyDataSetChanged();
        }else{
            if (isRefresh) {
                emptyView.showNoData(isSearch ?getString(R.string.no_search_song_tip):getString(R.string.start_search_song));
            }
        }

    }

    @Override
    public void getTotalCount(int total) {
        if(total == 0){
            verticalProgressBar.setVisibility(View.INVISIBLE);
            tvPagerNum.setVisibility(View.INVISIBLE);
        }else{
            verticalProgressBar.setVisibility(View.VISIBLE);
            tvPagerNum.setVisibility(View.VISIBLE);
            verticalProgressBar.setMax(total);
        }
    }

    @Override
    public boolean toLeft(View view) {
        return false;
    }

    @Override
    public boolean toRight(View view) {

       if(view == skbContainer){
           if(mAdapter.getDataList() != null && mAdapter.getDataList().size() >0) {
               View firstItemView = mRecyclerView.getLayoutManager().findViewByPosition(mRecyclerView.getFirstVisiblePosition());
               if (firstItemView !=null){
                   firstItemView.requestFocus();
                   return true;
               }
           }
       }
       return false;
    }

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
        return false;
    }

    @Override
    public boolean toMenu(View view) {
        return false;
    }


}
