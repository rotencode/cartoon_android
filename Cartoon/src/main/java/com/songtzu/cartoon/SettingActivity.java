package com.songtzu.cartoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.Util;

public class SettingActivity extends BaseActivity {

	private SharedPreferences sp;
	private EditText maxSizeEt;

//	private MenuItem menuItem;
//	private boolean editing = false;

	private SeekBar seekBar;

	private int auto;

	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_setting;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences(Constants.MAXSIZE, 0);
		auto = sp.getInt(Constants.SIZE, Constants.AUTO);

		maxSizeEt = (EditText) findViewById(R.id.maxSizeEt);
		maxSizeEt.setText(String.valueOf(auto));
		maxSizeEt.setEnabled(false);

		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setMax(Constants.MAX - Constants.MIN);
		seekBar.setProgress(auto - Constants.MIN);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int size = progress + Constants.MIN;
				if (Util.DEBUG)
				Util.write("size is " + size);
				maxSizeEt.setText(String.valueOf(size));
			}
		});
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	// getMenuInflater().inflate(R.menu.edit, menu);
	// menuItem = menu.findItem(R.id.action_edit);
	// return super.onCreateOptionsMenu(menu);
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		// case R.id.action_edit:
		// if (editing) {
		// int size = Integer.parseInt(maxSizeEt.getText().toString());
		// if (size < 1000 || size > 3000) {
		// Util.write("size不符合标准");
		// } else {
		// menuItem.setTitle(R.string.action_edit);
		// editing = false;
		// maxSizeEt.setEnabled(false);
		// sp.edit().putInt(Constants.SIZE, size).commit();
		// }
		// } else {
		// editing = true;
		// maxSizeEt.setEnabled(true);
		// menuItem.setTitle(R.string.action_finish);
		// }
		// break;
		case android.R.id.home:
			back();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void back() {
		int size = seekBar.getProgress() + Constants.MIN;
		if (size != auto) {
			sp.edit().putInt(Constants.SIZE, size).commit();
			Intent intent = new Intent();
			intent.putExtra("size", seekBar.getProgress() + Constants.MIN);
			setResult(RESULT_OK, intent);
		}
		finish();
	}
}
