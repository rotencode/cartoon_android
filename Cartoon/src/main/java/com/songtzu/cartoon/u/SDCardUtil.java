package com.songtzu.cartoon.u;

import java.io.File;

import com.songtzu.cartoon.app.AppContext;

import android.os.Environment;
import android.text.TextUtils;

/**
 * sdcard helper
 */
public final class SDCardUtil {

	private SDCardUtil() {
	}

	public static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static final String SONGTZU_IMAGE = SDCARD_PATH + "/songtzu/image/";
	public static final String SONGTZU_LOG = SDCARD_PATH + "/songtzu/log/";

	/**
	 * init application sdcard path
	 */
	static {
		if (isSDCardExists()) {
			File file = new File(SONGTZU_IMAGE);
			if (!file.exists()) {
				file.mkdirs();
			}
			File fileLog = new File(SONGTZU_LOG);
			if (!fileLog.exists()) {
				fileLog.mkdirs();
			}
		}
	}

	/**
	 * measure if sdcard is exists or not
	 * 
	 * @methodName: isSDCardExists
	 * @return true:sdcard is exists;false:sdcard is not exists
	 */
	public static boolean isSDCardExists() {
		if (TextUtils.isEmpty(SDCARD_PATH)) {
			return false;
		}
		return true;
	}/**
	 * 获取到log保存在sdcard的路径
	 * @return
	 */
	public static String getFilePath() {
		String file_dir = "";
		if (isSDCardExists()) {
			file_dir = SONGTZU_LOG;
		} else {
			// MyApplication.getInstance().getFilesDir()返回的路劲为/data/data/PACKAGE_NAME/files，其中的包就是我们建立的主Activity所在的包
			file_dir = AppContext.getInstance().getFilesDir()
					.getAbsolutePath()
					+ "/log/";
		}
		return file_dir;
	}
}
