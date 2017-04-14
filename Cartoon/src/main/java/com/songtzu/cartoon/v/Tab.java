package com.songtzu.cartoon.v;

import com.songtzu.cartoon.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Tab {
	private View tabView;
	private TextView titleTv;
	private ImageView symbolIv;

	public Tab(Context context) {
		initView(context);
	}

	private void initView(Context _c) {
		tabView = LayoutInflater.from(_c).inflate(R.layout.layout_tab, null);
		titleTv = (TextView) tabView.findViewById(R.id.titleTv);
		symbolIv = (ImageView) tabView.findViewById(R.id.symbolIv);
	}

	public View getView(){
		return tabView;
	}
	
	public void setText(String msg) {
		titleTv.setText(msg);
	}

	public void setText(int res) {
		titleTv.setText(res);
	}

	public void setTextColor(int color) {
		titleTv.setTextColor(color);
	}

	public void setSymbolVisibility(int v) {
		symbolIv.setVisibility(v);
	}

	public void setOnClickListener(OnClickListener listener) {
		titleTv.setOnClickListener(listener);
	}
}
