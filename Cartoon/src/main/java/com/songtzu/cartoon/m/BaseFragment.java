package com.songtzu.cartoon.m;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by DongShu.Shu on 2014/12/25.
 */
public abstract class BaseFragment extends Fragment {
	private View contentView;
	
	public Activity mParent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(layout(), container, false);
		init();
		return contentView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mParent=activity;
	}

	protected abstract int layout();

	protected abstract void init();

	protected View findViewById(int id) {
		return contentView.findViewById(id);
	}
}
