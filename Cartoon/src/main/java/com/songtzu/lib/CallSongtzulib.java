package com.songtzu.lib;

import com.songtzu.cartoon.c.ImageImp;

public class CallSongtzulib {
	static {
		System.loadLibrary("opencv_java");
		System.loadLibrary("songtzulib");
	}
	
	private static ImageImp imageImp;
	
	/**
	 * 本地方法
	 * 
	 * @param srcPath
	 *            图片源路径
	 * @param dstPtah
	 *            目标图片路径
	 * @param model
	 *            模式
	 * @return
	 */
	public native int Effect(String srcPath, String dstPtah, int model);

	// 由JNI中的线程回调
	private static void processcallback(int i) {
		if(imageImp!=null)
			imageImp.handle(i);
	}
	
	public void setCallback(ImageImp imp){
		imageImp=imp;
	}

	public static CallSongtzulib getLib() {
		return CallSongtzulibHolder.libInstance;
	}

	private static class CallSongtzulibHolder {
		private static final CallSongtzulib libInstance = new CallSongtzulib();
	}
}
