package com.palmwifi.view.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.palmwifi.fragmention.R;
import com.palmwifi.helper.ILoading;


public class TextDialogLoading implements ILoading {

	private BaseDialog mDialog;
	private Context mContext;
	private AnimationDrawable mLoadingAnimDrawable;
	private View loadingView;
	private TextView mLoadingTv;
	private String loading;

	public TextDialogLoading(Context context) {

		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loadingView = inflater.inflate(R.layout.dialog_text_loading, null);
		mLoadingTv = (TextView) loadingView.findViewById(R.id.tv_dialog_loading);
		loading = "正在加载...";
		mLoadingTv.setText(loading);
		mDialog = new BaseDialog.Builder(context).setGravity(Gravity.CENTER)
				.setDialogStyle(R.style.loading_dialog_style).setContentView(loadingView).create();
		mDialog.setCanceledOnTouchOutside(false);
	}

	/**
	 * 开始加载
	 */
	@Override
	public void startLoading() {

		if (mContext != null) {
			ImageView progressView = (ImageView) loadingView
					.findViewById(R.id.iv_dialog_loading);
			if (mLoadingAnimDrawable == null) {
				mLoadingAnimDrawable = (AnimationDrawable) mContext
						.getResources().getDrawable(
								R.drawable.loading_anim_list);
			}
			progressView.setImageDrawable(mLoadingAnimDrawable);
			mLoadingAnimDrawable.start();
			if (mDialog != null) {
				mDialog.show();
			}
		}
	}

	/**
	 * 完成加载
	 */
	@Override
	public void stopLoading() {

		if (mLoadingAnimDrawable != null)
			mLoadingAnimDrawable.stop();
		if (mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
	}

	@Override
	public void onProgress(float progress) {
		//mLoadingTv.setText(loading+(int)progress+"%");
	}

	@Override
	public void showFailure() {

	}

	@Override
	public void showNoData() {

	}


	public void setLoadingTip(String loading){
		this.loading = loading;
		mLoadingTv.setText(loading);
	}

}
