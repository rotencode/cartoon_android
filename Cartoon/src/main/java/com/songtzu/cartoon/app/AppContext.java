package com.songtzu.cartoon.app;

import android.app.Application;

public class AppContext extends Application {
	private static AppContext instance;

	@Override
	public void onCreate() {
		super.onCreate();

		CrashHandler.getInstance().init(getApplicationContext());
		
		instance = this;
	}

	public static AppContext getInstance() {
		return instance;
	}
}
