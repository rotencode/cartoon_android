package com.songtzu.cartoon;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.songtzu.cartoon.u.UiUtil;
import com.umeng.fb.ConversationActivity;

public class FeedbackActivity extends ConversationActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		getActionBar().setDisplayShowHomeEnabled(false);//隐藏App Icon

//		UiUtil.renderNotificationBar(this);//渲染通知栏背景色
 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(getResources().getColor(R.color.TitleBar));

		//底部导航栏
		//window.setNavigationBarColor(activity.getResources().getColor(colorResId));
	}
	}
}
