package com.songtzu.cartoon.u.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.SDCardUtil;
import com.songtzu.cartoon.u.Util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * image helper class
 */
public class ImageUtil {
	/**
	 * convert bitmap to byte array
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * convert byte array to bitmap
	 * 
	 * @param bm
	 * @return
	 */
	public static Bitmap bytes2bimap(byte[] b) {
		if (b == null || b.length == 0) {
			return null;
		} else {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
	}

	/**
	 * 缩放图片
	 * 
	 * @param bm
	 *            图片资源
	 * @param newWidth
	 *            缩放宽
	 * @param newHeight
	 *            缩放高
	 * @return
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = (float) newHeight / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	/**
	 * get selected image path
	 * 
	 * @param activity
	 * @param uri
	 * @return
	 */
	public static String getFilePath(Activity activity, Intent d) {

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.getContentResolver().query(d.getData(), proj,
				null, null, null);
		// get selected image index
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * decode a bitmap from sdcard
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getImage(String path) {
//		try {
//			FileInputStream is = new FileInputStream(path);
//			BitmapFactory.Options opts = new BitmapFactory.Options();;
//			Bitmap bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
//			return bmp ;
//		}catch (Exception e){
//			return null;
//		}
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		return bitmap;
	}

	public static Bitmap getImage(String path, int maxSize) {

		File file = new File(path);
		if (file == null || !file.exists())
			return null;

		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;

		Bitmap bm = BitmapFactory.decodeFile(path, option);
		int w = option.outWidth, h = option.outHeight;
		int scale = 1;
		if (w > maxSize || h > maxSize) {
			scale = (w > h ? w : h) / maxSize + 1;
		}

		if (Util.DEBUG)
			Util.write("scale is " + scale + " size is " + maxSize);

		option.inSampleSize = scale;
		option.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, option);
		if (bm != null) {
			if (Util.DEBUG)
				Util.write("w is " + bm.getWidth() + " h is " + bm.getHeight());
		}
		return bm;
	}

	/**
	 * resize bitmap ,make the new bitmap width or height equils size and the
	 * other small than size
	 * 
	 * @param bm
	 *            source bm
	 * @param size
	 *            max target size
	 * @return
	 */
	public static Bitmap getImage(Bitmap bm, float size) {
		int w = bm.getWidth(), h = bm.getHeight();
		float r = w > h ? w : h;
		r = size / r;
		Matrix m = new Matrix();
		m.setScale(r, r);
		Bitmap b = Bitmap.createBitmap(bm, 0, 0, w, h, m, true);
		return b;
	}

	/**
	 * save bitmap to sdcard
	 * 
	 * @param a
	 * @param bm
	 * @param fileName
	 */
	public static String save2file(Activity a, String path, Bitmap bm,
			String fileName) {
		if (Util.DEBUG)
			Util.write("fileName is " + fileName + " size is " + bm.getWidth());
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		File f = new File(dir, fileName);
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fos;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			bm.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			return f.getAbsolutePath();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * save bitmap to sdcard
	 * 
	 * @param a
	 * @param bm
	 * @param fileName
	 */
	public static String save2file(Activity a, byte[] bm, String fileName) {
		// Util.write("fileName is " + fileName);
		File dir = new File(SDCardUtil.SONGTZU_IMAGE);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		File f = new File(dir, fileName);
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fos;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			fos.write(bm);
			fos.flush();
			fos.close();
			return f.getAbsolutePath();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static void save2file(String src) throws IOException {
		File dir = new File(SDCardUtil.SONGTZU_IMAGE);
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdirs();
		File file = new File(src);
		if (file == null || !file.exists())// 源文件不存在
			return;

		String fileName = file.getName();
		int i = fileName.lastIndexOf(".");

		String name = fileName.substring(0, i);// 源文件名称
		String sourceName = name + Util.getYYYYmmDDHHmmss();//
		String suffix = fileName.substring(i, fileName.length());// 源文件后缀
		String md5SourceName = MD5.Md5(sourceName);
		String dstFile = SDCardUtil.SONGTZU_IMAGE + md5SourceName + sourceName
				+ sourceName.length() + suffix;

		if (Util.DEBUG)
			Util.write("save is clicked ，the path is " + dstFile);
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(dstFile);
		byte[] data = new byte[1024];
		while (fis.read(data) != -1) {
			fos.write(data);
		}
		fos.flush();
		fos.close();
		fis.close();
		data = null;
		fis = null;
		fos = null;
	}

	/**
	 * save bitmap to sdcard
	 * 
	 * @param a
	 * @param bm
	 * @param fileName
	 */
	public static String save2file(Activity a, Bitmap bm, int q) {
		File dir = new File(SDCardUtil.SONGTZU_IMAGE);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		FileOutputStream fos;
		try {
			File file = new File(dir, System.currentTimeMillis() + ".jpg");
			file.createNewFile();
			fos = new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, q, fos);
			fos.flush();
			fos.close();
			return file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param context
	 * @param uri
	 */
	public static void startPhotoZoom(Activity context, Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
		if (Util.DEBUG)
			Util.write("开始裁剪");
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 500);
		intent.putExtra("outputY", 500);
		intent.putExtra("return-data", true);
		context.startActivityForResult(intent, Constants.RESOURCE_BITMAPCUT);
	}

	/**
	 * according to file length and network state to calculate save bitmap
	 * quality
	 * 
	 * @param m
	 * @return
	 */
	public static int getQuality(float m) {
		// 图片在1M到5M之间，计算图片质量
		// if (m > 5) {
		// return 50;
		// }
		if (m > 1f) {
			return (int) (90 - 80 * (m - 1) / m);
		} else {
			return (int) (m * 90);
		}
	}

	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
