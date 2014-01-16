package com.engine.sevenge.input;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.engine.sevenge.input.Input.TouchEvent.TouchType;
import com.engine.sevenge.utils.Log;
import com.engine.sevenge.utils.Pool;
import com.engine.sevenge.utils.Pool.PoolObjectFactory;

public class AndroidInput implements Input, OnTouchListener {
	private final String TAG = "AndroidInput";

	private static final int MAX_TOUCHPOINTS = 5;
	private static final int SLEEP_TIME_BETWEEN_INTERCEPTING_TOUCH_EVENTS = 16;

	private final int INVALID_POINTER_ID = -1;

	Pool<TouchEvent> touchEventPool;
	List<TouchEvent> touchEvents;
	List<TouchEvent> touchEventsBuffer;

	boolean[] isPointerTouched = new boolean[MAX_TOUCHPOINTS];
	int[] pointerX = new int[MAX_TOUCHPOINTS];
	int[] pointerY = new int[MAX_TOUCHPOINTS];
	int[] pointerIds = new int[MAX_TOUCHPOINTS];

	class AndroidInputTranslator {
		static final int MAX_MS_TAP_INTERVAL = 200;

		boolean[] isDragged = new boolean[MAX_TOUCHPOINTS];
		int[] dragStartX = new int[MAX_TOUCHPOINTS];
		int[] dragStartY = new int[MAX_TOUCHPOINTS];

		long[] touchStartTime = new long[MAX_TOUCHPOINTS];

		void detectDrag(TouchEvent touchEvent) {
			if (touchEvent == null)
				return;

			if (touchEvent.pointerID == INVALID_POINTER_ID)
				return;

			switch (touchEvent.type) {
			case DOWN:
				isDragged[touchEvent.pointerID] = true;
				dragStartX[touchEvent.pointerID] = touchEvent.x;
				dragStartY[touchEvent.pointerID] = touchEvent.y;
				break;
			case UP:
				isDragged[touchEvent.pointerID] = false;
				break;
			case MOVE:
				if (isDragged[touchEvent.pointerID] == true) {
					TouchEvent dragEvent = touchEventPool.newObject();
					dragEvent.type = TouchType.DRAG;
					dragEvent.pointerID = touchEvent.pointerID;
					dragEvent.x = touchEvent.x;
					dragEvent.y = touchEvent.y;
					dragEvent.startX = dragStartX[touchEvent.pointerID];
					dragEvent.startY = dragStartY[touchEvent.pointerID];
					dragEvent.eventTime = touchEvent.eventTime;
					touchEventsBuffer.add(dragEvent);
					Log.v(TAG, dragEvent.type + ", pointer : "
							+ dragEvent.pointerID + " , pos : (" + dragEvent.x
							+ "," + dragEvent.y + ")");
				}
				break;
			default:
				break;
			}
		}

		void detectTap(TouchEvent touchEvent) {

			if (touchEvent == null)
				return;

			switch (touchEvent.type) {
			case DOWN:
				touchStartTime[touchEvent.pointerID] = touchEvent.eventTime;
				break;
			case UP:
				if (touchEvent.eventTime - touchStartTime[touchEvent.pointerID] <= MAX_MS_TAP_INTERVAL) {
					TouchEvent tapEvent = touchEventPool.newObject();
					tapEvent.type = TouchType.TAP;
					tapEvent.pointerID = touchEvent.pointerID;
					tapEvent.x = touchEvent.x;
					tapEvent.y = touchEvent.y;
					tapEvent.eventTime = touchEvent.eventTime;
					touchEventsBuffer.add(tapEvent);
					Log.v(TAG, tapEvent.type + ", pointer : "
							+ tapEvent.pointerID + " , pos : (" + tapEvent.x
							+ "," + tapEvent.y + ")");
				}
				break;
			default:
				break;
			}

		}
	}

	public AndroidInput() {
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
			@Override
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};

		touchEventPool = new Pool<TouchEvent>(factory, 100);
		touchEvents = new ArrayList<TouchEvent>();
		touchEventsBuffer = new ArrayList<TouchEvent>();

	}

	@Override
	public boolean isTouched(int pointerId) {
		synchronized (this) {
			int index = getIndex(pointerId);
			if (index < 0 || index >= MAX_TOUCHPOINTS)
				return false;
			else
				return isPointerTouched[pointerId];
		}
	}

	@Override
	public int getTouchX(int pointerId) {
		synchronized (this) {
			int index = getIndex(pointerId);
			if (index < 0 || index >= MAX_TOUCHPOINTS)
				return 0;
			else
				return pointerX[index];
		}
	}

	@Override
	public int getTouchY(int pointerId) {
		synchronized (this) {
			int index = getIndex(pointerId);
			if (index < 0 || index >= MAX_TOUCHPOINTS)
				return 0;
			else
				return pointerY[index];
		}
	}

	@Override
	public List<TouchEvent> getTouchEvents() {
		synchronized (this) {
			for (int i = 0; i < touchEvents.size(); i++) {
				touchEventPool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}

	private int getIndex(int pointerId) {
		for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
			if (pointerIds[i] == pointerId) {
				return i;
			}
		}
		return INVALID_POINTER_ID;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		synchronized (this) {
			int action = event.getActionMasked();
			int pointerIndex = event.getActionIndex();
			int pointerCount = event.getPointerCount();
			TouchEvent touchEvent = null;

			for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
				if (i >= pointerCount) {
					isPointerTouched[i] = false;
					pointerIds[i] = INVALID_POINTER_ID;
					continue;
				}

				int pointerId = event.getPointerId(i);
				if (event.getAction() != MotionEvent.ACTION_MOVE
						&& i != pointerIndex) {
					// if it's an up/down/cancel/out event, mask the id to see
					// if we should
					// process it for this touch
					// point
					continue;
				}

				switch (action) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchType.DOWN;
					touchEvent.pointerID = pointerId;
					touchEvent.x = pointerX[i] = (int) (event.getX(i));
					touchEvent.y = pointerY[i] = (int) (event.getY(i));
					touchEvent.eventTime = event.getEventTime();
					isPointerTouched[i] = true;
					pointerIds[i] = pointerId;
					touchEventsBuffer.add(touchEvent);
					Log.v(TAG, touchEvent.type + ", pointer : "
							+ touchEvent.pointerID + " , pos : ("
							+ touchEvent.x + "," + touchEvent.y + ")");
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchType.UP;
					touchEvent.pointerID = pointerId;
					touchEvent.x = pointerX[i] = (int) (event.getX(i));
					touchEvent.y = pointerY[i] = (int) (event.getY(i));
					touchEvent.eventTime = event.getEventTime();
					isPointerTouched[i] = false;
					pointerIds[i] = INVALID_POINTER_ID;
					touchEventsBuffer.add(touchEvent);
					Log.v(TAG, touchEvent.type + ", pointer : "
							+ touchEvent.pointerID + " , pos : ("
							+ touchEvent.x + "," + touchEvent.y + ")");
					break;
				case MotionEvent.ACTION_MOVE:
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchType.MOVE;
					touchEvent.pointerID = pointerId;
					touchEvent.x = pointerX[i] = (int) (event.getX(i));
					touchEvent.y = pointerY[i] = (int) (event.getY(i));
					touchEvent.eventTime = event.getEventTime();
					isPointerTouched[i] = true;
					pointerIds[i] = pointerId;
					touchEventsBuffer.add(touchEvent);
					Log.v(TAG, touchEvent.type + ", pointer : "
							+ touchEvent.pointerID + " , pos : ("
							+ touchEvent.x + "," + touchEvent.y + ")");
					break;
				}

				// translator.detectDrag(touchEvent);
				// translator.detectTap(touchEvent);

			}
		}

		try {
			Thread.sleep(SLEEP_TIME_BETWEEN_INTERCEPTING_TOUCH_EVENTS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}
