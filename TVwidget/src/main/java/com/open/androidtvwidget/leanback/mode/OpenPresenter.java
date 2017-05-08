package com.open.androidtvwidget.leanback.mode;

import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;

/**
 * Created by hailongqiu on 2016/8/22.
 */
public abstract class OpenPresenter {

    /**
     * 基本数据类型(ViewHolder)
     */
    public static class ViewHolder {

        private final View view;
        private SparseArray<View> childViews;

        public ViewHolder(View view) {
            this.view = view;
            childViews = new SparseArray<>();
        }

        public View getChildView(int id){
            View child  = childViews.get(id);
            if(child == null){
                child = view.findViewById(id);
                childViews.put(id,child);
            }
            return child;
        }

        public View getItemView(){
            return view;
        }

        public ViewHolder  setText(int id,String text){
            if(text == null){
                 text ="";
            }
            TextView textView = (TextView) getChildView(id);
            textView.setText(text);
            return this;
        }

        public ViewHolder setImageResource(int id, int drawableID) {

            ImageView imageView = (ImageView) getChildView(id);
            imageView.setImageResource(drawableID);
            return this;
        }

    }

    public int getItemCount() {
        return 0;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public void onViewAttachedToWindow(ViewHolder viewHolder) {

    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {

    }

    public void onBindViewHolder(ViewHolder viewHolder, Object item) {

    }

    public void onViewDetachedFromWindow(ViewHolder viewHolder) {

    }

    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    public void setAdapter(GeneralAdapter adapter) {

    }


}
