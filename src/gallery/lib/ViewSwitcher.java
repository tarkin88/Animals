package gallery.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
public class ViewSwitcher extends ViewGroup 
{
	public static interface OnScreenSwitchListener {
		/**
		 * Notifies listeners about the new screen. Runs after the animation completed.
		 * 
		 * @param screen The new screen index.
		 */
		void onScreenSwitched(int screen);
	}
	
	public static interface OnScreenClickListener {
		/**
		 * Notifies listeners about click in the screen. Runs after the animation completed.
		 * 
		 * @param screen clicked
		 */
		void onScreenClicked(int screen);

	}
	

	public static int SNAP_VELOCITY = 150;
	private static final int INVALID_SCREEN = -1;

	public Scroller mScroller;
	public VelocityTracker mVelocityTracker;

	public final static int TOUCH_STATE_REST = 0;
	public final static int TOUCH_STATE_SCROLLING = 1;

	public int mTouchState = TOUCH_STATE_REST;

	public float mLastMotionX;
	public int mTouchSlop;
	public int mMaximumVelocity;
	public int mCurrentScreen;
	public int mNextScreen = INVALID_SCREEN;

	private boolean mFirstLayout = true;

	private OnScreenSwitchListener mOnScreenSwitchListener;
	public OnScreenClickListener mOnScreenClickListener;

	
	public ViewSwitcher(Context context) {
		super(context);
		init();
	}

	public ViewSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;
	}

	public void snapToDestination() {
		//final int screenWidth = getWidth();
		//final int whichScreen = (getScrollX() + (screenWidth / 2)) / screenWidth;
		//snapToScreen(whichScreen);
	}

	public void snapToScreen(int whichScreen) {
		if (!mScroller.isFinished())
			return;

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		mNextScreen = whichScreen;

		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));

			// notify observer about screen change
			if (mOnScreenSwitchListener != null)
				mOnScreenSwitchListener.onScreenSwitched(mCurrentScreen);

			mNextScreen = INVALID_SCREEN;
		}
	}

	/**
	 * Returns the index of the currently displayed screen.
	 * 
	 * @return The index of the currently displayed screen.
	 */
	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	/**
	 * Sets the current screen.
	 * 
	 * @param currentScreen The new screen.
	 */
	public void setCurrentScreen(int currentScreen) {
		mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
		scrollTo(mCurrentScreen * getWidth(), 0);
		invalidate();
	}

	/**
	 * Sets the {@link ViewSwitcher.OnScreenSwitchListener}.
	 * 
	 * @param onScreenSwitchListener The listener for switch events.
	 */
	public void setOnScreenSwitchListener(OnScreenSwitchListener onScreenSwitchListener) {
		mOnScreenSwitchListener = onScreenSwitchListener;
	}
	
	public void setOnScreenClickListener(OnScreenClickListener onScreenClickListener) {
		mOnScreenClickListener = onScreenClickListener;
	}
}
