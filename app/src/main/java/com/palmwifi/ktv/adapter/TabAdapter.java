package com.palmwifi.ktv.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.open.androidtvwidget.adapter.BaseTabTitleAdapter;
import com.open.androidtvwidget.view.OpenTabHost;
import com.open.androidtvwidget.view.TextViewWithTTF;
import com.palmwifi.ktv.R;
import com.palmwifi.ktv.bean.SingerType;

import java.util.List;

public class TabAdapter extends BaseTabTitleAdapter {
	private List<SingerType> tabNames;
	private int position;

	public TabAdapter(List<SingerType> tabNames) {
		this.tabNames = tabNames;
	}

	@Override
	public int getCount() {
		return tabNames.size();
	}



	@Override
	public Integer getTitleWidgetID(int pos) {
		return 100000+position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.getContext();
		String title = tabNames.get(position).getName();
		if (convertView == null) {
			convertView = newTabIndicator(parent.getContext(), title, false);
			convertView.setId(getTitleWidgetID(position)); // 设置ID.
		} else {
		}
		return convertView;
	}


	private View newTabIndicator(Context context, String tabName, boolean focused) {
		final String name = tabName;
		View viewC = View.inflate(context, R.layout.tab_view_indicator_item, null);
		TextViewWithTTF view = (TextViewWithTTF) viewC.findViewById(R.id.tv_tab_indicator);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(20, 0, 20, 0);
		view.setLayoutParams(lp);
		view.setText(name);

		if (focused) {
			Resources res = context.getResources();
			view.setTextColor(res.getColor(android.R.color.white));
			view.setTypeface(null, Typeface.BOLD);
			view.requestFocus();
		}
		return viewC;
	}


	public void switchFocusTab(OpenTabHost openTabHost, int position) {
		List<View> viewList = openTabHost.getAllTitleView();
		if(viewList.size() > position && position >= 0){
			View view = viewList.get(position);
			if(view != null) {
				view.setFocusable(true);
				view.requestFocus();
			}
		}

	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
