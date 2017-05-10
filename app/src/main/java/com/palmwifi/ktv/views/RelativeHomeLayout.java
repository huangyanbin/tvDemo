package com.palmwifi.ktv.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.open.androidtvwidget.view.RelativeMainLayout;

/**
 * Created by David on 2017/3/27.
 */

public class RelativeHomeLayout extends RelativeMainLayout {
    public RelativeHomeLayout(Context context) {
        super(context);

    }

    public RelativeHomeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void bringChildToFront(View child) {
        mWidgetTvViewBring.bringChildToFront(this, child);
    }
}
