package com.songtzu.cartoon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.m.MainFrameActivity;
import com.songtzu.cartoon.u.UiUtil;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends Activity implements OnPageChangeListener,
		OnClickListener {
	private ViewPager viewPager;
	private LinearLayout points;
	private Button beginBtn;

	private List<View> imageViewList;
	private int previousSelectPosition = 0;

//	private final static int[] res = { R.drawable.guide_1, R.drawable.guide_2 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 取消标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_guid);
		init();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	public void init() {
		imageViewList = new ArrayList<View>(3);
		viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		points = (LinearLayout) findViewById(R.id.guide_points);
		beginBtn = (Button) findViewById(R.id.guide_begin);
		beginBtn.setOnClickListener(this);
		ImageView view;
		// for (int i = 0; i < res.length; i++) {

		// 添加点view对象
		for(int i=0;i<3;){
			view = new ImageView(this);
//			view.setImageResource(R.drawable.point_background);
			LayoutParams lp = new LayoutParams(20, 20);
			lp.leftMargin = 10;
			view.setLayoutParams(lp);
			view.setEnabled(false);
			points.addView(view);
			++i;
		}
		
		ImageView iv = new ImageView(this);
		iv.setScaleType(ScaleType.CENTER);
		String zh=UiUtil.getLanguage(this);
		if(zh.equals("zh")){
			iv.setImageResource(R.mipmap.guid_1);
		}else {
			iv.setImageResource(R.mipmap.guid_1_en);
		}
		imageViewList.add(iv);
		
		LayoutInflater li = getLayoutInflater();
		View g2 = li.inflate(R.layout.activity_guid_2, null);
		View g3 = li.inflate(R.layout.activity_guid_3, null);

		imageViewList.add(g2);
		imageViewList.add(g3);

		ViewPagerAdapter adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
		points.getChildAt(previousSelectPosition).setEnabled(true);
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
		// 切换选中的点
		points.getChildAt(previousSelectPosition).setEnabled(false); // 把前一个点置为normal状态
		points.getChildAt(position % imageViewList.size()).setEnabled(true); // 把当前选中的position对应的点置为enabled状态
		previousSelectPosition = position % imageViewList.size();
		if (position == imageViewList.size() - 1) {
			beginBtn.setVisibility(ViewPager.VISIBLE);
		} else {
			beginBtn.setVisibility(ViewPager.GONE);
		}
	}

	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViewList.size();
		}

		/**
		 * 判断出去的view是否等于进来的view 如果为true直接复用
		 */
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/**
		 * 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来就是position
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageViewList.get(position
					% imageViewList.size()));
		}

		/**
		 * 创建一个view
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container
					.addView(imageViewList.get(position % imageViewList.size()));
			return imageViewList.get(position % imageViewList.size());
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, MainFrameActivity.class);
		startActivity(intent);
		finish();
	}
}
