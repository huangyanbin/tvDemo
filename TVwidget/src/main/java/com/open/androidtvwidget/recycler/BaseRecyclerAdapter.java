package com.open.androidtvwidget.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

/**
 * Created by liuyu on 17/2/15.
 * <p>
 * note:防止RecyclerView更新数据的时候焦点丢失
 * <p>
 * (1)adapter执行setHasStableIds(true)方法
 * (2)重写getItemId()方法,让每个view都有各自的id
 * (3)RecyclerView的动画必须去掉
 */
public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerViewHolder, T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    public LayoutInflater mInflate;
    protected OnItemFocusListener onItemFocusListener;
    protected OnItemClickListener onItemClickListener;
    public List<T> mDataList;

    public BaseRecyclerAdapter(Context context, List<T> dataList) {
        this.mInflate = LayoutInflater.from(context);
        this.mDataList = dataList;
        setHasStableIds(true);
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void addDataList(List<T> dataList) {
        this.mDataList.addAll(dataList);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItemData(int position){
        return mDataList.get(position);
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, final int position) {

        onBindBaseViewHolder((VH) holder, position);
        if(onItemClickListener != null){
            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder,position);
                }
            });

        }
    }

    protected abstract void onBindBaseViewHolder(VH viewHolder, int position);

    /**
     * @param hasStableIds 有多个observer的话会报错
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }


    public void setKeyDispath(final TvRecyclerView recyclerView,final BaseRecyclerViewHolder viewHolder,final int position){


        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    onFocus(view,position);
                    if (onItemFocusListener != null) {
                        onItemFocusListener.onFocusItem(viewHolder, position);
                    }
                } else {
                    onUnFocus(view,position);
                }
            }
        });
        viewHolder.itemView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(isInterceptKeyCode(view,keyCode,event)){
                    return true;
                }
                if (onItemFocusListener != null && event.getAction() == KeyEvent.ACTION_DOWN) {

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (recyclerView.isLeftEdge(position) &&
                                    onItemFocusListener.onLeftItem(viewHolder, position)) {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if (onItemFocusListener.onRightItem(viewHolder, position)) {
                                return true;
                            }

                            break;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (recyclerView.isTopEdge(position) &&
                                    onItemFocusListener.onTopItem(viewHolder, position)) {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (recyclerView.isBottomEdge(position) &&
                                    onItemFocusListener.onBottomItem(viewHolder, position)) {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_BACK:
                            if(onItemFocusListener.onBackItem(viewHolder, position)) {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_DPAD_CENTER:

                            if(onItemFocusListener.onItemClick(viewHolder,position)){

                                return true;
                            }
                            break;

                    }
                }
                return false;
            }
        });
    }

    public void onFocus(View view,int position){

    }

    public void onUnFocus(View view,int position){

    }



    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isInterceptKeyCode(View view, int keyCode, KeyEvent event){
        return false;
    }
    public OnItemFocusListener getOnItemFocusListener() {
        return onItemFocusListener;
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemFocusListener) {
        this.onItemFocusListener = onItemFocusListener;
    }
}
