package com.palmwifi.ktv.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.open.androidtvwidget.recycler.BaseRecyclerAdapter;
import com.open.androidtvwidget.recycler.BaseRecyclerViewHolder;
import com.open.androidtvwidget.recycler.OnItemFocusListener;
import com.open.androidtvwidget.recycler.TvRecyclerView;
import com.palmwifi.ktv.R;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.helper.ItemFocusHelper;

import java.util.List;

/**
 * Created by liuyu on 17/1/20.
 * 防止RecyclerView更新数据的时候焦点丢失:
 * (1)adapter执行setHasStableIds(true)方法
 * (2)重写getItemId()方法,让每个view都有各自的id
 * (3)RecyclerView的动画必须去掉
 */
public class SongAdapter extends BaseRecyclerAdapter<BaseRecyclerViewHolder, Song> {

    private ItemFocusHelper mHelper;
    private boolean isShowRecommend;
    private boolean isFavPager;
    private TvRecyclerView mRecyclerView;

    public SongAdapter(Context context, TvRecyclerView recyclerView, List<Song> dataList) {
        super(context, dataList);
        mHelper = new ItemFocusHelper();
        this.mRecyclerView = recyclerView;

    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = mInflate.inflate(R.layout.item_base_video, viewGroup, false);
        return new BaseRecyclerViewHolder(itemView);
    }


    public void setShowRecommend(boolean isShow) {
        isShowRecommend = isShow;
    }

    @Override
    protected void onBindBaseViewHolder(final BaseRecyclerViewHolder viewHolder, final int position) {


        Song song = mDataList.get(position);
        viewHolder.setText(R.id.tv_song_name, song.getName())
                .setText(R.id.tv_singer, song.getSinger())
                .setImageResource(R.id.img_hot_fav, isFavPager ? R.drawable.ic_delete_fav_selector
                        :(song.isFav() ? R.drawable.ic_has_fav_selector :R.drawable.ic_fav_selector))
                .getChildView(R.id.img_recommend)
                .setVisibility(isShowRecommend ? View.VISIBLE : View.INVISIBLE);
        setKeyDispath(mRecyclerView,viewHolder,position);
    }

    public OnItemFocusListener getOnItemFocusListener() {
        return onItemFocusListener;
    }


    public void setFavPager(boolean isFav){
        this.isFavPager = isFav;
    }

    public boolean isFavPager() {
        return isFavPager;
    }

    public boolean  isSelectFav(){
        return mHelper.isSelectedFav();
    }

    @Override
    public void onFocus(View view, int position) {
        mHelper.onFocus(view);
    }

    @Override
    public void onUnFocus(View view, int position) {
        mHelper.onUnFocus(view);
    }

    public boolean isShowRecommend() {
        return isShowRecommend;
    }

    @Override
    public boolean isInterceptKeyCode(View view, int keyCode, KeyEvent event) {
        return mHelper.onSoftKeyDown(keyCode,event);
    }

    public void deletePosition(int position){
        mDataList.remove(position);
        notifyItemRemoved(position);
    }


}
