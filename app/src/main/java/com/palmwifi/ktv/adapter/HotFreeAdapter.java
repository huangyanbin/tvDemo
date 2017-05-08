package com.palmwifi.ktv.adapter;

import android.content.Context;
import android.text.TextUtils;
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
public class HotFreeAdapter extends BaseRecyclerAdapter<BaseRecyclerViewHolder, Song> {

    private ItemFocusHelper mHelper;
    private TvRecyclerView mRecyclerView;

    public HotFreeAdapter(Context context, TvRecyclerView recyclerView, List<Song> dataList) {
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
        View itemView = mInflate.inflate(R.layout.item_home_hot, viewGroup, false);
        return new BaseRecyclerViewHolder(itemView);
    }


    @Override
    protected void onBindBaseViewHolder(final BaseRecyclerViewHolder viewHolder, final int position) {


        Song song = mDataList.get(position);
        viewHolder.setText(R.id.tv_song_name, song.getName())
                .setImageResource(R.id.img_hot_fav, song.isFav()
                        ? R.drawable.ic_home_has_fav_selector : R.drawable.ic_home_fav_selector);
        setKeyDispath(mRecyclerView, viewHolder, position);
    }

    public OnItemFocusListener getOnItemFocusListener() {
        return onItemFocusListener;
    }


    public boolean isSelectFav() {
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

    @Override
    public boolean isInterceptKeyCode(View view, int keyCode, KeyEvent event) {
        return mHelper.onSoftKeyDown(keyCode, event);
    }

    public void deletePosition(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }


    public void cancelFav(Song song) {
        int size = mDataList.size();
        for (int i = 0; i <= size; i++) {
            Song s = mDataList.get(i);
            if(TextUtils.equals(s.getUrl(), song.getUrl())) {
                s.setFav(song.isFav());
                notifyItemChanged(i);
                break;
            }
        }
    }

}
