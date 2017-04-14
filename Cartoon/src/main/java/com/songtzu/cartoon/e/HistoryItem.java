package com.songtzu.cartoon.e;

import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HistoryItem {
	private String path;
	private boolean inited;
	private ImageView magicView;
	private Context mContext;

	public HistoryItem(Context context) {
		mContext = context;
		initView();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isInited() {
		return inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	private void initView() {
		magicView=new ImageView(mContext);
		magicView.setScaleType(ScaleType.CENTER);
	}

	public ImageView getView() {
		return magicView;
	}

	public void setOnClickListener(OnClickListener listener) {
		magicView.setOnClickListener(listener);
	}
}
