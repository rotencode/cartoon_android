package com.songtzu.cartoon.m;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.Util;
import com.songtzu.cartoon.u.image.ImageUtil;
import com.songtzu.cartoon.v.ImageResourceDialog;
import com.umeng.analytics.MobclickAgent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainFrameActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	ImageResourceDialog dialog;
	private boolean processing = false;

	boolean isExit = false;

	MainFragement mainFragement;
	MenuFragement menuFragement;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main_f);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

//		UiUtil.renderNotificationBar(this);// 渲染通知栏背景色
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.TitleBar));

			//底部导航栏
			//window.setNavigationBarColor(activity.getResources().getColor(colorResId));
		}
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



	private static final int REQUEST_CODE = 1;
	private void requestMultiplePermissions() {
		String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(permissions, REQUEST_CODE);
		}
	}




	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE) {
			int grantResult = grantResults[0];
			boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
			Log.i("songtzucartoon", "onRequestPermissionsResult granted=" + granted);


			if (processing) {// 如果正在处理，等待处理完成
				Util.showMsg(this, R.string.processing);
			} else {
				if (dialog == null) {
					dialog = new ImageResourceDialog(this);
				}
				dialog.show();
			}
		}
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			break;
		case R.id.action_try:

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
					if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
						Toast.makeText(this, "Please grant the permission this time", Toast.LENGTH_LONG).show();
					}
					requestMultiplePermissions();
				} else {


					if (processing) {// 如果正在处理，等待处理完成
						Util.showMsg(this, R.string.processing);
					} else {
						if (dialog == null) {
							dialog = new ImageResourceDialog(this);
						}
						dialog.show();
					}
				}

			}else {	//低于23，默认赋予权限。


				if (processing) {// 如果正在处理，等待处理完成
					Util.showMsg(this, R.string.processing);
				} else {
					if (dialog == null) {
						dialog = new ImageResourceDialog(this);
					}
					dialog.show();
				}
			}

			break;
		case R.id.action_save:
			mainFragement.save();
			break;
		case R.id.action_share:
			mainFragement.share();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_BACK && !isExit) {
			isExit = true;
			Util.showMsg(this, R.string.exit);
			mDrawerLayout.postDelayed(new Runnable() {

				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
			return true;
		}
		return super.onKeyDown(keycode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.RESOURCE_PHOTOSTORE:
				String srcPath = ImageUtil.getPath(this, data.getData());
				if (Util.DEBUG)
				Util.write("src is " + srcPath);
				mainFragement.effect(srcPath, false);
				break;
			case Constants.RESOURCE_CARMERA:
				String path = dialog.getSrcPath();
				mainFragement.effect(path, true);
				break;
			case Constants.RESOURCE_SETTING:
				int size = data.getIntExtra("size", Constants.AUTO);
				if (Util.DEBUG)
				Util.write("result size is " + size);
				mainFragement.setSize(size);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void init() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
				GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,//R.mipmap.btn_menu,
				 R.string.drawer_open,
				R.string.drawer_close) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
			}
		};
//		mDrawerToggle.setHomeAsUpIndicator(R.mipmap.btn_menu);

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mainFragement = new MainFragement();
		menuFragement = new MenuFragement();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.main_container, mainFragement);
		ft.add(R.id.menu_container, menuFragement);
		ft.commit();
	}

	public void setProcess(boolean bool) {
		processing = bool;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				setProgressBarIndeterminateVisibility(processing);
			}
		});
	}
}
