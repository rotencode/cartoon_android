package com.songtzu.cartoon.m;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.UiUtil;

public class MenuFragement extends BaseFragment implements OnClickListener {
	
	@Override
	protected int layout() {
		return R.layout.activity_main_menu;
	}

	@Override
	protected void init() {
		findViewById(R.id.menu_aboutTv).setOnClickListener(this);
		findViewById(R.id.menu_guidTv).setOnClickListener(this);
		findViewById(R.id.menu_historyTv).setOnClickListener(this);
		findViewById(R.id.menu_settingTv).setOnClickListener(this);
		findViewById(R.id.menu_reviewTv).setOnClickListener(this);
		findViewById(R.id.menu_feedbackTv).setOnClickListener(this);
		findViewById(R.id.menu_websiteTv).setOnClickListener(this);
		findViewById(R.id.menu_protocolTv).setOnClickListener(this);
		
		TextView versionTv=(TextView)findViewById(R.id.menu_versionTv);
		versionTv.setText(getVersionName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_aboutTv:
			start(Constants.ABOUT);
			break;
		case R.id.menu_guidTv:
			start(Constants.OPT_GUIDE);
			break;
		case R.id.menu_historyTv:
			start(Constants.HISTORY);
			break;
		case R.id.menu_settingTv:
			startForResult(Constants.SETTING);
			break;
		case R.id.menu_reviewTv:
			Uri uri = Uri.parse("market://details?id="
					+ getActivity().getPackageName());
			Intent aIntent = new Intent(Intent.ACTION_VIEW, uri);
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(aIntent);
			break;
		case R.id.menu_feedbackTv:
			start(Constants.FEEDBACK);
			break;
		case R.id.menu_websiteTv:
			UiUtil.loadUrl(getActivity(), getString(R.string.share_url));
			break;
		case R.id.menu_protocolTv:
			start(Constants.PROTOCOL);
			break;
		default:
			break;
		}
	}

	private void start(String action) {
		Intent i = new Intent(action);
		startActivity(i);
	}
	
	private void startForResult(String action){
		Intent i=new Intent(action);
		getActivity().startActivityForResult(i, Constants.RESOURCE_SETTING);
	}

	private String getVersionName(){
		// 获取packagemanager的实例
		PackageManager packageManager = getActivity().getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getActivity()
					.getPackageName(), 0);
			return "V"+packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
