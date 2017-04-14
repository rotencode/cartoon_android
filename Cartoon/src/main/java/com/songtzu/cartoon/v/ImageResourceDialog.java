package com.songtzu.cartoon.v;

import java.io.File;

import com.songtzu.cartoon.R;
import com.songtzu.cartoon.u.Constants;
import com.songtzu.cartoon.u.SDCardUtil;
import com.songtzu.cartoon.u.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class ImageResourceDialog extends Dialog implements
		View.OnClickListener {
	private Activity context;

	private String srcPath;

	public String getSrcPath() {
		return srcPath;
	}

	public ImageResourceDialog(Activity context) {
		super(context, R.style.transparentFrameWindowStyle);
		this.context = context;
		init();
	}

	private void init() {
		View contentView = View.inflate(getContext(),
				R.layout.dialog_imageresource, null);
		setContentView(contentView);
		Window window = getWindow();
		window.setWindowAnimations(R.style.bottom2up);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.BOTTOM;

		onWindowAttributesChanged(wl);
		setCanceledOnTouchOutside(true);
		contentView.setOnClickListener(this);

		findViewById(R.id.dialog_imagesource_photostoreTv).setOnClickListener(
				this);
		findViewById(R.id.dialog_imagesource_cameraTv).setOnClickListener(this);
		findViewById(R.id.dialog_imagesource_cancelTv).setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.dialog_imagesource_photostoreTv:
			openPhotostore();
			break;
		case R.id.dialog_imagesource_cameraTv:
			openCamera();
			break;

		default:
			break;
		}
		dismiss();
	}

	/**
	 * 打开系统相册
	 */
	private void openPhotostore() {
		Intent intentFromGallery = new Intent();
		intentFromGallery.setType("image/*");
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		context.startActivityForResult(intentFromGallery,
				Constants.RESOURCE_PHOTOSTORE);
	}

	/**
	 * 打开照相机
	 */
	protected void openCamera() {

		srcPath = SDCardUtil.SONGTZU_IMAGE + System.currentTimeMillis()
				+ ".jpg";

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File file = new File(srcPath);
		if (file.exists()) {
			if (Util.DEBUG)
			Util.write("delete file " + srcPath);
			file.delete();
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		context.startActivityForResult(intent, Constants.RESOURCE_CARMERA);
	}
}
