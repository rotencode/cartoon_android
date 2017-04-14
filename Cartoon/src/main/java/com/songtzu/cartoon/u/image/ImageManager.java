package com.songtzu.cartoon.u.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.songtzu.cartoon.app.AppContext;
import com.songtzu.cartoon.u.UiUtil;
import com.songtzu.cartoon.u.Constants.Source;

public class ImageManager {
	private static ImageManager imageManager;
//	public LruCache<String, Bitmap> mMemoryCache;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 10MB
	private static final String DISK_CACHE_SUBDIR = "songtzu";
	public DiskLruCache mDiskCache;
	private static AppContext myapp;

	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();

	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();

	private Handler mImageLoaderHandler;

	private boolean mImageLoaderIdle = true;

	/** ����ͼƬ */
	private static final int MSG_REQUEST = 1;
	/** ͼƬ������� */
	private static final int MSG_REPLY = 2;
	/** ��ֹͼƬ�����߳� */
	private static final int MSG_STOP = 3;
	/** ���ͼƬ�Ǵ�������أ���Ӧ�ý��Զ���������ӻ��������Ӧ�ö��� */
	private boolean isFromNet = true;
	
	public static ImageManager from(Context context) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}
		
		if (myapp == null) {
			myapp = (AppContext) context.getApplicationContext();
		}

		if (imageManager == null) {
			imageManager = new ImageManager(context);
		}

		return imageManager;
	}

	/**
	 * ˽�й��캯������֤����ģʽ
	 * 
	 * @param context
	 */
	private ImageManager(Context context) {
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		memClass = memClass > 32 ? 32 : memClass;
		// ʹ�ÿ����ڴ��1/8��ΪͼƬ����
//		final int cacheSize = 1024 * 1024 * memClass / 8;

//		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//
//			protected int sizeOf(String key, Bitmap bitmap) {
//				return bitmap.getRowBytes() * bitmap.getHeight();
//			}
//
//		};

		File cacheDir = DiskLruCache
				.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
		mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);

	}

	/**
	 * ���ͼƬ��Ϣ
	 */
	class ImageRef {

		/** ͼƬ��ӦImageView�ؼ� */
		ImageView imageView;
		/** ͼƬURL��ַ */
		String url;
		/** ͼƬ����·�� */
//		String filePath;
		/** Ĭ��ͼ��ԴID */
		int resId;
		int width = 0;
		int height = 0;
		Source source = Source.NONE;

		/**
		 * ���캯��
		 * 
		 * @param imageView
		 * @param url
		 * @param resId
		 * @param filePath
		 */
		ImageRef(ImageView imageView, String url, int resId,
				Source source) {
			this.imageView = imageView;
			this.url = url;
			this.resId = resId;
			this.source = source;
		}

		ImageRef(ImageView imageView, String url, int resId,
				int width, int height, Source source) {
			this.imageView = imageView;
			this.url = url;
			this.resId = resId;
			this.width = width;
			this.height = height;
			this.source = source;
		}

	}

	/**
	 * ��ʾͼƬ
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 */
	public void displayImage(ImageView imageView, String url, int resId,
			Source source) {
		if (imageView == null) {
			return;
		}
		if (TextUtils.isEmpty(url)) {
			return;
		}
		if (imageView.getTag() != null
				&& imageView.getTag().toString().equals(url)) {
			return;
		}
		if (resId > 0) {
			imageView.setImageResource(resId);
		}
		url = url.replaceAll("\\\\", "/");
		imageView.setTag(url);

		Bitmap bitmap = mDiskCache.get(url);
		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
			return;
		}

		queueImage(new ImageRef(imageView, url, resId, source));
	}

	/**
	 * ��ʾͼƬ�̶���СͼƬ������ͼ��һ��������ʾ�б��ͼƬ�����Դ���С�ڴ�ʹ��
	 * 
	 * @param imageView
	 *            ����ͼƬ�Ŀؼ�
	 * @param url
	 *            ���ص�ַ
	 * @param resId
	 *            Ĭ��ͼƬ
	 * @param width
	 *            ָ�����
	 * @param height
	 *            ָ���߶�
	 */
	public void displayImage(ImageView imageView, String url, int resId,
			int width, int height, Source source) {
		if (imageView == null) {
			return;
		}
		if (TextUtils.isEmpty(url)) {
			return;
		}
		if (imageView.getTag() != null
				&& imageView.getTag().toString().equals(url)) {
			return;
		}
		if (resId > 0) {
			imageView.setImageResource(resId);
		}
		url = url.replaceAll("\\\\", "/");
		imageView.setTag(url);
		
		Bitmap bitmap = mDiskCache.get(url + width + height);
		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
			return;
		}
		
		queueImage(new ImageRef(imageView, url, resId, width, height,
				source));
	}

	/**
	 * ��ӣ�����ȳ�
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {
		// ɾ������ImageView
		Iterator<ImageRef> iterator = mImageQueue.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().imageView == imageRef.imageView) {
				iterator.remove();
			}
		}
		// �������
		mImageQueue.push(imageRef);
		sendRequest();
	}

	/**
	 * ��������
	 */
	private void sendRequest() {
		// ����ͼƬ�����߳�
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(
					imageLoader.getLooper());
		}

		// ��������
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST,
					imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}

	/**
	 * ͼƬ�����߳�
	 */
	class ImageLoaderHandler extends Handler {

		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			if (msg == null)
				return;

			switch (msg.what) {

			case MSG_REQUEST:
				if (msg.obj != null && msg.obj instanceof ImageRef) {
					ImageRef ref = (ImageRef) msg.obj;
					String path = ref.url;
					int width = ref.width;
					int height = ref.height;

					Bitmap bitmap = null;
					// 杩斿洖澶у浘,灞忓箷瀹藉害涓哄噯
					if (width == 0 || height == 0) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(path, options);
						int size = computeScale(options,
								UiUtil.getScreenWidth(),
								UiUtil.getScreenHeight());
						options.inSampleSize = size;
						options.inJustDecodeBounds = false;
						try {
							bitmap = BitmapFactory.decodeFile(path, options);
						} catch (OutOfMemoryError e) {
							releaseAllSizeCache();
							bitmap = BitmapFactory.decodeFile(path, options);
						}
					} else {
//						String hash = UiUtil.md5(path);
						File file = new File(UiUtil.getDataPath());
						if (!file.exists())
							file.mkdirs();
						// 涓存椂鏂囦欢鐨勬枃浠跺悕
						String tempPath = UiUtil.getDataPath() + path + ".temp";
						File picFile = new File(path);
						File tempFile = new File(tempPath);
						// 濡傛灉璇ユ枃浠跺瓨鍦�,骞朵笖temp鏂囦欢鐨勫垱寤烘椂闂磋鍘熸枃浠朵箣鍚�
						if (tempFile.exists()
								&& (picFile.lastModified() <= tempFile
										.lastModified()))
							bitmap = BitmapFactory.decodeFile(tempPath);
						// 鏃犳硶鍦ㄤ复鏃舵枃浠剁殑缂╃暐鍥剧洰褰曟壘鍒拌鍥剧墖锛屼簬鏄墽琛岀浜屾
						if (bitmap == null) {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inJustDecodeBounds = true;
							BitmapFactory.decodeFile(path, options);
							options.inSampleSize = computeScale(options, width,
									height);
							options.inJustDecodeBounds = false;
							try {
								bitmap = BitmapFactory
										.decodeFile(path, options);
							} catch (OutOfMemoryError error) {
								bitmap = null;
							}
							if (bitmap != null) {
								bitmap = centerSquareScaleBitmap(
										bitmap,
										((bitmap.getWidth() > bitmap
												.getHeight()) ? bitmap
												.getHeight() : bitmap
												.getWidth()));
							}
							// 绗笁姝�,濡傛灉缂╂斁姣斾緥澶т簬4锛岃鍥剧殑鍔犺浇浼氶潪甯告參锛屾墍浠ュ皢璇ュ浘淇濆瓨鍒颁复鏃剁洰褰曚笅浠ヤ究涓嬫鐨勫揩閫熷姞杞�
							if (options.inSampleSize >= 4 && bitmap != null) {
								try {
									file = new File(tempPath);
									if (!file.exists())
										file.createNewFile();
									else {
										file.delete();
										file.createNewFile();
									}
									FileOutputStream fos = new FileOutputStream(
											file);
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									bitmap.compress(Bitmap.CompressFormat.PNG,
											100, baos);
									fos.write(baos.toByteArray());
									fos.flush();
									fos.close();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							// }
						} else {
							if (bitmap != null) {
								bitmap = centerSquareScaleBitmap(
										bitmap,
										((bitmap.getWidth() > bitmap
												.getHeight()) ? bitmap
												.getHeight() : bitmap
												.getWidth()));
							}
						}
					}
					if (mImageManagerHandler != null && bitmap != null) {
						Message message = mImageManagerHandler.obtainMessage(
								MSG_REPLY, bitmap);
						mImageManagerHandler.sendMessage(message);
					} else {
						idleThread();
					}
				} else {
					idleThread();
				}
				break;

			case MSG_STOP:
				Looper.myLooper().quit();
				break;
			}
		}
	}

	/** UI�߳���Ϣ������ */
	@SuppressLint("HandlerLeak")
	private Handler mImageManagerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
				case MSG_REPLY: // �յ�Ӧ��
					do {
						ImageRef imageRef = mRequestQueue.remove();
						if (imageRef == null)
							break;
						if (imageRef.imageView == null
								|| imageRef.imageView.getTag() == null
								|| imageRef.url == null)
							break;
						if (!(msg.obj instanceof Bitmap) || msg.obj == null) {
							break;
						}
						Bitmap bitmap = (Bitmap) msg.obj;
						// ��ͬһImageView
						if (!(imageRef.url).equals((String) imageRef.imageView
								.getTag())) {
							break;
						}
						int w = imageRef.width, h = imageRef.height;
						if (w != 0 && h != 0) {
							mDiskCache.put(imageRef.url + w + h, bitmap);
//							mMemoryCache.put(imageRef.url + w + h, bitmap);
						} else {
							mDiskCache.put(imageRef.url, bitmap);
//							mMemoryCache.put(imageRef.url, bitmap);
						}
						setImageBitmap(imageRef.imageView, bitmap, isFromNet);
						isFromNet = false;

					} while (false);
					break;
				}
			}
			// �������ñ�־
			mImageLoaderIdle = true;

			// ������δ�رգ�������һ������
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};
	

	/**
	 * ���ͼƬ��ʾ���ֶ���
	 * 
	 */
	private void setImageBitmap(ImageView imageView, Bitmap bitmap,
			boolean isTran) {
		imageView.setImageBitmap(bitmap);
		imageView.setVisibility(View.VISIBLE);
	}

	/**
	 * ����url���ɻ����ļ�����·����
	 * 
	 * @param url
	 * @return
	 */
	public String urlToFilePath(String url) {

		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		StringBuilder filePath = new StringBuilder();
		filePath.append(myapp.getCacheDir().toString()).append('/');
		filePath.append(MD5.Md5(url)).append(url.substring(index));
		return filePath.toString();
	}

	public void stop() {
		mImageQueue.clear();
	}

	private void idleThread() {
		if (mImageManagerHandler != null) {
			mImageManagerHandler.sendEmptyMessage(MSG_REPLY);
		}
	}

	public void remove(String url,int w,int h){
		url = url.replaceAll("\\\\", "/");
		mDiskCache.remove(url+w+h);
	}
	public void remove(String url){
		url = url.replaceAll("\\\\", "/");
		mDiskCache.remove(url);
	}
	private int computeScale(BitmapFactory.Options options, int width,
			int height) {
		if (options == null)
			return 1;
		int widthScale = (int) ((float) options.outWidth / (float) width);
		int heightScale = (int) ((float) options.outHeight / (float) height);
		// 閫夋嫨缂╂斁姣斾緥杈冨ぇ鐨勯偅涓�
		int scale = (widthScale > heightScale ? widthScale : heightScale);
		if (scale < 1)
			scale = 1;
		return scale;
	}

	/**
	 * 閲婃斁鎵�鏈夌殑鍐呭瓨
	 */
	public void releaseAllSizeCache() {
		mDiskCache.clearCache();
	}

	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
		if (null == bitmap || edgeLength <= 0) {
			return null;
		}
		Bitmap result = bitmap;
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();

		// 浠庡浘涓埅鍙栨涓棿鐨勬鏂瑰舰閮ㄥ垎銆�
		int xTopLeft = (widthOrg - edgeLength) / 2;
		int yTopLeft = (heightOrg - edgeLength) / 2;

		if (xTopLeft == 0 && yTopLeft == 0)
			return result;

		try {
			result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft,
					edgeLength, edgeLength);
			bitmap.recycle();
		} catch (OutOfMemoryError e) {
			return result;
		}

		return result;
	}
}
