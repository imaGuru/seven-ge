
package com.sevenge.input;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.sevenge.utils.Pool;
import com.sevenge.utils.Pool.PoolObjectFactory;

public class InputListener implements OnGestureListener, OnDoubleTapListener {
	private static final String TAG = "Input";
	private static final int POOL_SIZE = 100;
	private Pool<InputEvent> inputEventPool;
	private List<InputEvent> inputEvents;
	private List<InputEvent> inputEventsBuffer;

	public InputListener () {
		PoolObjectFactory<InputEvent> factory = new PoolObjectFactory<InputEvent>() {
			@Override
			public InputEvent createObject () {
				return new InputEvent();
			}
		};

		inputEventPool = new Pool<InputEvent>(factory, POOL_SIZE);
		inputEvents = new ArrayList<InputEvent>();
		inputEventsBuffer = new ArrayList<InputEvent>();
	}

	@Override
	public boolean onDoubleTap (MotionEvent m1) {

		Log.d(TAG, "DoubleTap!");
		InputEvent e = inputEventPool.newObject();
		e.type = InputEvent.Type.DOUBLETAP;
		e.motionEvent1 = m1;
		inputEventsBuffer.add(e);

		return true;
	}

	@Override
	public boolean onDoubleTapEvent (MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "DoubleTapEvent!");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Tap!");

		InputEvent e = inputEventPool.newObject();
		e.type = InputEvent.Type.TAP;
		e.motionEvent1 = m1;
		inputEventsBuffer.add(e);

		return true;
	}

	@Override
	public boolean onDown (MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Down!");

		InputEvent e = inputEventPool.newObject();
		e.type = InputEvent.Type.DOWN;
		e.motionEvent1 = m1;
		inputEventsBuffer.add(e);

		return true;
	}

	@Override
	public boolean onFling (MotionEvent m1, MotionEvent m2, float vx, float vy) {
		Log.d(TAG, "Fling!");

		InputEvent e = inputEventPool.newObject();
		e.type = InputEvent.Type.FLING;
		e.motionEvent1 = m1;
		e.motionEvent1 = m2;
		e.setVelocity(vx, vy);
		inputEventsBuffer.add(e);

		return true;
	}

	@Override
	public void onLongPress (MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LongPress!");

		InputEvent e = inputEventPool.newObject();
		e.type = InputEvent.Type.LONGPRESS;
		e.motionEvent1 = m1;
		inputEventsBuffer.add(e);

	}

	@Override
	public boolean onScroll (MotionEvent m1, MotionEvent m2, float dx, float dy) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Scroll!");

		InputEvent e = inputEventPool.newObject();
		e.type = InputEvent.Type.SCROLL;
		e.motionEvent1 = m1;
		e.motionEvent2 = m2;
		e.setDistance(dx, dy);
		inputEventsBuffer.add(e);

		return true;
	}

	@Override
	public void onShowPress (MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "ShowPress!");
	}

	@Override
	public boolean onSingleTapUp (MotionEvent m1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "TapUp!");
		return true;
	}

	public List<InputEvent> getInputEvents () {
		synchronized (this) {
			for (int i = 0; i < inputEvents.size(); i++) {
				inputEventPool.free(inputEvents.get(i));
			}
			inputEvents.clear();
			inputEvents.addAll(inputEventsBuffer);
			inputEventsBuffer.clear();
			return inputEvents;
		}
	}

}
