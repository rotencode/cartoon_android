package com.songtzu.cartoon.u;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.FaceDetector;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.reflect.TypeToken;
import com.songtzu.cartoon.R;
import com.songtzu.cartoon.app.AppContext;
import com.songtzu.cartoon.e.Model;
import com.songtzu.cartoon.u.Constants.NetworkType;

public class UiUtil {

	/**
	 * 打开浏览器访问url
	 * 
	 * @param a
	 *            上下文
	 * @param p
	 *            浏览地址
	 */
	public static void loadUrl(Activity a, String p) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri content_url = Uri.parse(p);
		intent.setData(content_url);
		a.startActivity(intent);
	}

	/**
	 * get mobile language setting
	 * 
	 * @param a
	 * @return
	 */
	public static String getLanguage(Activity a) {
		return a.getResources().getConfiguration().locale.getLanguage();
	}

	/**
	 * 读取配置文件，获得模式信息
	 * 
	 * @param a
	 * @return
	 */
	public static ArrayList<Model> getModels(Activity a) {
		try {
			String fileName = getLanguage(a).equals("zh") ? "config.json"
					: "config-en.json";
			String r = getAsset(a, fileName);
			ArrayList<Model> ms = JsonUtil.getJsonUtil().fromJson(r,
					new TypeToken<List<Model>>() {
					}.getType());
			return ms;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAsset(Activity a, String fileName)
			throws IOException {
		String r = "";

		InputStream is = a.getAssets().open(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				Charset.forName("utf-8")));
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		is.close();
		r = sb.toString();

		return r;
	}

	/**
	 * 根据资源名称和类型获取到资源id
	 * 
	 * @param c
	 *            上下文对象
	 * @param resName
	 *            资源名称
	 * @return 资源id
	 */
	public static int getResouce(Context c, String resName) {
		int indentify = c.getResources().getIdentifier(
				c.getPackageName() + ":mipmap/" + resName, null, null);
		return indentify;
	}

	/**
	 * check faces count with a bitmap
	 * 
	 * @return
	 */
	public static int getFaceCount(Bitmap bm) {
		FaceDetector fd;
		FaceDetector.Face[] faces = new FaceDetector.Face[10];
		int count = 0;
		Matrix m = new Matrix();
		m.setScale(0.5f, 0.5f);

		Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
				bm.getHeight(), m, true);

		Bitmap b = bitmap.copy(Bitmap.Config.RGB_565, true);

		fd = new FaceDetector(b.getWidth(), b.getHeight(), 10);
		count = fd.findFaces(b, faces);
		b.recycle();
		b = null;

		bitmap.recycle();
		bitmap = null;

		return count;
	}

	/**
	 * make the other title bar button show while press a extend button
	 */
	public static void forceShowOverflowMenu(Context c) {
		try {
			ViewConfiguration config = ViewConfiguration.get(c);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * check network type
	 * 
	 * @param context
	 * @return
	 */
	public static NetworkType getNetworkType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo ni = cm.getActiveNetworkInfo();
		// mobile 3G Data Network
		State mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		// wifi
		State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		// //如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接

		if (wifi == State.CONNECTED) {
			return NetworkType.WIFI;
		} else if (mobile == State.CONNECTED) {
			return NetworkType.MOBILE;
		} else {
			return NetworkType.NONE;
		}
	}

//	public static void renderNotificationBar(Activity activity) {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(activity, true);
//			SystemBarTintManager tintManager = new SystemBarTintManager(
//					activity);
//			tintManager.setStatusBarTintEnabled(true);
//			tintManager.setStatusBarTintResource(R.color.TitleBar);
//		}
//	}

//	@TargetApi(19)
//	private static void setTranslucentStatus(Activity activity, boolean on) {
//		Window win = activity.getWindow();
//		WindowManager.LayoutParams winParams = win.getAttributes();
//		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//		if (on) {
//			winParams.flags |= bits;
//		} else {
//			winParams.flags &= ~bits;
//		}
//		win.setAttributes(winParams);
//	}

	public static void shareImg(Activity a, String imgPath) {
		File file = new File(imgPath);
		if (file == null || !file.exists())
			return;

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_SUBJECT,
				a.getString(R.string.action_share));
		intent.putExtra(Intent.EXTRA_TEXT,
				"I would like to share this with you...");
		a.startActivity(Intent.createChooser(intent,
				a.getString(R.string.app_name)));
	}

	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		return width;
	}

	@SuppressWarnings("deprecation")
	public static int getScreenWidth() {
		return ((WindowManager) (AppContext.getInstance()
				.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay()
				.getWidth();
	}

	@SuppressWarnings("deprecation")
	public static int getScreenHeight() {
		return ((WindowManager) (AppContext.getInstance()
				.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay()
				.getHeight();
	}

	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int height = metric.heightPixels; // 屏幕高度（像素）
		return height;
	}

	public static float getScreenDensity(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		float density = metric.density;
		return density;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 获取存储路径
	 */
	public static String getDataPath() {
		String path;
		if (SDCardUtil.isSDCardExists())
			path = Environment.getExternalStorageDirectory().getPath()
					+ "/songtzu";
		else
			path = AppContext.getInstance().getFilesDir().getPath();
		if (!path.endsWith("/"))
			path = path + "/" + "temp/";
		return path;
	}

	/**
	 * md5加密
	 */
	public static String md5(Object object) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	public static int getStatusBarHeight(Activity a) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			return a.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
			return 75;
		}
	}
}
