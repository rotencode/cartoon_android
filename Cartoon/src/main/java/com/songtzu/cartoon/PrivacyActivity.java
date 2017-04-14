package com.songtzu.cartoon;

import java.io.IOException;

import android.widget.TextView;

import com.songtzu.cartoon.u.UiUtil;

public class PrivacyActivity extends BaseActivity {

	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_privacy;
	}

	@Override
	public void init() {
		TextView protocolTv = (TextView) findViewById(R.id.privacyTv);
		try {
			String fileName = UiUtil.getLanguage(this).equals("zh") ? "privacy.txt"
					: "privacy-en.txt";
			String line = UiUtil.getAsset(this, fileName);
			protocolTv.setText(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
