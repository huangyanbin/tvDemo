package com.palmwifi.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.palmwifi.fragmention.R;
import com.palmwifi.view.loadingballs.BallView;


/**
 * Created by huangyanbin on 2016/8/8.
 * 加载,空白提示,网络错误重新加载通用类
 * 为了适用网络请求加载loading，实现了ILoading接口
 */

public  class BaseEmptyView implements ILoading{

    public static final int HIDE = 0;
    public static final int LOADING = 1;
    public static final int FAILURE = 2;
    public static final int NO_DATA = 3;
    private View contentView;
    private View emptyView;
    private Builder builder;
    public int status;
    private TextView tipTv;
    private ImageView tipImg;
    private BallView tipPb;

    private BaseEmptyView(Builder builder) {
        this.builder = builder;
        contentView = builder.contentView;
        emptyView = View.inflate(builder.context, R.layout.layout_empty, null);
        genEmptyView();
        emptyView.setVisibility(View.GONE);
        status = HIDE;
    }
    private void showLoading() {
        if (emptyView != null) {
            if (builder.loadingIcon != 0) {
                tipImg.setVisibility(View.VISIBLE);
                tipImg.setImageResource(builder.loadingIcon);
            } else {
                tipImg.setVisibility(View.INVISIBLE);
            }
           tipTv.setText(builder.loadingTip != null ? builder.loadingTip
                    : builder.context.getString(R.string.empty_loading_tip));
            tipPb.setVisibility(View.VISIBLE);
            emptyView.setClickable(false);
        }
        status = LOADING;
        show();
    }

    @Override
    public void showFailure() {

        if (emptyView != null) {
            if (builder.failureIcon != 0) {
                tipImg.setVisibility(View.VISIBLE);
                tipImg.setImageResource(builder.failureIcon);
            } else {
                tipImg.setVisibility(View.INVISIBLE);
            }
           tipTv.setText(builder.failureTip != null ? builder.failureTip
                    : builder.context.getString(R.string.empty_loading_failure_tip));
            tipPb.setVisibility(View.INVISIBLE);
            if(builder.onFailureClickListener != null){
                emptyView.setClickable(true);
                emptyView.setOnClickListener(builder.onFailureClickListener);
            }
        }
        status = FAILURE;
        show();
    }
    @Override
    public void showNoData() {

        if (emptyView != null) {
            if (builder.emptyIcon != 0) {
                tipImg.setVisibility(View.VISIBLE);
                tipImg.setImageResource(builder.emptyIcon);
            } else {
                tipImg.setVisibility(View.INVISIBLE);
            }
           tipTv.setText(builder.noDataTip != null ? builder.noDataTip
                    : builder.context.getString(R.string.empty_loading_no_data_tip));
            emptyView.setClickable(false);
            tipPb.setVisibility(View.INVISIBLE);
        }
        status = NO_DATA;
        show();
    }

    public void showNoData(String noDataTip) {

        if (emptyView != null) {
            if (builder.emptyIcon != 0) {
                tipImg.setVisibility(View.VISIBLE);
                tipImg.setImageResource(builder.emptyIcon);
            } else {
                tipImg.setVisibility(View.INVISIBLE);
            }
           tipTv.setText(noDataTip != null ? noDataTip
                    : builder.context.getString(R.string.empty_loading_no_data_tip));
            emptyView.setClickable(false);
            tipPb.setVisibility(View.INVISIBLE);
        }
        status = NO_DATA;
        show();
    }


    private void show() {
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }

    private void hide() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
        status = HIDE;
    }

    public void startLoading() {
        if(status != LOADING) {
            showLoading();
        }
    }

    public void stopLoading() {
       /* if(status == LOADING) {*/
            hide();
       /* }*/
    }

    @Override
    public void onProgress(float progress) {

    }




    private void genEmptyView() {
        ViewParent parent = contentView.getParent();
        if(parent != null) {
            if (parent instanceof ViewGroup) {
                if (parent instanceof LinearLayout) {
                    ((LinearLayout) parent).addView(emptyView);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) emptyView.getLayoutParams();
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = -1;
                    lp.gravity = Gravity.CENTER;
                    emptyView.setLayoutParams(lp);
                } else if (parent instanceof RelativeLayout) {
                    ((ViewGroup) parent).addView(emptyView);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) emptyView.getLayoutParams();
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    emptyView.setLayoutParams(lp);
                } else if (parent instanceof FrameLayout) {
                    ((ViewGroup) parent).addView(emptyView);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) emptyView.getLayoutParams();
                    lp.height = -1;
                    lp.gravity = Gravity.CENTER;
                    emptyView.setLayoutParams(lp);
                }
            }
        }
        tipTv = (TextView) emptyView.findViewById(R.id.tv_empty_tip);
        tipPb = (BallView) emptyView.findViewById(R.id.pb_empty_tip);
        tipImg = (ImageView) emptyView.findViewById(R.id.img_empty_tip);
    }


    public static class Builder{
        View contentView;
        String loadingTip;
        String failureTip;
        String noDataTip;
        Context context;
        private int emptyIcon;
        private int failureIcon;
        private int loadingIcon;
        View.OnClickListener onFailureClickListener;

        public Builder( Context context,View contentView) {
            this.context = context;
            this.contentView = contentView;
        }

        public Builder setLoadingTip(String loadingTip) {
            this.loadingTip = loadingTip;
            return this;
        }

        public Builder setFailureTip(String failureTip) {
            this.failureTip = failureTip;
            return this;
        }

        public Builder setNoDataTip(String noDataTip) {
            this.noDataTip = noDataTip;
            return this;
        }
        public Builder setFailureIcon(int failureIcon) {
            this.failureIcon = failureIcon;
            return this;
        }

        public Builder setLoadingIcon(int loadingIcon) {
            this.loadingIcon = loadingIcon;
            return this;
        }

        public Builder setEmptyIcon(int emptyIcon) {
            this.emptyIcon = emptyIcon;
            return this;
        }

        public BaseEmptyView build() {
            return new BaseEmptyView(this);
        }

        public Builder setOnFailureClickListener(View.OnClickListener onFailureClickListener) {
            this.onFailureClickListener = onFailureClickListener;
            return this;
        }
    }
}
