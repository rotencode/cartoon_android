package com.songtzu.cartoon.u.anim;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ZoomUtil {

	private View thumb;

	private final long len = 500l;// animation length
	private float startScaleFinal;
	private ArrayList<Animator> animators = new ArrayList<Animator>(4);

	private AnimatorSet itemSet = new AnimatorSet();
	private AnimatorSet imgSet = new AnimatorSet();
	private Rect startBounds = new Rect();
	private Rect finalBounds = new Rect();
	private Point globalOffset = new Point();

	private View mContainer, mDetailView;
	
	public ZoomUtil(View container, View detail) {
		mContainer = container;
		mDetailView = detail;

		itemSet.setDuration(len);
		itemSet.setInterpolator(new DecelerateInterpolator());

		imgSet.setDuration(len);
		imgSet.setInterpolator(new DecelerateInterpolator());
		imgSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// thumb.setAlpha(1f);
				mDetailView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// thumb.setAlpha(1f);
				mDetailView.setVisibility(View.GONE);
			}
		});
	}

	public void zoomImageFromThumb(View thumbView, String resId) {
		thumb = thumbView;
		// expandedImageView.setImageResource(resId);
		// ImageManager.from(mActivity).displayImage(expandedImageView, resId,
		// R.color.HintTextColor, Source.SDCARD);
		animation();
	}

	public void zoomImageFromThumb(View thumbView, Bitmap bitmap) {
		thumb = thumbView;
		// expandedImageView.setImageBitmap(bitmap);
		animation();
	}

	public void zoomImageFromThumb(View thu, int pos) {
		thumb = thu;
		// viewPager.setCurrentItem(pos);
		animation();
	}

	public void empty() {
		animators.clear();
		startBounds.setEmpty();
		finalBounds.setEmpty();
		globalOffset.set(0, 0);
	}

	private void animation() {
		empty();

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step
		// involves lots of math. Yay, math.

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the
		// final bounds are the global visible rectangle of the container view.
		// Also
		// set the container view's offset as the origin for the bounds, since
		// that's
		// the origin for the positioning animation properties (X, Y).
		thumb.getGlobalVisibleRect(startBounds);
		mContainer.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);
		// finalBounds.top = -(globalOffset.y * 1);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the
		// "center crop" technique. This prevents undesirable stretching during
		// the animation.
		// Also calculate the start scaling factor (the end scaling factor is
		// always 1.0).
		// float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScaleFinal = (float) startBounds.height()
					/ finalBounds.height();
			float startWidth = startScaleFinal * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScaleFinal = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScaleFinal * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins,
		// it will position the zoomed-in view in the place of the thumbnail.
		// thumb.setAlpha(0f);
		mDetailView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations to the
		// top-left corner of
		// the zoomed-in view (the default is the center of the view).
		mDetailView.setPivotX(0f);
		mDetailView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.X,
				startBounds.left, finalBounds.left));
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.Y,
				startBounds.top, finalBounds.top));
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.SCALE_X,
				startScaleFinal, 1f));
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.SCALE_Y,
				startScaleFinal, 1f));
		itemSet.playTogether(animators);

		itemSet.start();
	}

	public void hide() {
		animators.clear();
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.X,
				startBounds.left));
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.Y,
				startBounds.top));
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.SCALE_X,
				startScaleFinal));
		animators.add(ObjectAnimator.ofFloat(mDetailView, View.SCALE_Y,
				startScaleFinal));
		imgSet.playTogether(animators);
		imgSet.start();
	}

	public void destory() {
		animators.clear();
		thumb = null;
		itemSet = null;
		imgSet = null;
		startBounds.setEmpty();
		startBounds=null;
		finalBounds.setEmpty();
		finalBounds=null;
		globalOffset = null;
		mContainer = null;
		mDetailView = null;
	}
}