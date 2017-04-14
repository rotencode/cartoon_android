package com.songtzu.cartoon.v;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.c.DeleteImp;
import com.songtzu.cartoon.u.UiUtil;

/**
 * �����˶��ĶԻ���
 * @author Administrator
 * @date 2015-01-06
 */
public class RemindDialog extends Dialog implements
		View.OnClickListener {

	private TextView titleTv;
	private TextView okTv;
	private DeleteImp deleteImp;
	int w;
	public RemindDialog(Activity context) {
		super(context, R.style.transparentFrameWindowStyle);
		w=UiUtil.getScreenWidth(context);
		init();
	}
	
	public void setCallback(DeleteImp imp){
		deleteImp=imp;
	}

	/**
	 * ��ʼ��
	 * @return void
	 */
	private void init() {
		View contentView = View.inflate(getContext(), R.layout.dialog_remind,
				null);
		setContentView(contentView);
		Window window = getWindow();
//		window.setWindowAnimations(R.style.bottom2up);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = w*2/3;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity=Gravity.CENTER;
		//
		contentView.setOnClickListener(this);

		onWindowAttributesChanged(wl);
		setCanceledOnTouchOutside(true);

		okTv=(TextView)findViewById(R.id.dialog_remindOkTv);
		okTv.setOnClickListener(this);
		
		titleTv=(TextView)findViewById(R.id.dialog_remindTitleTv);
	}

	public void setTitle(String res){
		titleTv.setText(res);
	}
	
	public void setTitle(int res){
		titleTv.setText(res);
	}
	
	public void setOk(String res){
		okTv.setText(res);
	}
	
	public void setOk(int res){
		okTv.setText(res);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.dialog_remindOkTv:
			deleteImp.delete();
			break;
		default:
			break;
		}
		dismiss();
	}
}
