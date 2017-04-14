package com.songtzu.cartoon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.songtzu.cartoon.m.MainFrameActivity;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.Util;
import com.songtzu.cartoon.u.image.MD5;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity implements AnimationListener {
	private RelativeLayout imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_welcome);

		imageView = (RelativeLayout) findViewById(R.id.welcome_aLl);

		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		imageView.startAnimation(aa);
		aa.setAnimationListener(this);
		// 谢松的代码，勿注释。
		// isRawDataExists(WelcomeActivity.this);
		copyResource2Cache();
		// MobclickAgent.updateOnlineConfig(this);
		// AnalyticsConfig.enableEncrypt(true);
		//
		// FeedbackAgent agent = new FeedbackAgent(this);
		// agent.sync();
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType. E_UM_NORMAL);
		if (Util.DEBUG)
		Util.write("density is " + UiUtil.getScreenDensity(this));
		
//		MD5.test("this is the test for md5");
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Constants.ACTIONBAR_HEIGHT = UiUtil.getStatusBarHeight(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * transform to mainactivity
	 * 
	 * @return void
	 */
	private void redirectTo() {
		// SharedPreferences sp = getSharedPreferences(Constants.FIRST, 0);
		// boolean s = sp.getBoolean(Constants.FIRST, false);
		Intent intent = new Intent();
		// if (s) {
		intent.setClass(this, MainFrameActivity.class);
		// } else {
		// sp.edit().putBoolean(Constants.FIRST, true).commit();
		// intent.setClass(this, GuideActivity.class);
		// }
		startActivity(intent);
		finish();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		imageView.clearAnimation();
		redirectTo();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	/**
	 * 拷贝stasm数据库到文件系统中。
	 */
	private void isRawDataExists(Context context) {
		try {
			File internalDir = context.getDir("stasm", Context.MODE_PRIVATE);
			File frontalface_xml = new File(internalDir,
					"haarcascade_frontalface_alt2.xml");
			// File frontalface_xml = new File(internalDir,
			// "lbpcascade_frontalface.xml");
			File lefteye_xml = new File(internalDir,
					"haarcascade_mcs_lefteye.xml");
			File righteye_xml = new File(internalDir,
					"haarcascade_mcs_righteye.xml");
			File mounth_xml = new File(internalDir,
					"haarcascade_mcs_mounth.xml");

			if (frontalface_xml.exists() && lefteye_xml.exists()
					&& righteye_xml.exists() && mounth_xml.exists()) {

				System.out.println("RawDataExists");
			} else {
				Resources res = getResources();
				copyRawDataToInternal(
						res.openRawResource(R.raw.haarcascade_frontalface_alt2),
						frontalface_xml);
				copyRawDataToInternal(context,
						R.raw.haarcascade_frontalface_alt2, frontalface_xml);
				copyRawDataToInternal(context, R.raw.haarcascade_mcs_lefteye,
						lefteye_xml);
				copyRawDataToInternal(context, R.raw.haarcascade_mcs_righteye,
						righteye_xml);
				// copyRawDataToInternal(context, R.raw.haarcascade_mcs_mouth,
				// mounth_xml);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 谢松的代码，勿注释。
	private void copyRawDataToInternal(Context context, int id, File file) {
		System.out.println("copyRawDataToInternal: " + file.toString());

		try {
			InputStream is = context.getResources().openRawResource(id);
			FileOutputStream fos = new FileOutputStream(file);

			int data;
			byte[] buffer = new byte[4096];
			while ((data = is.read(buffer)) != -1) {
				fos.write(buffer, 0, data);
			}
			is.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("copyRawDataToInternal done");
	}

	public void copyResource2Cache() {
		File internalDir = getDir("stasm", Context.MODE_PRIVATE);
		try {
			AssetManager manager = getAssets();
			String[] res = getAssets().list("resource");
			for (String s : res) {
				File file = new File(internalDir, s);
				if (file.exists()) {
					file = null;
					continue;
				}
				InputStream is = manager.open("resource/" + s);
				if (is != null) {
					if (Util.DEBUG)
					Util.write("copy " + s + " to cache");
					copyRawDataToInternal(is, file);
					is.close();
				}
				file = null;
				is = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 谢松的代码，勿注释。
	private void copyRawDataToInternal(InputStream is, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);

			int data;
			byte[] buffer = new byte[4096];
			while ((data = is.read(buffer)) != -1) {
				fos.write(buffer, 0, data);
			}
			fos.flush();
			fos.close();
			fos = null;
			buffer = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
