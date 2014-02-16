package com.engine.sevenge.sample;

import android.view.MotionEvent;

import com.engine.sevenge.input.InputEvent;
import com.engine.sevenge.input.SingleTouchDetector.onGestureListener;
import com.engine.sevenge.utils.Log;

public class SampleGestureListener implements onGestureListener {
	private static final String TAG = "Gesture";
	private final InputProcessor ip;

	public SampleGestureListener(InputProcessor ip) {
		this.ip = ip;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Log.i(TAG, "Down");
		InputEvent ie = new InputEvent("Down");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onUp(MotionEvent e) {
		Log.i(TAG, "Up");
		InputEvent ie = new InputEvent("Up");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onLongPress(MotionEvent e) {
		Log.i(TAG, "Long Press");
		InputEvent ie = new InputEvent("LongPress");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onDrag(float x1, float y1, float x2, float y2) {
		Log.i(TAG, "Drag");
		InputEvent ie = new InputEvent("Drag");
		ie.x2 = x2;
		ie.y2 = y2;
		ie.y1 = y1;
		ie.x1 = x1;
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onTap(MotionEvent e) {
		Log.i(TAG, "Tap");
		InputEvent ie = new InputEvent("Tap");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.i(TAG, "Double Tap");
		InputEvent ie = new InputEvent("DoubleTap");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

}