package com.songtzu.cartoon.v;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.songtzu.cartoon.u.UiUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {

	private int mViewHeight, mViewWidth;
	/**
	 * 适应屏幕时，图片的宽高
	 */
	private float mMatchedImageWidth, mMatchedImageHeight;
	private static int SCREEN_WIDTH = 0, SCREEN_HEIGHT = 0;
	Matrix mMatrix;

	public ScaleImageView(Activity context) {
		super(context);
		init(context);
	}

	private void init(Activity context) {
		setScaleType(ScaleType.MATRIX);

		SCREEN_WIDTH = UiUtil.getScreenWidth(context);
		SCREEN_HEIGHT = UiUtil.getScreenHeight(context);
		mMatrix = new Matrix();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		int screenWidth = 0, screenHeight = 0;
		if (bm.getWidth() > bm.getHeight()) {
			screenWidth = SCREEN_HEIGHT;
			screenHeight = SCREEN_WIDTH;
		} else {
			screenWidth = SCREEN_WIDTH;
			screenHeight = SCREEN_HEIGHT;
		}
		while (bm.getWidth() > screenWidth * 2
				|| bm.getHeight() > screenHeight * 2) {
			bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2,
					bm.getHeight() / 2, false);
		}
		super.setImageBitmap(bm);
		fitImageToView();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		// InputStream is = getContext().getResources().openRawResource(resId);
		// setInputStream(is);
	}

	public void setInputStream(InputStream is) {
		if (is == null) {
			setImageBitmap(null);
			return;
		}
		Bitmap bmp = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 1;
		while (true) {
			try {
				bmp = BitmapFactory.decodeStream(is, null, options);
				break;
			} catch (OutOfMemoryError e) {
				options.inSampleSize++;
			}
		}
		setImageBitmap(bmp);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		fitImageToView();
	}

	public void setByte(byte[] data) {
		if (data == null || data.length == 0) {
			setInputStream(null);
			return;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		setInputStream(is);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
		mViewHeight = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(mViewWidth, mViewHeight);

		fitImageToView();
	}

	// private void fitImageToView() {
	//
	// Drawable drawable = getDrawable();
	// int drawableWidth = drawable.getIntrinsicWidth();
	// int drawableHeight = drawable.getIntrinsicHeight();
	// float scale1 = (float) mViewWidth / drawableWidth;
	// float scale2 = (float) mViewHeight / drawableHeight;
	// float scale = Math.max(scale1, scale2);
	// Matrix mMatrix = new Matrix();
	// mMatrix.setScale(scale, scale);
	// mMatchedImageWidth = drawableWidth * scale;
	// mMatchedImageHeight = drawableHeight * scale;
	// mMatrix.postTranslate((mViewWidth - mMatchedImageWidth) / 2,
	// (mViewHeight - mMatchedImageHeight) / 2);
	//
	// setImageMatrix(mMatrix);
	// }
	public void fitImageToView() {
		if (mMatrix == null) {
			return;
		}
		
		Drawable drawable = getDrawable();
		if(drawable==null){
			return;
		}
		
		if (mViewHeight == 0) {
			mViewHeight = getHeight();
		}
		if (mViewWidth == 0) {
			mViewWidth = getWidth();
		}
		
		mMatrix.reset();
		
		int drawableWidth = drawable.getIntrinsicWidth();
		int drawableHeight = drawable.getIntrinsicHeight();
		float scale1 = (float) mViewWidth / drawableWidth;
		float scale2 = (float) mViewHeight / drawableHeight;
		float scale = Math.min(scale1, scale2);
		mMatrix.setScale(scale, scale);
		mMatchedImageWidth = drawableWidth * scale;
		mMatchedImageHeight = drawableHeight * scale;
		mMatrix.postTranslate((mViewWidth - mMatchedImageWidth) / 2,
				(mViewHeight - mMatchedImageHeight) / 2);

		setImageMatrix(mMatrix);

	}
	// private void fitImageToView() {
	// if(mViewHeight==0||mViewWidth==0){
	// mViewHeight=getHeight();
	// mViewWidth=getWidth();
	// }
	// Drawable drawable = getDrawable();
	// Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
	// int w = bitmap.getWidth();
	// int h = bitmap.getHeight();
	// // bitmap.recycle();
	// boolean wl=w>h;
	// int r=wl?w:h;
	//
	// float scale1 = (float) mViewWidth / w;
	// float scale2 = (float) mViewHeight / h;
	// float scale = Math.min(scale1, scale2);
	// if(wl){
	// scale=(float) mViewWidth / r;
	// }else {
	// scale=(float) mViewHeight / r;
	// }
	// Matrix mMatrix = new Matrix();
	// mMatrix.setScale(scale, scale);
	// mMatchedImageWidth = w * scale;
	// mMatchedImageHeight = h * scale;
	//
	// // StringBuilder sb=new StringBuilder();
	// // sb.append("w is ").append(w).append("\n");
	// // sb.append("h is ").append(h).append("\n");
	// // sb.append("r is ").append(r).append("\n");
	// // sb.append("mViewWidth is ").append(mViewWidth).append("\n");
	// // sb.append("mViewHeight is ").append(mViewHeight).append("\n");
	// // sb.append("scale is ").append(scale).append("\n");
	// //
	// sb.append("mMatchedImageWidth is ").append(mMatchedImageWidth).append("\n");
	// //
	// sb.append("mMatchedImageHeight is ").append(mMatchedImageHeight).append("\n");
	// // Util.write(sb.toString());
	// mMatrix.postTranslate((mViewWidth - mMatchedImageWidth) / 2,
	// (mViewHeight - mMatchedImageHeight) / 2);
	// setImageMatrix(mMatrix);
	// }
}
