package com.songtzu.cartoon;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.UiUtil;

public class AboutActivity extends BaseActivity implements OnClickListener{
	
	private TextView verTv;
	
	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_about;
	}

	@Override
	public void init() {
		verTv=(TextView)findViewById(R.id.about_versionTv);
		verTv.setText("V1.1");
		
		findViewById(R.id.about_authorTv).setOnClickListener(this);
		findViewById(R.id.about_appraiseTv).setOnClickListener(this);
		findViewById(R.id.about_suggestionTv).setOnClickListener(this);
		findViewById(R.id.about_websiteTv).setOnClickListener(this);
		findViewById(R.id.about_privacyTv).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_authorTv:
			Intent auIntent=new Intent(this, AuthorActivity.class);
			startActivity(auIntent);
			break;
		case R.id.about_appraiseTv:
			Uri uri = Uri.parse("market://details?id="+getPackageName());  
			Intent aIntent = new Intent(Intent.ACTION_VIEW,uri);  
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			startActivity(aIntent);
			break;
		case R.id.about_websiteTv:
			UiUtil.loadUrl(this, Constants.PATH_ROUTE);
			break;
		case R.id.about_privacyTv:
			Intent pIntent=new Intent(this, PrivacyActivity.class);
			startActivity(pIntent);
			break;
		case R.id.about_suggestionTv:
			Intent fIntent=new Intent(this,FeedbackActivity.class);
			startActivity(fIntent);
			break;
		default:
			break;
		}
	}
}
