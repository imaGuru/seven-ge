package com.engine.sevenge.input;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class InputListener implements OnGestureListener,OnDoubleTapListener{
	private static final String TAG = "Input";
	private Queue<InputEvent> eventQueue = new LinkedList<InputEvent>();
	@Override
	public boolean onDoubleTap(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "DoubleTap!");
		eventQueue.add(new InputEvent(InputEvent.Type.DOUBLETAP, m1));
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "DoubleTapEvent!");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Tap!");
		eventQueue.add(new InputEvent(InputEvent.Type.TAP, m1));
		return true;
	}

	@Override
	public boolean onDown(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Down!");
		eventQueue.add(new InputEvent(InputEvent.Type.DOWN, m1));
		return true;
	}

	@Override
	public boolean onFling(MotionEvent m1, MotionEvent m2, float vx,
			float vy) {
		Log.d(TAG, "Fling!");
		InputEvent ie = new InputEvent(InputEvent.Type.FLING, m1, m2);
		ie.setVelocity(vx, vy);
		eventQueue.add(ie);
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onLongPress(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LongPress!");
		eventQueue.add(new InputEvent(InputEvent.Type.LONGPRESS, m1));
	}

	@Override
	public boolean onScroll(MotionEvent m1, MotionEvent m2, float dx,
			float dy) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Scroll!");
		InputEvent ie = new InputEvent(InputEvent.Type.SCROLL, m1, m2);
		ie.setDistance(dx, dy);
		eventQueue.add(ie);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "ShowPress!");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "TapUp!");
		return true;
	}

	public Queue<InputEvent> getQueue() {
		// TODO Auto-generated method stub
		return eventQueue;
	}
}