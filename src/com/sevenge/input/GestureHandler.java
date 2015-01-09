
package com.sevenge.input;

import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.sevenge.input.Input.Gesture;

public class GestureHandler extends ScaleGestureDetector.SimpleOnScaleGestureListener implements OnGestureListener,
	OnDoubleTapListener {

	Input input;

	public GestureHandler (Input input) {
		this.input = input;
	}

	@Override
	public boolean onDoubleTap (MotionEvent me) {
		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.DOUBLETAP;
			g.motionEvent1 = me;

			input.gesturesBuffer.add(g);
		}

		return false;
	}

	@Override
	public boolean onDoubleTapEvent (MotionEvent me) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent me) {
		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.TAP;
			g.motionEvent1 = me;

			input.gesturesBuffer.add(g);
		}

		return false;
	}

	@Override
	public boolean onDown (MotionEvent arg0) {
		// do not add code here !!!
		return false;
	}

	@Override
	public boolean onFling (MotionEvent me1, MotionEvent me2, float vx, float vy) {
		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.FLING;
			g.motionEvent1 = me1;
			g.motionEvent1 = me2;
			g.velX = vx;
			g.velY = vy;

			input.gesturesBuffer.add(g);
		}

		return false;
	}

	@Override
	public void onLongPress (MotionEvent me1) {
		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.LONGPRESS;
			g.motionEvent1 = me1;

			input.gesturesBuffer.add(g);
		}

	}

	@Override
	public boolean onScroll (MotionEvent me1, MotionEvent me2, float dx, float dy) {
		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.SCROLL;
			g.motionEvent1 = me1;
			g.motionEvent2 = me2;
			g.distX = dx;
			g.distY = dy;
			input.gesturesBuffer.add(g);
		}
		return false;
	}

	@Override
	public void onShowPress (MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp (MotionEvent arg0) {
		// do not add code here !!!
		return false;
	}

	@Override
	public boolean onScale (ScaleGestureDetector detector) {

		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.SCALE;
			g.currentSpan = detector.getCurrentSpan();
			input.gesturesBuffer.add(g);
		}
		return false;
	}

	@Override
	public boolean onScaleBegin (ScaleGestureDetector detector) {

		super.onScaleBegin(detector);

		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.SCALE_BEGIN;
			g.currentSpan = detector.getCurrentSpan();
			input.gesturesBuffer.add(g);
		}

		return true;
	}

	@Override
	public void onScaleEnd (ScaleGestureDetector detector) {

		super.onScaleEnd(detector);

		synchronized (input) {
			Gesture g = input.gesturePool.newObject();
			g.type = Gesture.SCALE_END;
			g.currentSpan = detector.getCurrentSpan();
			input.gesturesBuffer.add(g);
		}
	}

}
