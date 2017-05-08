package com.palmwifi.ktv.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.open.androidtvwidget.recycler.BaseRecyclerAdapter;
import com.open.androidtvwidget.recycler.BaseRecyclerViewHolder;
import com.open.androidtvwidget.recycler.TvRecyclerView;
import com.palmwifi.ktv.R;
import com.palmwifi.ktv.bean.Singer;
import com.palmwifi.ktv.helper.ItemFocusHelper;

import java.util.List;

/**
 * Created by liuyu on 17/1/20.
 * 防止RecyclerView更新数据的时候焦点丢失:
 * (1)adapter执行setHasStableIds(true)方法
 * (2)重写getItemId()方法,让每个view都有各自的id
 * (3)RecyclerView的动画必须去掉
 */
public class SingerAdapter extends BaseRecyclerAdapter<BaseRecyclerViewHolder, Singer> {

    private TvRecyclerView mRecyclerView;


    public SingerAdapter(Context context, TvRecyclerView recyclerView, List<Singer> dataList) {
        super(context, dataList);
        this.mRecyclerView = recyclerView;

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = mInflate.inflate(R.layout.item_singer, viewGroup, false);
        return new BaseRecyclerViewHolder(itemView);
    }


    @Override
    protected void onBindBaseViewHolder(final BaseRecyclerViewHolder viewHolder, final int position) {


        Singer singer = mDataList.get(position);
        viewHolder.setText(R.id.tv_singer_name, singer.getName());
        if(singer.getUrl() != null) {
            Uri uri = Uri.parse(singer.getUrl());
            SimpleDraweeView iconView = (SimpleDraweeView) viewHolder.getChildView(R.id.img_singer_icon);
            iconView.setImageURI(uri);
        }
        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (onItemFocusListener != null) {
                        onItemFocusListener.onFocusItem(viewHolder, position);
                    }
                }
            }
        });
        setKeyDispath(mRecyclerView,viewHolder,position);
    }

    @Override
    public void onFocus(View view, int position) {
        onFocusAnim(view);
    }

    @Override
    public void onUnFocus(View view, int position) {
        onUnFocusAnim(view);
    }


    public  void onFocusAnim(View view){
        view.clearAnimation();
        ViewCompat.setScaleX(view,1);
        ViewCompat.setScaleY(view,1);
        SpringForce spring = new SpringForce(1.05f)
                .setDampingRatio(0.7f)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        final SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.05f).setSpring(spring);
        final SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.05f).setSpring(spring);
        animX.start();
        animY.start();
    }

    public  void onUnFocusAnim(View view){
        view.clearAnimation();
        SpringForce spring = new SpringForce(1f)
                .setDampingRatio(0.7f)
                .setStiffness(SpringForce.STIFFNESS_LOW);
        final SpringAnimation animX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1).setSpring(spring);
        final SpringAnimation animY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1).setSpring(spring);
        animX.start();
        animY.start();
    }
}
