package com.palmwifi.ktv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.open.androidtvwidget.keyboard.SkbContainer;
import com.open.androidtvwidget.keyboard.SoftKey;
import com.open.androidtvwidget.keyboard.SoftKeyBoardListener;
import com.open.androidtvwidget.recycler.BaseRecyclerViewHolder;
import com.open.androidtvwidget.recycler.OnItemClickListener;
import com.open.androidtvwidget.recycler.TvGridLayoutManager;
import com.open.androidtvwidget.recycler.TvRecyclerView;
import com.open.androidtvwidget.view.OpenTabHost;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.open.androidtvwidget.view.SmoothHorizontalScrollView;
import com.palmwifi.base.BaseActivity;
import com.open.androidtvwidget.recycler.OnItemFocusListener;
import com.palmwifi.helper.BaseEmptyView;
import com.palmwifi.ktv.adapter.SingerAdapter;
import com.palmwifi.ktv.adapter.TabAdapter;
import com.palmwifi.ktv.bean.Singer;
import com.palmwifi.ktv.bean.SingerType;
import com.palmwifi.ktv.constact.SearchSingerContract;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.presenter.SearchSingerPresenter;
import com.palmwifi.ktv.views.VerticalProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by David on 2017/3/23.
 */

public class SearchSingerActivity extends BaseActivity<SearchSingerContract.Presenter> implements OnItemFocusListener,
        ViewFocusHelper.OnSharkListener, OpenTabHost.OnTabSelectListener, SearchSingerContract.View {

    @BindView(R.id.rl_home_content)
    RelativeMainLayout rlHomeContent;
    @BindView(R.id.tv_input)
    EditText tvInput;
    @BindView(R.id.skbContainer)
    SkbContainer skbContainer;
    @BindView(R.id.rv_home_hot)
    TvRecyclerView mRecyclerView;
    @BindView(R.id.openTabHost)
    OpenTabHost mOpenTabHost;
    @BindView(R.id.verticalProgressBar)
    VerticalProgressBar verticalProgressBar;
    @BindView(R.id.tv_pager_num)
    TextView tvPagerNum;
    @BindView(R.id.hscroll_view)
    SmoothHorizontalScrollView horizontalScrollView;
    private SoftKey mOldSoftKey;
    private SingerAdapter mAdapter;
    TvGridLayoutManager mLayoutManager;
    private TabAdapter tabAdapter;
    private BaseEmptyView emptyView;
    private ViewFocusHelper mSharkHelper;


    public static void startActivity(Context context) {

        Intent i = new Intent(context, SearchSingerActivity.class);
        context.startActivity(i);
    }


    @Override
    protected int setLayoutID() {
        return R.layout.activity_singer_search;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mSharkHelper = new ViewFocusHelper(this);
        skbContainer.setFocusable(true);
        skbContainer.setFocusableInTouchMode(true);
        skbContainer.setMoveSoftKey(false);
        skbContainer.setSoftKeySelectPadding(2);
        skbContainer.setSelectSofkKeyFront(false);
        skbContainer.setSkbLayout(R.xml.ktv_all_key);
        skbContainer.setKeyScale(1.0f); // 设置按键放大.
        skbContainer.setOnSoftKeyBoardListener(new SoftKeyBoardListener() {
            @Override
            public void onCommitText(SoftKey softKey) {
                int keyCode = softKey.getKeyCode();
                String keyLabel = softKey.getKeyLabel();
                String searchContent = tvInput.getText() + softKey.getKeyLabel();
                if (!TextUtils.isEmpty(keyLabel)) { // 输入文字.
                    tvInput.setText(searchContent);
                    emptyView.startLoading();
                    mPresenter.searchSingerList(true,searchContent.toLowerCase());
                    horizontalScrollView.setVisibility(View.INVISIBLE);
                } else {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        String text = tvInput.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            String str = text.substring(0, text.length() - 1);
                            tvInput.setText(str);
                            if(TextUtils.isEmpty(str)){
                                horizontalScrollView.setVisibility(View.VISIBLE);
                                mPresenter.requestSingerList(true,tabAdapter.getPosition());
                            }else{
                                mPresenter.searchSingerList(true,str.toLowerCase());
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
                    mSharkHelper.setFocusView(skbContainer);
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
        skbContainer.requestFocus();
        emptyView = new BaseEmptyView.Builder(this, mRecyclerView).setEmptyIcon(R.drawable.fav_empty).build();

    }


    @Override
    protected void initData() {
        new SearchSingerPresenter(this, this, this);
        initRecyclerView();
        emptyView.startLoading();
        mPresenter.requestSingerType();

    }


    private void initRecyclerView() {

        mRecyclerView.setFocusable(false);
        mRecyclerView.setItemAnimator(null);
        mLayoutManager = new TvGridLayoutManager(this, 5);
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
        mAdapter = new SingerAdapter(this, mRecyclerView, new ArrayList<Singer>());
        mAdapter.setOnItemFocusListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewHolder holder, int position) {
                SearchSingerActivity.this.onItemClick(holder,position);
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
        return super.onKeyDown(keyCode, event);
    }


    private void changedPagerNum(int position) {
        position += 1;
        int total = mAdapter.getItemCount();
        verticalProgressBar.setMax(total);
        verticalProgressBar.setProgress(position);
        tvPagerNum.setText(position + "/" + total);
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
        if(!mPresenter.getIsSearch()) {
            tabAdapter.switchFocusTab(mOpenTabHost, tabAdapter.getPosition());
            return true;
        }
        return false;
    }

    @Override
    public boolean toLeft(View view) {
        return false;
    }

    @Override
    public boolean toRight(View view) {
        if (view == skbContainer) {
            if(mPresenter.getIsSearch()){
                if(mAdapter.getDataList() != null && mAdapter.getDataList().size() >0) {
                    View firstItemView = mRecyclerView.getLayoutManager()
                            .findViewByPosition(mRecyclerView.getFirstVisiblePosition());
                    if (firstItemView !=null){
                        firstItemView.requestFocus();
                        return true;
                    }
                }
            }else {
                tabAdapter.switchFocusTab(mOpenTabHost, tabAdapter.getPosition());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean toTop(View view) {
        if (view == skbContainer) {
            tabAdapter.switchFocusTab(mOpenTabHost, tabAdapter.getPosition());
            return true;
        }
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
        return false;
    }

    @Override
    public boolean onBackItem(BaseRecyclerViewHolder viewHolder, int position) {
        if(horizontalScrollView.getVisibility() != View.VISIBLE){
            horizontalScrollView.setVisibility(View.VISIBLE);
            emptyView.startLoading();
            mPresenter.requestSingerList(true,tabAdapter.getPosition());
        }else{
            finish();
        }
        return true;
    }

    @Override
    public boolean onItemClick(BaseRecyclerViewHolder viewHolder, int position) {
        String singerID = mAdapter.getItemData(position).getTid();
        SongListActivity.startActivity(this, singerID);
        return true;
    }

    @Override
    public boolean onBottomItem(BaseRecyclerViewHolder viewHolder, int position) {
        return false;
    }

    @Override
    public void onTabSelect(OpenTabHost openTabHost, View titleWidget, int position) {
        tabAdapter.setPosition(position);
        mPresenter.requestSingerList(true, position);
    }


    @Override
    public void setPresenter(SearchSingerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getSingerType(List<SingerType> singerTypes) {
        mOpenTabHost = (OpenTabHost) findViewById(R.id.openTabHost);
        tabAdapter = new TabAdapter(singerTypes);
        mOpenTabHost.setOnTabSelectListener(this);
        mOpenTabHost.setAdapter(tabAdapter);
    }

    @Override
    public void getSingerListSuc(boolean isSearch,boolean isRefresh, List<Singer> singers) {
        emptyView.stopLoading();
        if (singers != null && singers.size() > 0) {
            if (isRefresh) {
             mAdapter.mDataList.clear();
            }
            mAdapter.addDataList(singers);
            mAdapter.notifyDataSetChanged();

        }else{
            if (isRefresh) {
                emptyView.showNoData(isSearch ?getString(R.string.no_search_singer_tip):getString(R.string.start_search_singer));
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
}
