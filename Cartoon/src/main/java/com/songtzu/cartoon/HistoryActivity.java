package com.songtzu.cartoon;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.c.DeleteImp;
import com.songtzu.cartoon.e.SingleImageModel;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.SDCardUtil;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.Util;
import com.songtzu.cartoon.u.Constants.Source;
import com.songtzu.cartoon.u.image.ImageManager;
import com.songtzu.cartoon.u.image.MD5;
import com.songtzu.cartoon.v.RemindDialog;

/**
 * @author: zzp
 * @since: 2015-06-10 Description: 仿微信选取手机所有图片，或者拍照界面
 */
public class HistoryActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, OnItemLongClickListener, DeleteImp {

	/** 按时间排序的所有图片list */
	private ArrayList<SingleImageModel> allImages;
	/** 按目录排序的所有图片list */

	private MyHandler handler;

	private View noDataTv;

	private GridView gridView = null;
	private LayoutInflater inflater = null;
	private GridViewAdapter adapter;
	/** 每张图片需要显示的高度和宽度 */
	private int perWidth;

	public static final int CODE_FOR_PIC_BIG = 1;
	private int count = 0;

	private RemindDialog dialog;

	private enum State {
		LONG_CLICK, CLICK, NONE;
	}

	private State state = State.NONE;

	protected void initView() {
		gridView = (GridView) findViewById(R.id.gv_content);
		gridView.setOnItemLongClickListener(this);
		gridView.setOnItemClickListener(this);

		noDataTv = findViewById(R.id.noDataTv);
		noDataTv.setVisibility(View.GONE);
		noDataTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	protected void initData() {
		inflater = LayoutInflater.from(this);

		allImages = new ArrayList<SingleImageModel>();

		handler = new MyHandler(this);
		adapter = new GridViewAdapter();
		getAllImages();
		// 计算每张图片应该显示的宽度
		perWidth = (UiUtil.getScreenWidth(this) - UiUtil.dip2px(this, 4)) / 2;
	}

	/**
	 * 从手机中获取所有的手机图片
	 */
	private void getAllImages() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File dir = new File(SDCardUtil.SONGTZU_IMAGE);
				if (dir == null || !dir.exists() || !dir.isDirectory()) {
					return;
				}

				// ArrayList<Model> models = UiUtil
				// .getModels(HistoryActivity.this);
				// ArrayList<String> style = new ArrayList<String>(
				// models.size() + 1);
				// style.add("songtzu");
				// for (Model model : models) {
				// style.add(model.getName());
				// }
				String[] fs = dir.list();
				int fileNameLen;
				for (String fileName : fs) {
					int i = fileName.lastIndexOf(".");
					String name = fileName.substring(0, i);// 源文件名称，不考虑后缀
					/******* 验证文件合法性 ********/
					fileNameLen = name.length();
					if (fileNameLen <= 2) {
						continue;
					}
					String len = name.substring(fileNameLen - 2, fileNameLen);// 取到文件名长度
					try {
						int length = Integer.parseInt(len);
						if (fileNameLen <= length + 2) {
							continue;
						}
						String md5 = name
								.substring(0, fileNameLen - 2 - length);
						String source = name.substring(
								fileNameLen - 2 - length, fileNameLen - 2);
						String md5s = MD5.Md5(source);

						if (Util.DEBUG) {
							Util.write("name is " + name);
							Util.write("md5 is " + md5);
							Util.write("source is " + source);
							Util.write("md5s is " + md5s);
						}
						if (md5.equals(md5s)) {
							SingleImageModel singleImageModel = new SingleImageModel();
							singleImageModel.setPath(SDCardUtil.SONGTZU_IMAGE
									+ fileName);
							allImages.add(singleImageModel);
						}
					} catch (Exception e) {
						continue;
					}
				}
				// for (String t : fs) {
				// String t1 = t.substring(0, t.lastIndexOf("."));
				// if (style.contains(t1)) {
				// if (Util.DEBUG)
				// Util.write("remove file name is " + t1);
				// } else {
				// if (Util.DEBUG)
				// Util.write("add file name is " + t1);
				// SingleImageModel singleImageModel = new SingleImageModel();
				// singleImageModel.setPath(SDCardUtil.SONGTZU_IMAGE + t);
				// allImages.add(singleImageModel);
				// }
				// }
				// models.clear();
				// models = null;
				// style.clear();
				// style = null;
				fs = null;
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long l) {
		if (state == State.LONG_CLICK) {
			onSelected(position);
		} else {
			ArrayList<String> list = new ArrayList<String>(allImages.size());
			for (SingleImageModel model : allImages) {
				list.add(model.getPath());
			}
			Intent intent = new Intent();
			intent.setClass(HistoryActivity.this, DetailActivity.class);
			// TODO
			intent.putStringArrayListExtra(DetailActivity.EXTRA_DATA, list);
			intent.putExtra(DetailActivity.EXTRA_CURRENT_PIC, position);
			intent.putExtra(DetailActivity.EXTRA_WIDTH, perWidth);
			startActivityForResult(intent, CODE_FOR_PIC_BIG);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		onSelected(arg2);
		return true;
	}

	private void onSelected(int pos) {
		boolean isPicked = allImages.get(pos).isPicked();
		if (isPicked) {
			--count;
		} else {
			++count;
		}
		allImages.get(pos).setPicked(!isPicked);
		handler.sendEmptyMessage(0);
	}

	/**
	 * leak memory
	 */
	private static class MyHandler extends Handler {

		WeakReference<HistoryActivity> activity = null;

		public MyHandler(HistoryActivity context) {
			activity = new WeakReference<HistoryActivity>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			HistoryActivity tp = activity.get();
			if (tp == null)
				return;
			if (tp.gridView.getAdapter() == null) {
				tp.gridView.setAdapter(tp.adapter);
			} else
				tp.adapter.notifyDataSetChanged();
			if (tp.adapter.getCount() == 0) {
				tp.noDataTv.setVisibility(View.VISIBLE);
			} else {
				tp.noDataTv.setVisibility(View.GONE);
			}
			if (tp.delItem != null) {
				tp.delItem.setVisible(tp.count > 0);
			}
			if (tp.couItem != null) {
				tp.couItem.setVisible(tp.count > 0);
			}

			if (tp.count > 0) {
				tp.state = State.LONG_CLICK;
				if (tp.couItem != null) {
					if (tp.adapter.getCount() == tp.count) {
						tp.couItem.setTitleCondensed("All");
					} else {
						tp.couItem.setTitleCondensed(String.valueOf(tp.count));
					}
				}
			} else {
				tp.state = State.NONE;
			}
			super.handleMessage(msg);
		}
	}

	/**
	 * gridview适配器
	 */
	private class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return allImages.size();
		}

		@Override
		public Object getItem(int i) {
			return allImages.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			final String path = allImages.get(i).getPath();
			if (view == null || view.getTag() == null) {
				view = inflater.inflate(R.layout.activity_history_item, null);
				GridViewHolder holder = new GridViewHolder();
				holder.iv_content = (ImageView) view
						.findViewById(R.id.iv_content);
				holder.v_gray_masking = view.findViewById(R.id.v_gray_masking);
				holder.iv_pick_or_not = (ImageView) view
						.findViewById(R.id.iv_pick_or_not);

				view.setTag(holder);
				// 要在这进行设置，在外面设置会导致第一个项点击效果异常
				view.setLayoutParams(new GridView.LayoutParams(perWidth,
						perWidth));
			}
			final GridViewHolder holder = (GridViewHolder) view.getTag();
			// 一定不要忘记更新position
			// 如果该图片被选中，则讲状态变为选中状态
			if (allImages.get(i).isPicked()) {
				holder.v_gray_masking.setVisibility(View.VISIBLE);
				holder.iv_pick_or_not.setVisibility(View.VISIBLE);
			} else {
				holder.v_gray_masking.setVisibility(View.GONE);
				holder.iv_pick_or_not.setVisibility(View.GONE);
			}
			ImageManager.from(HistoryActivity.this).displayImage(
					holder.iv_content, path, -1, perWidth, perWidth,
					Source.SDCARD);
			return view;
		}
	}

	private class GridViewHolder {
		public ImageView iv_content;
		public View v_gray_masking;
		public ImageView iv_pick_or_not;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (Util.DEBUG)
			Util.write("resultCode is " + resultCode);

		if (resultCode != Constants.STATE_OK || data == null) {
			return;
		}
		switch (requestCode) {
		case CODE_FOR_PIC_BIG:
			ArrayList<String> temp = data
					.getStringArrayListExtra(DetailActivity.EXTRA_DATA);
			if (temp == null) {
				return;
			}
			allImages.clear();
			for (String t : temp) {
				SingleImageModel model = new SingleImageModel();
				model.setPath(t);
				allImages.add(model);
			}
			handler.sendEmptyMessage(0);
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public int layout() {
		// TODO Auto-generated method stub
		return R.layout.activity_history;
	}

	@Override
	public void init() {
		initView();
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.history, menu);
		delItem = menu.findItem(R.id.action_delete);
		delItem.setVisible(false);

		couItem = menu.findItem(R.id.action_count);
		couItem.setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_delete:
			// 删除
			if (dialog == null) {
				dialog = new RemindDialog(this);
				dialog.setTitle(R.string.delete_title);
				dialog.setOk(R.string.delete_ok);
				dialog.setCallback(this);
			}
			dialog.show();
			return true;
		case R.id.action_count:
			if (count == allImages.size()) {
				count = 0;
				for (SingleImageModel model : allImages) {
					model.setPicked(false);
				}
			} else {
				count = allImages.size();
				for (SingleImageModel model : allImages) {
					model.setPicked(true);
				}
			}
			handler.sendEmptyMessage(0);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private MenuItem delItem;
	private MenuItem couItem;

	@Override
	public void delete() {
		if (Util.DEBUG)
			Util.write("count is " + count);
		ArrayList<SingleImageModel> tp = new ArrayList<SingleImageModel>();
		for (SingleImageModel model : allImages) {
			if (model.isPicked()) {
				tp.add(model);
			}
		}
		allImages.removeAll(tp);

		handler.sendEmptyMessage(0);

		for (SingleImageModel model : tp) {
			File file = new File(model.getPath());
			if (file != null && file.exists()) {
				// 移除缓存图片
				ImageManager.from(this).remove(model.getPath());
				ImageManager.from(this).remove(model.getPath(), perWidth,
						perWidth);
				file.delete();
			}
		}
		delItem.setVisible(false);
		count = 0;
	}
}
