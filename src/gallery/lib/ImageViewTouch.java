package gallery.lib;

import gallery.lib.ViewSwitcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class ImageViewTouch extends ImageViewTouchBase {

	public static final float MIN_ZOOM = 0.9f;
	public ScaleGestureDetector mScaleDetector;
	public GestureDetector mGestureDetector;
	public int mTouchSlop;
	public float mCurrentScaleFactor;
	public float mScaleFactor;
	public int mDoubleTapDirection;
	public GestureListener mGestureListener;
	public ScaleListener mScaleListener;

	Boolean zooming=false;
	
	public ImageViewTouch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void init() {
		super.init();
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mGestureListener = new GestureListener();
		mScaleListener = new ScaleListener();

		mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
		mGestureDetector = new GestureDetector(getContext(), mGestureListener,null, true);
		mCurrentScaleFactor = 1f;
		mDoubleTapDirection = 1;

	}

	@Override
	public void setImageRotateBitmapReset(RotateBitmap bitmap, boolean reset) {
		super.setImageRotateBitmapReset(bitmap, reset);
		mScaleFactor = getMaxZoom() / 3;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ViewSwitcher r = (ViewSwitcher) this.getParent().getParent().getParent();

		MotionEvent ev = event;

		if (r.mVelocityTracker == null) {
			r.mVelocityTracker = VelocityTracker.obtain();
		}
		r.mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!r.mScroller.isFinished()) {
				//r.snapToDestination();
				//r.mScroller.abortAnimation();
			}
			r.mLastMotionX = x;

			r.mTouchState = r.mScroller.isFinished() ? ViewSwitcher.TOUCH_STATE_REST : ViewSwitcher.TOUCH_STATE_SCROLLING;

			break;

		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - r.mLastMotionX);

			boolean xMoved = xDiff > r.mTouchSlop;

			if (xMoved) {
				r.mTouchState = ViewSwitcher.TOUCH_STATE_SCROLLING;
			}

			if (r.mTouchState == ViewSwitcher.TOUCH_STATE_SCROLLING && !zooming) {
				r.mLastMotionX = x;
			}
			break;

		case MotionEvent.ACTION_UP:

			if (r.mTouchState == ViewSwitcher.TOUCH_STATE_SCROLLING && !zooming || nextPic) {
				final VelocityTracker velocityTracker = r.mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, r.mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > ViewSwitcher.SNAP_VELOCITY && r.mCurrentScreen > 0) {
					r.snapToScreen(r.mCurrentScreen - 1);
				} else if (velocityX < -ViewSwitcher.SNAP_VELOCITY && r.mCurrentScreen < r.getChildCount() - 1) {
					r.snapToScreen(r.mCurrentScreen + 1);
				} else {
					
					r.snapToDestination();
				}

				if (r.mVelocityTracker != null) {
					r.mVelocityTracker.recycle();
					r.mVelocityTracker = null;
				}
			} else {
				r.mOnScreenClickListener.onScreenClicked(r.getCurrentScreen());
			}

			r.mTouchState = ViewSwitcher.TOUCH_STATE_REST;
			zooming=false;
			break;
		case MotionEvent.ACTION_CANCEL:
			r.mTouchState = ViewSwitcher.TOUCH_STATE_REST;
		}

		mScaleDetector.onTouchEvent(event);
		if (!mScaleDetector.isInProgress())
			mGestureDetector.onTouchEvent(event);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			if (getScale() < 1f) {
				 zoomTo( 1f, 50 );
			}
			break;
		}

		return true;
	}

	@Override
	protected void onZoom(float scale) {
		super.onZoom(scale);
		if (!mScaleDetector.isInProgress())
			mCurrentScaleFactor = scale;
	}

	protected float onDoubleTapPost(float scale, float maxZoom) {
		if (mDoubleTapDirection == 1) {
			if ((scale + (mScaleFactor * 2)) <= maxZoom) {
				return scale + mScaleFactor;
			} else {
				mDoubleTapDirection = -1;
				return maxZoom;
			}
		} else {
			mDoubleTapDirection = 1;
			return 1f;
		}
	}

	class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float scale = getScale();
			float targetScale = scale;
			targetScale = onDoubleTapPost(scale, getMaxZoom());
			targetScale = Math.min(getMaxZoom(),
					Math.max(targetScale, MIN_ZOOM));
			mCurrentScaleFactor = targetScale;
			zoomTo(targetScale, e.getX(), e.getY(), 200);
			invalidate();
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (getScale() > 1f)
			{
				ViewSwitcher.SNAP_VELOCITY=1000;
				zooming=true;
				if (e1 == null || e2 == null)
					return false;
				if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
					return false;
				if (mScaleDetector.isInProgress())
					return false;
				if (getScale() == 1f)
					return false;
				scrollBy(-distanceX, -distanceY);
				invalidate();
			}else{
				zooming=false;
				nextPic=false;
				ViewSwitcher.SNAP_VELOCITY=150;
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
			ViewSwitcher.SNAP_VELOCITY=200;
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		@SuppressWarnings("unused")
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			zooming=true;
			float span = detector.getCurrentSpan() - detector.getPreviousSpan();
			float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
			if (true) {
				targetScale = Math.min(getMaxZoom(),
						Math.max(targetScale, MIN_ZOOM));
				zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
				mCurrentScaleFactor = Math.min(getMaxZoom(),
						Math.max(targetScale, MIN_ZOOM));
				mDoubleTapDirection = 1;
				invalidate();
				return true;
			}
			return false;
		}
	}
}
