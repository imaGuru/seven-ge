
package com.sevenge.input;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public interface GestureProcessor {

	public boolean onDoubleTap (MotionEvent arg0);

	public boolean onSingleTapConfirmed (MotionEvent arg0);

	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3);

	public void onLongPress (MotionEvent arg0);

	public boolean onScroll (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3);

	public boolean onScale (ScaleGestureDetector detector);

}
