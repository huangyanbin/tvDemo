package com.open.androidtvwidget.recycler;
/**
 * Created by David on 2017/3/30.
 */

public interface OnItemFocusListener {

    void onFocusItem(BaseRecyclerViewHolder viewHolder,int position);

    boolean onLeftItem(BaseRecyclerViewHolder viewHolder,int position);
    boolean onRightItem(BaseRecyclerViewHolder viewHolder,int position);

    boolean onTopItem(BaseRecyclerViewHolder viewHolder,int position);

    boolean onBackItem(BaseRecyclerViewHolder viewHolder,int position);

    boolean onItemClick(BaseRecyclerViewHolder viewHolder,int position);

    boolean onBottomItem(BaseRecyclerViewHolder viewHolder,int position);

}
