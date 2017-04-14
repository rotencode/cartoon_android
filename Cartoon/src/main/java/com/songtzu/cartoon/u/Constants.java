package com.songtzu.cartoon.u;

public final class Constants {
	
	private Constants(){}
	
	public enum NetworkType{
		NONE,WIFI,MOBILE;
	}
	
	public enum Source{
		NONE,SDCARD,WEB;
	}
	
	public static int ACTIONBAR_HEIGHT;

	public static final byte RESOURCE_PHOTOSTORE = 101;
	public static final byte RESOURCE_CARMERA = 102;
	public static final byte RESOURCE_BITMAPCUT=103;
	public final static byte RESOURCE_SETTING=104;
	
	public final static int MAX=3000;
	public final static int MIN=1000;
	public final static int AUTO=1000;
	
	public static final String PATH_ROUTE="http://www.songtzu.com";
	public static final String PATH_URL=PATH_ROUTE+"/api?";
	
	public static final int STATE_OK=100;

	public static final String MAXSIZE="prf_maxsize";
	public static final String SIZE="prf_size";
	
	
	public static final String ABOUT="com.songtzu.cartoon.about";
	public static final String OPT_GUIDE="com.songtzu.cartoon.guid";
	public static final String HISTORY="com.songtzu.cartoon.history";
	public static final String SETTING="com.songtzu.cartoon.setting";
	public static final String FEEDBACK="com.songtzu.cartoon.feedback";
	public static final String PROTOCOL="com.songtzu.cartoon.protocol";
}
