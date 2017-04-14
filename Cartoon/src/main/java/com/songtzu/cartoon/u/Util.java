/**  
 * @description: TODO

 * @author Carrot

 * @date 2015-5-9 ����2:51:28
 */
package com.songtzu.cartoon.u;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

public final class Util {

	public final static String APP_NAME = "Cartoon";
	public final static boolean DEBUG = true;
	public final static Locale LOCALE = Locale.getDefault();

	private Util() {

	}

	public static void write(String m) {
		if (DEBUG)
			Log.e(APP_NAME, m);
	}

	public static void showMsg(Context c, String m) {
		ToastHelper.makeText(c, m);
//		Toast.makeText(c, m, Toast.LENGTH_SHORT).show();
	}

	public static void showMsg(Context c, int m) {
		ToastHelper.makeText(c, m);
//		Toast.makeText(c, m, Toast.LENGTH_SHORT).show();
	}

	public static String getYYYYmmDDHHmmss() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", LOCALE);
		String today = format.format(new Date());
		return today;
	}
}
