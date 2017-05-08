package com.palmwifi.ktv.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.palmwifi.ktv.R;

/**
 * Created by David on 2017/5/3.
 */


public class CommDialog extends Dialog {

    private static final int INVALID = 0;

    private CommDialog(Builder builder) {
        super(builder.context, builder.dialogStyle);
        Window window = this.getWindow();
        window.setGravity(builder.gravity);
        if (builder.animStyle == INVALID) {
            switch (builder.gravity) {
                case Gravity.CENTER:
                    window.setWindowAnimations(com.palmwifi.fragmention.R.style.center_dialog_animation);
                    break;
                case Gravity.BOTTOM:
                    window.setWindowAnimations(com.palmwifi.fragmention.R.style.bottom_dialog_animation);
                    break;
                case Gravity.TOP:
                    window.setWindowAnimations(com.palmwifi.fragmention.R.style.top_dialog_animation);
                    break;
                default:
                    break;
            }
        } else {
            window.setWindowAnimations(builder.animStyle);
        }
        window.getDecorView().setPadding(0, 0, 0, 0);
        View contentView = View.inflate(builder.context, R.layout.dialog_comm,null);
        TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content);
        contentTv.setText(builder.contentText != null ?builder.contentText :"");
        TextView confirmTv = (TextView) contentView.findViewById(R.id.dialog_ok);
        confirmTv.setText(builder.confirmText != null ?builder.confirmText :"确定");
        if(builder.onConfirmListener != null){
            confirmTv.setOnClickListener(builder.onConfirmListener);
        }else{
            confirmTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        TextView cancelTv = (TextView) contentView.findViewById(R.id.dialog_cancel);
        cancelTv.setText(builder.cancelText != null ?builder.cancelText :"取消");
        if(builder.onCancelListener != null){
            cancelTv.setOnClickListener(builder.onCancelListener);
        }else{
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        setContentView(contentView);

        this.setCanceledOnTouchOutside(true);
    }




    public static class Builder {
        private Context context;
        private int gravity;
        private int animStyle;
        private int dialogStyle = com.palmwifi.fragmention.R.style.base_dialog_style;
        private String contentText;
        private String confirmText;
        private String cancelText;
        private View.OnClickListener onConfirmListener;
        private View.OnClickListener onCancelListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setAnimStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        public Builder setDialogStyle(int dialogStyle) {
            this.dialogStyle = dialogStyle;
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setConfirmText(String confirmText) {
            this.confirmText = confirmText;
            return this;
        }

        public Builder setCacelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder setOnConfirmListener(View.OnClickListener onConfirmListener) {
            this.onConfirmListener = onConfirmListener;
            return this;
        }

        public Builder setOnCancelListener(View.OnClickListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public CommDialog build(){
           return new CommDialog(this);
        }
    }


}
