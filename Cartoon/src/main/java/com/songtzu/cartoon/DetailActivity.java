package com.songtzu.cartoon;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.c.DeleteImp;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.Constants.Source;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.Util;
import com.songtzu.cartoon.u.image.ImageManager;
import com.songtzu.cartoon.v.MagicImageView;
import com.songtzu.cartoon.v.RemindDialog;

public class DetailActivity extends BaseActivity implements
		ViewPager.OnPageChangeListener, DeleteImp {
	private ViewPager viewPager;

	private ViewPagerAdapter adapter;

	private ArrayList<String> list;
	/** 当前选中的图片 */
	private int currentPic;
	private int perWidth;
	/** 选择的照片文件夹 */
	public final static String EXTRA_DATA = "extra_data";
	/** 当前被选中的照片 */
	public final static String EXTRA_CURRENT_PIC = "extra_current_pic";
	public final static String EXTRA_WIDTH = "extra_width";

	private RemindDialog dialog;

	private boolean update = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			back();
			return true;
		case R.id.action_delete:
			// 删除
			if (currentPic < 0 || currentPic >= list.size()) {
				return false;
			}
			if (dialog == null) {
				dialog = new RemindDialog(this);
				dialog.setTitle(R.string.delete_title);
				dialog.setOk(R.string.delete_ok);
				dialog.setCallback(this);
			}
			dialog.show();
			return true;
		case R.id.action_share:
			if (currentPic < 0 || currentPic >= list.size()) {
				return false;
			}
			UiUtil.shareImg(this, list.get(currentPic));
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void initData() {
		list = getIntent().getStringArrayListExtra(EXTRA_DATA);
		currentPic = getIntent().getIntExtra(EXTRA_CURRENT_PIC, 0);
		perWidth = getIntent().getIntExtra(EXTRA_WIDTH, 0);
		setCustomTitle(currentPic);

		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(currentPic);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		currentPic = position;
		setCustomTitle(currentPic);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private Activity context = this;

	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			MagicImageView zoomImageView = new MagicImageView(context);
			String path = list.get(position);
			ImageManager.from(DetailActivity.this).displayImage(zoomImageView,
					path, -1, Source.SDCARD);
			container.addView(zoomImageView);
			return zoomImageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
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

	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_detail;
	}

	@Override
	public void init() {
		viewPager = (ViewPager) findViewById(R.id.vp_content);
		initData();
	}

	private void setCustomTitle(int pos) {
		if (Util.DEBUG)
			Util.write("currentPic is " + currentPic);
		// File file = new File(list.get(pos));
		// if (file != null && file.exists()) {
		// String fileName = file.getName();
		// setTitle(fileName.substring(0, fileName.lastIndexOf(".")) + "("
		// + ((pos + 1) + "/" + list.size() + ")"));
		// }
		// file = null;
		setTitle((pos + 1) + "/" + list.size());
	}

	@Override
	public void delete() {
		File file = new File(list.get(currentPic));
		if (file == null || !file.exists()) {
			return;
		}
		// 移除缓存图片
		ImageManager.from(this).remove(file.getAbsolutePath());
		ImageManager.from(this).remove(file.getAbsolutePath(), perWidth,
				perWidth);
		file.delete();
		list.remove(currentPic);
		update = true;
		if (list.size() == 0) {
			back();
			return;
		}
		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		if (currentPic == list.size()) {
			--currentPic;
		}
		viewPager.setCurrentItem(currentPic);
		setCustomTitle(currentPic);
	}

	private void back() {
		if (update) {
			Intent data = new Intent();
			data.putExtra(EXTRA_DATA, list);
			setResult(Constants.STATE_OK, data);
		}
		finish();
	}
}
