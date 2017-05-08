package com.palmwifi.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.palmwifi.fragmention.R;
import com.palmwifi.utils.BaseUtils;


public class BaseDialog extends Dialog {

	public static final int INVALID = 0;

	private BaseDialog(Builder builder) {
		super(builder.context, builder.dialogStyle);
		Window window = this.getWindow();
		window.setGravity(builder.gravity);
		if (builder.animStyle == INVALID) {
			switch (builder.gravity) {
			case Gravity.CENTER:
				window.setWindowAnimations(R.style.center_dialog_animation);
				break;
			case Gravity.BOTTOM:
				window.setWindowAnimations(R.style.bottom_dialog_animation);
				break;
				case Gravity.TOP:
					window.setWindowAnimations(R.style.top_dialog_animation);
				break;
			default:
				break;
			}
		} else {
			window.setWindowAnimations(builder.animStyle);
		}
		LayoutParams params = window.getAttributes();
		window.getDecorView().setPadding(0, 0, 0,0);
		BaseUtils.Rect rect = BaseUtils.getScreen((Activity) builder.context);
		if(builder.isFillHeight){
			params.height = rect.getHeight()- (builder.margin != null? builder.margin[1] + builder.margin[3] :0);
		}else {
			if(builder.height  == 0) {
				params.height = LayoutParams.WRAP_CONTENT;
			}else{
				params.height = builder.height;
			}
		}
		if(builder.isFillWidth){
			params.width = rect.getWidth()- (builder.margin != null? builder.margin[0] + builder.margin[2] :0);
		}else{
			if(builder.width == 0) {
				params.width = (int) (rect.getWidth() * 0.5);
			}else{
				params.width = builder.width;
			}
		}
		getWindow().setAttributes(params);
		onWindowAttributesChanged(params);
		if(builder.contentView != null){
			setContentView(builder.contentView);
		}
		this.setCanceledOnTouchOutside(true);
	}


	public void show(View contentView){
		setContentView(contentView);
		show();
	}


	public static class Builder {
		private Context context;
		private int gravity;
		private int animStyle;
		private int dialogStyle = R.style.base_dialog_style;
		private boolean isFillWidth;
		private boolean isFillHeight;
		private View contentView;
		private int width;
		private int height;
		private int margin[];

		public Builder(Context context) {
			this.context = context;
			int margin = context.getResources().getDimensionPixelSize(R.dimen.dimen_30);
			this.margin = new int[4];
			this.margin[0] = margin;
			this.margin[1] = 0;
			this.margin[2] = margin;
			this.margin[3] = 0;
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

		public Builder setFillWidth(boolean isFillWidth) {
			this.isFillWidth = isFillWidth;
			return this;
		}
		public Builder setFillHeight(boolean isFillHeight) {
			this.isFillHeight = isFillHeight;
			return this;
		}
		public Builder setContentView(View contentView) {
			this.contentView = contentView;
			return this;
		}

		public Builder setWidth(int width) {
			this.width = width;
			return this;
		}

		public Builder setHeight(int height) {
			this.height = height;
			return this;
		}
		public Builder setMargin(int left, int top, int right, int bottom) {
			this.margin = new int[4];
			margin[0] = left;
			margin[1] = top;
			margin[2] = right;
			margin[3] = bottom;
			return this;
		}

		public BaseDialog create() {
			return new BaseDialog(this);
		}

	}
}
