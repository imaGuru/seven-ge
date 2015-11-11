package com.sevenge.input;

import android.view.MotionEvent;

import com.sevenge.input.Input.TouchEvent;

/** Responsible for intercepting and passing touch events to the Input instance **/
public class TouchHandler {

	/**
	 * Called when Android intercepts a MotionEvent and translates it into a
	 * TouchEvent
	 **/
	public void onTouchEvent(MotionEvent event, Input input) {
		int action = event.getActionMasked();
		int pointerIndex = event.getActionIndex();
		int pointerCount = event.getPointerCount();

		for (int i = 0; i < Input.NUM_TOUCHPOINTS; i++) {
			if (i >= pointerCount) {
				input.isPointerTouched[i] = false;
				input.pointerIds[i] = Input.INVALID_POINTER_ID;
				continue;
			}

			int pointerId = event.getPointerId(i);
			if (event.getAction() != MotionEvent.ACTION_MOVE
					&& i != pointerIndex) {
				// if it's an up/down/cancel/out event, mask the id to see
				// if we should process it for this touch point
				continue;
			}

			int x = 0, y = 0;
			int deltaX, deltaY;

			long timeStamp = System.nanoTime();
			synchronized (input) {
				switch (action) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:

					x = (int) (event.getX(i));
					y = (int) (event.getY(i));

					registerTouchEvent(input, TouchEvent.TOUCH_DOWN, x, y, 0,
							0, pointerId, i, timeStamp);

					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:

					x = (int) (event.getX(i));
					y = (int) (event.getY(i));

					registerTouchEvent(input, TouchEvent.TOUCH_UP, x, y, 0, 0,
							pointerId, i, timeStamp);

					break;
				case MotionEvent.ACTION_MOVE:

					x = (int) (event.getX(i));
					y = (int) (event.getY(i));
					deltaX = x - input.touchX[i];
					deltaY = x - input.touchX[i];

					registerTouchEvent(input, TouchEvent.TOUCH_MOVED, x, y,
							deltaX, deltaY, pointerId, i, timeStamp);
					break;
				}

			}
		}

	}

	/** Creates a TouchEvent instance and puts in the buffer **/
	private void registerTouchEvent(Input input, int type, int x, int y,
			int deltaX, int deltaY, int pointer, int index, long timeStamp) {
		TouchEvent event = input.touchEventPool.newObject();
		event.timeStamp = timeStamp;
		event.pointer = pointer;
		event.x = x;
		event.y = y;
		event.type = type;
		input.isPointerTouched[index] = true;
		input.pointerIds[index] = pointer;
		input.touchX[index] = x;
		input.touchY[index] = y;
		input.deltaX[index] = deltaX;
		input.deltaY[index] = deltaY;
		input.touchEventsBuffer.add(event);
	}

}
