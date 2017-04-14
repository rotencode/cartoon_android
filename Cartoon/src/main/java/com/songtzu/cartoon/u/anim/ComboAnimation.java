// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 

package com.songtzu.cartoon.u.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

// Referenced classes of package com.meizu.media.common.animation:
//            AnimUtils

public class ComboAnimation extends Animation {

	private final float mAlpha[];
	private Camera mCamera;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDegree[];
	private int mHeight;
	private boolean mIsXMapped;
	private final boolean mIsYAxis;
	private boolean mIsYMapped;
	private boolean mIsZMapped;
	private final boolean mReverse;
	private final float mScale[];
	private final float mTranslateX[];
	private final float mTranslateY[];
	private final float mTranslateZ[];
	private int mWidth;

	public ComboAnimation(float af[], float af1[], float af2[], float af3[],
			float af4[], float f, float f1, boolean flag, boolean flag1) {
		this(af, af1, af2, null, af3, af4, f, f1, flag, flag1);
	}

	public ComboAnimation(float af[], float af1[], float af2[], float af3[],
			float af4[], float af5[], float f, float f1, boolean flag,
			boolean flag1) {
		mIsXMapped = false;
		mIsYMapped = false;
		mIsZMapped = false;
		mTranslateX = af;
		mTranslateY = af1;
		mTranslateZ = af2;
		mScale = af3;
		mAlpha = af4;
		mDegree = af5;
		mCenterX = f;
		mCenterY = f1;
		mReverse = flag1;
		mIsYAxis = flag;
	}

	protected void applyTransformation(float f, Transformation transformation) {
		if (mReverse)
			f = 1.0F - f;
		float f1 = AnimUtils.calculateValue(mTranslateX, f, 0.0F);
		if (!mIsXMapped)
			f1 *= mWidth;
		float f2 = AnimUtils.calculateValue(mTranslateY, f, 0.0F);
		if (!mIsYMapped)
			f2 *= mHeight;
		float f3 = AnimUtils.calculateValue(mTranslateZ, f, 0.0F);
		if (!mIsZMapped)
			f3 *= 1000F;
		float f4 = AnimUtils.calculateValue(mAlpha, f, 1.0F);
		float f5 = AnimUtils.calculateValue(mDegree, f, 0.0F) / 3.141593F;
		float f6 = mCenterX;
		if (!mIsXMapped)
			f6 *= mWidth;
		float f7 = mCenterY;
		if (!mIsYMapped)
			f7 *= mHeight;
		Camera camera = mCamera;
		Matrix matrix = transformation.getMatrix();
		camera.save();
		if (f1 != 0.0F || f2 != 0.0F || f3 != 0.0F)
			camera.translate(f1, -f2, f3);
		float f8;
		if (f5 != 0.0F)
			if (mIsYAxis)
				camera.rotateY(f5);
			else
				camera.rotateX(f5);
		camera.getMatrix(matrix);
		camera.restore();
		f8 = 1.0F;
		if (mScale != null)
			f8 = AnimUtils.calculateValue(mScale, f, 1.0F);
		if (f8 != 1.0F)
			matrix.preScale(f8, f8);
		if (f5 != 0.0F || f8 != 1.0F) {
			matrix.preTranslate(-f6, -f7);
			matrix.postTranslate(f6, f7);
		}
		transformation.setAlpha(f4);
	}

	public void initialize(int i, int j, int k, int l) {
		super.initialize(i, j, k, l);
		mCamera = new Camera();
		mWidth = i;
		mHeight = j;
	}

	public void setIsXMapped(boolean flag) {
		mIsXMapped = flag;
	}

	public void setIsYMapped(boolean flag) {
		mIsYMapped = flag;
	}

	public void setIsZMapped(boolean flag) {
		mIsZMapped = flag;
	}
}
