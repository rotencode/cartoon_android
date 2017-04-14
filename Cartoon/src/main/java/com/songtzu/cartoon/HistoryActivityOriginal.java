package com.songtzu.cartoon;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.c.DeleteImp;
import com.songtzu.cartoon.e.HistoryItem;
import com.songtzu.cartoon.u.Constants.Source;
import com.songtzu.cartoon.u.SDCardUtil;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.anim.AlphaInAnimationAdapter;
import com.songtzu.cartoon.u.anim.ArrayAdapter;
import com.songtzu.cartoon.u.anim.ComboAnimation;
import com.songtzu.cartoon.u.anim.ZoomUtil;
import com.songtzu.cartoon.u.image.ImageManager;
import com.songtzu.cartoon.v.MagicImageView;
import com.songtzu.cartoon.v.RemindDialog;

public class HistoryActivityOriginal extends BaseActivity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, Runnable, DeleteImp,
		OnPageChangeListener {
	private Activity activity;

	private GridView gridView;

	private GridAdapter adapter;

	private ZoomUtil util;

	private View detailView;
	private ViewPager viewPager;
	private TextView indexTv;
	private DetailAdapter detailAdapter;
	private ArrayList<String> fileList;

	private MenuItem delItem, shareItem;

	private RemindDialog dialog;

	private class GridAdapter extends ArrayAdapter<String> {

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ImageView imageView = (ImageView) convertView;

			if (imageView == null) {
				imageView = new ImageView(activity);
				imageView.setScaleType(ImageView.ScaleType.CENTER);
			}

			ImageManager.from(activity).displayImage(imageView,
					getItem(position).toString(), R.mipmap.logo, 120, 120,
					Source.SDCARD);
			return imageView;
		}
	}

	private class DetailAdapter extends PagerAdapter {

		private ArrayList<HistoryItem> items;

		public DetailAdapter() {
			items = new ArrayList<HistoryItem>();
		}

		public void setData(ArrayList<String> _data) {
			if (_data == null)
				return;
			items.clear();
			for (String p : _data) {
				HistoryItem item = new HistoryItem(activity);
				item.setPath(p);
				item.setOnClickListener(HistoryActivityOriginal.this);
				items.add(item);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object o) {
			// container.removeViewAt(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 设置ViewPager指定位置要显示的view
			HistoryItem view = items.get(position);
//			ImageView im = view.getView();
//			if (!view.isInited()) {
//				items.get(position).setInited(true);
//				ImageManager.from(activity).displayImage(im, view.getPath(),
//						R.drawable.logo, Source.SDCARD);
//				container.addView(im);
//			}
			
			MagicImageView miv=new MagicImageView(activity);
			ImageManager.from(activity).displayImage(miv, view.getPath(),
					R.mipmap.logo, Source.SDCARD);
			container.addView(miv);
			return miv;
		}

		public void destory() {
			if (items != null) {
				items.clear();
				items = null;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_delete:
		case R.id.action_share:
			int index = viewPager.getCurrentItem();
			if (index < fileList.size() && index >= 0) {
				String path = fileList.get(index);
				if (item.getItemId() == R.id.action_share) {
					// 分享
					UiUtil.shareImg(activity, path);
				} else {
					// 删除
					if (dialog == null) {
						dialog = new RemindDialog(activity);
						dialog.setTitle(R.string.delete_title);
						dialog.setOk(R.string.delete_ok);
						dialog.setCallback(this);
					}
					dialog.show();
				}
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.detail, menu);
		delItem = menu.findItem(R.id.action_delete);
		shareItem = menu.findItem(R.id.action_share);
		delItem.setVisible(false);
		shareItem.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Log.d("Carrot", "long click " + arg2);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (util != null) {
			menuEnable(true);
			viewPager.setCurrentItem(position, false);
			indexTv.setText((position + 1) + "/" + fileList.size());
			util.zoomImageFromThumb(arg1, position);
//			arg1.animate().
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (util == null) {
			util = new ZoomUtil(gridView, detailView);
		}
	}

	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_history_original;
	}

	@Override
	public void init() {
		activity = this;

		initGridView();

		initDetailView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (util != null) {
			util.destory();
			util = null;
		}
		if (detailAdapter != null) {
			detailAdapter.destory();
			detailAdapter = null;
		}
		if (adapter != null) {
			adapter = null;
		}
		fileList.clear();
		fileList = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& detailView.getVisibility() == View.VISIBLE) {
			menuEnable(false);
			util.hide();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void run() {
		getItems();
	}

	@Override
	public void onClick(View v) {
		menuEnable(false);
		util.hide();
	}

	private void initGridView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new GridAdapter();

		AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(
				adapter);
		alphaInAnimationAdapter.setAbsListView(gridView);
		gridView.setAdapter(alphaInAnimationAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);

		setGridViewAnimation();
	}

	private void setGridViewAnimation() {
		float[] f1 = new float[] { 0f, 0.95f };// scale
		float[] f2 = new float[] { 0f, 1f };// alpha
		ComboAnimation comboanimation = new ComboAnimation(null, null, null,
				f1, f2, null, 0.5F, 0.5F, false, false);
		comboanimation.setDuration(500L);
		comboanimation.setFillAfter(true);
		GridLayoutAnimationController gridlayoutanimationcontroller = new GridLayoutAnimationController(
				comboanimation,
				(float) (0.05000000074505806D + 0.23000000417232513D * Math
						.random()),
				(float) (0.05000000074505806D + 0.23000000417232513D * Math
						.random()));
		gridlayoutanimationcontroller.setOrder(2);
		gridView.setLayoutAnimation(gridlayoutanimationcontroller);
	}

	private void initDetailView() {
		detailView = findViewById(R.id.itemContainer);

		viewPager = (ViewPager) findViewById(R.id.viewpager);

		detailAdapter = new DetailAdapter();
		viewPager.setAdapter(detailAdapter);
		viewPager.setOnPageChangeListener(this);

		indexTv = (TextView) findViewById(R.id.indexTv);
		indexTv.setText("0");
		new Thread(this).start();
	}

	public void menuEnable(boolean enable) {
		delItem.setVisible(enable);
		shareItem.setVisible(enable);
	}

	private void getItems() {
		File dir = new File(SDCardUtil.SONGTZU_IMAGE);
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				return filename.endsWith("jpg") || filename.endsWith("bmp")
						|| filename.endsWith("png");
			}
		};
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			findViewById(R.id.noDataTv).setVisibility(View.VISIBLE);
		}
		File[] files = dir.listFiles(filter);
		if (files.length == 0) {
			findViewById(R.id.noDataTv).setVisibility(View.VISIBLE);
		}
		fileList = new ArrayList<String>(files.length);
		for (File f : files) {
			fileList.add(f.getAbsolutePath());
		}
		adapter.addAll(fileList);

		detailAdapter.setData(fileList);
		indexTv.setText("1/" + fileList.size());
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		indexTv.setText((position + 1) + "/" + fileList.size());
	}

	@Override
	public void delete() {
		int index = viewPager.getCurrentItem();
		File file = new File(fileList.get(index));
		if (file != null && file.exists())
			file.delete();
		adapter.remove(index);
		fileList.remove(index);
		detailAdapter = new DetailAdapter();
		detailAdapter.setData(fileList);
		viewPager.setAdapter(detailAdapter);
		if (index < fileList.size() && index >= 0) {
			viewPager.setCurrentItem(index, false);
		} else {
			// 图片删除完了
			menuEnable(false);
			util.hide();
			findViewById(R.id.noDataTv).setVisibility(View.VISIBLE);
		}
	}
}
