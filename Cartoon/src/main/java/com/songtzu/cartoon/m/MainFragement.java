package com.songtzu.cartoon.m;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.c.ImageImp;
import com.songtzu.cartoon.e.Model;
import com.songtzu.cartoon.e.ModelView;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.Constants.Source;
import com.songtzu.cartoon.u.SDCardUtil;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.Util;
import com.songtzu.cartoon.u.image.ImageManager;
import com.songtzu.cartoon.u.image.ImageUtil;
import com.songtzu.cartoon.v.MagicImageView;
import com.songtzu.cartoon.v.Tab;
import com.songtzu.lib.CallSongtzulib;

public class MainFragement extends BaseFragment implements Runnable, ImageImp {

	private static final int HINT = Color.argb(127, 0, 0, 0);

	private String srcPath = "";
	private boolean delSrc;

	private ArrayList<ModelView> modelViews;
	private ViewPager viewPager;
	private LinearLayout titleLayout;
	private HorizontalScrollView scrollView;
	private int current = 0;
	private int selected = -1;
	private String dst;

	private CustomHandler handler;

	private MainFrameActivity mParent;

	private int maxSize;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mParent = (MainFrameActivity) activity;
	}

	@Override
	protected int layout() {
		return R.layout.activity_main_body;
	}

	@Override
	protected void init() {
		titleLayout = (LinearLayout) findViewById(R.id.titleLayout);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		scrollView = (HorizontalScrollView) findViewById(R.id.horizontalView);

		initData();

		initViewPager();

		initHandler();
		CallSongtzulib.getLib().setCallback(this);
	}

	private void initData() {
		ArrayList<Model> models = UiUtil.getModels(mParent);
		int len = models.size();
		modelViews = new ArrayList<ModelView>(len);

		for (int i = 0; i < len; ++i) {
			Model model = models.get(i);

			Tab tab = new Tab(mParent);
			tab.setText(model.getName());
			tab.setTextColor(HINT);
			tab.setSymbolVisibility(View.INVISIBLE);
			tab.setOnClickListener(new ClickListener(i));
			titleLayout.addView(tab.getView()); // 把点点添加到容器中
			MagicImageView im = new MagicImageView(mParent);
			// im.setPadding(10, 0, 10, 0);
			im.setImageResource(UiUtil.getResouce(mParent, model.getlRes()));

			ModelView modelView = new ModelView(im, tab, model);
			modelViews.add(modelView);
		}
		setTab(Color.RED, View.VISIBLE);
		models.clear();
		models = null;

		SharedPreferences sp = getActivity().getSharedPreferences(
				Constants.MAXSIZE, 0);
		maxSize = sp.getInt(Constants.SIZE, Constants.AUTO);
		sp = null;
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		viewPager.setAdapter(new CustomPagerAdapter());
		viewPager.addOnPageChangeListener(new ChangeListener());
		viewPager.setCurrentItem(0);

	}

	private void initHandler() {
		handler = new CustomHandler(this);
	}

	/*
	 * public void effect(String path) { srcPath = path; new
	 * Thread(this).start(); }
	 */

	public void effect(String path, boolean del) {
		delSrc = del;
		srcPath = path;
		new Thread(this).start();
	}

	public void setSize(int size) {
		maxSize = size;
	}

	/**
	 * 保存图片
	 */
	public void save() {
		String src = modelViews.get(current).model.getRstPath();
		boolean saved = modelViews.get(current).model.isSaved();
		if (TextUtils.isEmpty(src)) {// 当前模式下没有处理过的图片
			Util.showMsg(mParent, R.string.nopic2save);
		} else if (saved) {// 当前处理的图片已经保存过了 ，不需要再次保存
			Util.showMsg(mParent, R.string.saved);
		} else {
			Util.showMsg(mParent,
					getString(R.string.savepath, SDCardUtil.SONGTZU_IMAGE));
			try {
				ImageUtil.save2file(src);
				modelViews.get(current).model.setSaved(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * alert share dialog
	 */
	public void share() {
		String path = modelViews.get(current).model.getRstPath();
		if (TextUtils.isEmpty(path)) {// 如果当前模式下没有处理过图片，不进行分享操作
			Util.showMsg(mParent, R.string.nopic2share);
			return;
		}
		UiUtil.shareImg(mParent, path);
	}

	/**
	 * 设置title的文字属性
	 * 
	 * @param color
	 *            字体颜色
	 * @param vi
	 *            红色标记显示与否
	 */
	private void setTab(int color, int vi) {
		modelViews.get(current).tab.setTextColor(color);
		modelViews.get(current).tab.setSymbolVisibility(vi);
	}

	private class ClickListener implements OnClickListener {
		private int index;

		ClickListener(int _index) {
			index = _index;
		}

		@Override
		public void onClick(View v) {
			// viewPager.setCurrentItem(index);
			if (Math.abs(index - current) == 1) {
				viewPager.setCurrentItem(index);
			} else {
				viewPager.setCurrentItem(index, false);
			}
		}
	}

	private class CustomPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return modelViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object o) {
			// container.removeViewAt(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 设置ViewPager指定位置要显示的view
			ModelView view = modelViews.get(position);
			MagicImageView im = view.imageView;
			if (!view.isInited()) {
				modelViews.get(position).setInited(true);
				container.addView(im);
			}
			return im;
		}
	}

	private class ChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			setTab(HINT, View.INVISIBLE);
			current = position;
			setTab(Color.RED, View.VISIBLE);
			scroll();
		}
	}

	@Override
	public void handle(int code) {
		Util.write("handle is called selected is " + selected + " dst is "
				+ dst);
		if (selected != -1 && !TextUtils.isEmpty(dst)) {
			remove(dst);
			setImage(dst, selected);
		}
	}

	private static final int REQUEST_CODE = 1;
	private void requestMultiplePermissions() {
		String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
		requestPermissions(permissions, REQUEST_CODE);
	}




	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE) {
			int grantResult = grantResults[0];
			boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
			Log.i("songtzucartoon", "onRequestPermissionsResult granted=" + granted);
			effect(current);

		}
	}


	@Override
	public void run() {

		if (Build.VERSION.SDK_INT >= 23) {
			if (!(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
				if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
					Toast.makeText(getContext(), "Please grant the permission this time", Toast.LENGTH_LONG).show();
				}
				requestMultiplePermissions();
			} else {
				effect(current);
			}
		}else {	//低于23，默认赋予权限。
			effect(current);
		}




	}

	/**
	 * 调用处理
	 * 
	 * @param index
	 */
	private void effect(int index) {
		File file = new File(srcPath);
		if (file == null || !file.exists()) {// 检查源文件是否存在
			return;
		}
		selected = index;

		mParent.setProcess(true);
		setImage(srcPath, index);// 读取SDCard里的图片文件，显示

		boolean del = false | delSrc;
		Bitmap bitmap = ImageUtil.getImage(srcPath);
		if (bitmap.getWidth() > maxSize || bitmap.getHeight() > maxSize) {
			Bitmap bm = ImageUtil.getImage(bitmap, maxSize);
			if (Util.DEBUG)
			Util.write("源图片w:" + bitmap.getWidth() + " h:" + bitmap.getHeight()
					+ " 新图片w:" + bm.getWidth() + " h:" + bm.getHeight());
			if (delSrc) {
				file.delete();
				remove(srcPath);
				if (Util.DEBUG)
				Util.write("删除拍照的图片" + srcPath);
			}
			srcPath = ImageUtil.save2file(getActivity(), bm, 100);
			bm.recycle();
			bm = null;
			file = new File(srcPath);
			del = true;
		}
		bitmap.recycle();
		bitmap = null;

		String proName = modelViews.get(index).model.getName();// 模式名称
		String fileName = file.getName();// 源文件名称
		String suffix = fileName.substring(fileName.lastIndexOf("."),
				fileName.length());// 源文件后缀

		dst = SDCardUtil.SONGTZU_IMAGE + proName + suffix;// 目标文件
		if (Util.DEBUG)
		Util.write("srcPath " + srcPath + " dst " + dst);
		File dstFile = new File(dst);
		if (dst != null && dstFile.exists()) {// 如果目标文件已经存在了，删除
			dstFile.delete();
		}

		int flag = modelViews.get(index).model.getFlag();
		int res = CallSongtzulib.getLib().Effect(srcPath, dst, flag);
		if (Util.DEBUG)
		Util.write("reslut is " + res);
		// switch (res) {
		// case Constants.STATE_OK:// 如果成功，保存地址
		modelViews.get(index).model.setRstPath(dst);
		modelViews.get(index).model.setSaved(false);
		remove(dst);
		setImage(dst, index);// 显示处理后的图片
		// break;
		//
		// default:
		// break;
		// }
		if (del) {
			if (Util.DEBUG)
			Util.write("删除临时文件");
			file.delete();
			remove(srcPath);
		}
		mParent.setProcess(false);
	}

	/**
	 * 根据图片路径和模式索引设置图片显示空间，解决处理过程中切换模式以后结果图片显示在当前模式View的情况
	 * 
	 * @param src
	 * @param pos
	 */
	private void setImage(String src, int pos) {
		Message msg = handler.obtainMessage(SETIMAGE);
		msg.obj = src;
		msg.arg1 = pos;
		handler.sendMessage(msg);
	}

	private void remove(String path) {
		Message message = handler.obtainMessage(REMOVECACCHE);
		message.obj = path;
		handler.sendMessage(message);
	}

	private void scroll() {
		int len = 0;
		for (int i = 0; i < current; ++i) {
			len += titleLayout.getChildAt(i).getWidth();
		}
		scrollView.smoothScrollTo(len, 0);
	}

	private final static int SETIMAGE = 1;
	private final static int REMOVECACCHE = 2;

	/**
	 * leak memory
	 */
	private static class CustomHandler extends Handler {

		WeakReference<MainFragement> activity = null;

		public CustomHandler(MainFragement context) {
			activity = new WeakReference<MainFragement>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			String src = msg.obj.toString();
			switch (msg.what) {
			case SETIMAGE:
				int pos = msg.arg1;
				MainFragement tp = activity.get();
				MagicImageView imageView = tp.modelViews.get(pos).imageView;
				int res = tp.modelViews.get(pos).model.getResId();
				ImageManager.from(tp.mParent).displayImage(imageView, src, res,
						Source.SDCARD);
				break;
			case REMOVECACCHE:
				ImageManager.from(activity.get().mParent).remove(src);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	/**
	 * 获取actionbar的像素高度，默认使用android官方兼容包做actionbar兼容
	 * 
	 * @return
	 */
	private int getActionBarHeight() {

		int actionBarHeight = mParent.getActionBar().getHeight();
		// if (actionBarHeight != 0) {
		return actionBarHeight;
		// }
	}
}
