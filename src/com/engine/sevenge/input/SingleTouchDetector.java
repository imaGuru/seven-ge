package com.engine.sevenge.input;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class SingleTouchDetector extends Detector {

	private int touchSlop;
	private int doubleTapSlop;

	private static final int LONGPRESS_TIMEOUT = ViewConfiguration
			.getLongPressTimeout();
	private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
	private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration
			.getDoubleTapTimeout();
	private static final int LONG_PRESS = 0;
	private static final int TAP = 1;

	private onGestureListener mListener;
	private VelocityTracker mVelocityTracker;
	private float mLastMotionX;
	private float mLastMotionY;

	private int mDoubleTapSlopSquare;
	private int mTouchSlopSquare;
	private MotionEvent mDownEvent;
	private MotionEvent mUpEvent;
	private boolean mInLongPress;
	private SingleTouchHandler mHandler;
	private boolean mAlwaysInTapRegion;
	private int mBiggerTouchSlopSquare;

	public interface onGestureListener {
		public boolean onDown(MotionEvent e);

		public boolean onUp(MotionEvent e);

		public boolean onLongPress(MotionEvent e);

		public boolean onDrag(float mLastMotionX, float mLastMotionY, float deltax,
				float deltay);

		public boolean onTap(MotionEvent e);

		public boolean onDoubleTap(MotionEvent e);
	}

	private class SingleTouchHandler extends Handler {
		SingleTouchHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LONG_PRESS:
				mHandler.removeMessages(TAP);
				mInLongPress = true;
				mListener.onLongPress(mDownEvent);
				break;
			case TAP:
				mListener.onTap(mDownEvent);
				break;
			default:
				throw new RuntimeException("Unknown message " + msg); // never
			}
		}
	}

	public SingleTouchDetector(Context context, onGestureListener stl) {
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		touchSlop = configuration.getScaledTouchSlop();
		doubleTapSlop = configuration.getScaledDoubleTapSlop();
		mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
		mTouchSlopSquare = touchSlop * touchSlop;
		mListener = stl;
		mHandler = new SingleTouchHandler();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float y = ev.getY();
		final float x = ev.getX();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		boolean consumed = false;

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			cancel();
			break;

		case MotionEvent.ACTION_POINTER_UP:
			// Ending a multitouch gesture and going back to 1 finger
			if (ev.getPointerCount() == 2) {
				int index = (((action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT) == 0) ? 1
						: 0;
				mLastMotionX = ev.getX(index);
				mLastMotionY = ev.getY(index);
				mVelocityTracker.recycle();
				mVelocityTracker = VelocityTracker.obtain();
			}
			break;

		case MotionEvent.ACTION_DOWN:
			boolean hadTapMessage = mHandler.hasMessages(TAP);
			if (hadTapMessage)
				mHandler.removeMessages(TAP);
			if ((mDownEvent != null) && (mUpEvent != null) && hadTapMessage
					&& isConsideredDoubleTap(mDownEvent, mUpEvent, ev)) {
				consumed |= mListener.onDoubleTap(ev);
			}

			mLastMotionX = x;
			mLastMotionY = y;
			if (mDownEvent != null) {
				mDownEvent.recycle();
			}
			mDownEvent = MotionEvent.obtain(ev);
			mAlwaysInTapRegion = true;
			mInLongPress = false;

			mHandler.removeMessages(LONG_PRESS);
			mHandler.sendEmptyMessageAtTime(LONG_PRESS,
					mDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
			consumed |= mListener.onDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mInLongPress || ev.getPointerCount() > 1) {
				break;
			}
			final float scrollX = mLastMotionX - x;
			final float scrollY = mLastMotionY - y;
			if (mAlwaysInTapRegion) {
				final int deltaX = (int) (x - mDownEvent.getX());
				final int deltaY = (int) (y - mDownEvent.getY());
				int distance = (deltaX * deltaX) + (deltaY * deltaY);
				if (distance > mTouchSlopSquare) {
					consumed = mListener.onDrag(mLastMotionX,mLastMotionY,x,y);
					mLastMotionX = x;
					mLastMotionY = y;
					mAlwaysInTapRegion = false;
					mHandler.removeMessages(TAP);
					mHandler.removeMessages(LONG_PRESS);
				}
				if (distance > mBiggerTouchSlopSquare) {
				}
			} else if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {
				consumed = mListener.onDrag(mLastMotionX,mLastMotionY,x,y);
				mLastMotionX = x;
				mLastMotionY = y;
			}
			break;

		case MotionEvent.ACTION_UP:
			MotionEvent currentUpEvent = MotionEvent.obtain(ev);
			if (mInLongPress) {
				mHandler.removeMessages(TAP);
				mInLongPress = false;
			} else if (mAlwaysInTapRegion) {
				if (mDownEvent != null && ev.getEventTime() - mDownEvent.getEventTime()<=TAP_TIMEOUT)
					mHandler.sendEmptyMessageDelayed(TAP, DOUBLE_TAP_TIMEOUT);
			}
			consumed = mListener.onUp(ev);
			if (mUpEvent != null) {
				mUpEvent.recycle();
			}
			// Hold the event we obtained above - listeners may have changed the
			// original.
			mUpEvent = currentUpEvent;
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			mHandler.removeMessages(LONG_PRESS);
			break;
		case MotionEvent.ACTION_CANCEL:
			cancel();
		}
		return consumed;
	}

	private void cancel() {
		mHandler.removeMessages(LONG_PRESS);
		mHandler.removeMessages(TAP);
		mVelocityTracker.recycle();
		mVelocityTracker = null;
		if (mInLongPress) {
			mInLongPress = false;
		}
	}

	private boolean isConsideredDoubleTap(MotionEvent firstDown,
			MotionEvent firstUp, MotionEvent secondDown) {

		if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
			return false;
		}

		int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
		int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
		return (deltaX * deltaX + deltaY * deltaY < mDoubleTapSlopSquare);
	}
}
