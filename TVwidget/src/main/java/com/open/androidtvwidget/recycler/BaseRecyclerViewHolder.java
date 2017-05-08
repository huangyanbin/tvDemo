package com.open.androidtvwidget.recycler;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by liuyu on 17/1/20.
 * 使用RecyclerView时,所有的ViewHolder需要继承该ViewHolder
 */
public  class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> childViews;
    private final View view;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        childViews = new SparseArray<>();
    }


    public View getChildView(int id) {
        View child = childViews.get(id);
        if (child == null) {
            child = getItemView().findViewById(id);
            childViews.put(id, child);
        }
        return child;
    }

    public View getItemView() {
        return view;
    }

    public BaseRecyclerViewHolder setText(int id, String text) {
        if (text == null) {
            text = "";
        }
        TextView textView = (TextView) getChildView(id);
        textView.setText(text);
        return this;
    }

    public BaseRecyclerViewHolder setImageResource(int id, int drawableID) {

        ImageView imageView = (ImageView) getChildView(id);
        imageView.setImageResource(drawableID);
        return this;
    }



}
