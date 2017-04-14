package com.songtzu.cartoon;

import java.io.IOException;

import android.widget.TextView;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.u.UiUtil;

public class AuthorActivity extends BaseActivity {

	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_author;
	}

	@Override
	public void init() {
		TextView tv=(TextView) findViewById(R.id.author_aboutTv);
		String c;
		try {
			c = UiUtil.getAsset(this, "about.json");
			tv.setText(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
