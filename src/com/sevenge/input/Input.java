
package com.sevenge.input;

import java.util.ArrayList;

import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import com.sevenge.GameActivity;
import com.sevenge.utils.Pool;
import com.sevenge.utils.Pool.PoolObjectFactory;

public class Input implements OnTouchListener {

	private static final int POOL_SIZE = 100;
	private static final int SLEEP_TIME = 10;
	public static final int NUM_TOUCHPOINTS = 5;
	public static final int INVALID_POINTER_ID = -1;

	private ArrayList<InputProcessor> inputProcessors = new ArrayList<InputProcessor>();
	private ArrayList<GestureProcessor> gestureProcessors = new ArrayList<GestureProcessor>();

	private long currentEventTimeStamp = System.nanoTime();
	TouchHandler touchDetector = new TouchHandler();
	GestureHandler gestureHandler = new GestureHandler(this);
	GestureDetectorCompat gestureDetector;
	ScaleGestureDetector SGD;

	int[] touchX = new int[NUM_TOUCHPOINTS];
	int[] touchY = new int[NUM_TOUCHPOINTS];

	int[] deltaX = new int[NUM_TOUCHPOINTS];
	int[] deltaY = new int[NUM_TOUCHPOINTS];

	int[] pointerIds = new int[NUM_TOUCHPOINTS];

	boolean[] isPointerTouched = new boolean[NUM_TOUCHPOINTS];

	ArrayList<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	ArrayList<Gesture> gestures = new ArrayList<Gesture>();

	ArrayList<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	ArrayList<Gesture> gesturesBuffer = new ArrayList<Gesture>();

// Pool<TouchEvent> touchEventPool;
// Pool<Gesture> gesturePool;

	public Input (GameActivity ga) {
		gestureDetector = new GestureDetectorCompat(ga, gestureHandler);
		SGD = new ScaleGestureDetector(ga, gestureHandler);

// PoolObjectFactory<TouchEvent> touchEventFactory = new PoolObjectFactory<TouchEvent>() {
// @Override
// public TouchEvent createObject() {
// return new TouchEvent();
// }
// };
//
// PoolObjectFactory<Gesture> gestureFactory = new PoolObjectFactory<Gesture>() {
// @Override
// public Gesture createObject() {
// return new Gesture();
// }
// };

// touchEventPool = new Pool<TouchEvent>(touchEventFactory, POOL_SIZE);
// gesturePool = new Pool<Gesture>(gestureFactory, POOL_SIZE);
	}

	static class TouchEvent {
		static final int TOUCH_DOWN = 0;
		static final int TOUCH_UP = 1;
		static final int TOUCH_DRAGGED = 2;
		static final int TOUCH_SCROLLED = 3;
		static final int TOUCH_MOVED = 4;

		long timeStamp;
		int type;
		int x;
		int y;
		int scrollAmount;
		int button;
		int pointer;
	}

	static class Gesture {
		static final int TAP = 0;
		static final int DOUBLETAP = 1;
		static final int FLING = 2;
		static final int SCROLL = 3;
		static final int LONGPRESS = 4;
		static final int SCALE = 5;
		

		int type;
		MotionEvent motionEvent1, motionEvent2;
		float distX, distY;
		float velX, velY;
		ScaleGestureDetector detector;

	}

	@Override
	public boolean onTouch (View v, MotionEvent event) {
		// synchronized in handler.postTouchEvent()
		touchDetector.onTouchEvent(event, this);
		gestureDetector.onTouchEvent(event);
		SGD.onTouchEvent(event);

		// try {
		// Thread.sleep(SLEEP_TIME);
		// } catch (InterruptedException e) {
		//
		// }
		return true;
	}

	public int getX () {
		synchronized (this) {
			return touchX[0];
		}
	}

	public int getY () {
		synchronized (this) {
			return touchY[0];
		}
	}

	public int getX (int pointer) {
		synchronized (this) {
			return touchX[pointer];
		}
	}

	public int getY (int pointer) {
		synchronized (this) {
			return touchX[pointer];
		}
	}

	public boolean isTouched (int pointer) {
		synchronized (this) {
			return isPointerTouched[pointer];
		}
	}

	public void process () {

		synchronized (this) {

			// for (int i = 0; i < touchEventsBuffer.size(); i++) {
			// touchEventPool.free(touchEventsBuffer.get(i));
			// }
			//
			// for (int i = 0; i < gesturesBuffer.size(); i++) {
			// gesturePool.free(gesturesBuffer.get(i));
			// }

			touchEvents.clear();
			gestures.clear();
			touchEvents.addAll(touchEventsBuffer);
			gestures.addAll(gesturesBuffer);
			touchEventsBuffer.clear();
			gesturesBuffer.clear();

		}

		processTouchEvents();
		processGestures();

	}

	private void processTouchEvents () {

		if (!inputProcessors.isEmpty()) {

			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent e = touchEvents.get(i);
				currentEventTimeStamp = e.timeStamp;
				switch (e.type) {
				case TouchEvent.TOUCH_DOWN:
					for (InputProcessor ip : inputProcessors)
						ip.touchDown(e.x, e.y, e.pointer, e.button);
					break;
				case TouchEvent.TOUCH_UP:
					for (InputProcessor ip : inputProcessors)
						ip.touchUp(e.x, e.y, e.pointer, e.button);
					break;
				case TouchEvent.TOUCH_MOVED:

					for (InputProcessor ip : inputProcessors)
						ip.touchMove(e.x, e.y, e.pointer);
					break;
				}
			}
		}

		if (touchEvents.size() == 0) {
			for (int i = 0; i < deltaX.length; i++) {
				deltaX[0] = 0;
				deltaY[0] = 0;
			}
		}

	}

	private void processGestures () {

		if (!gestureProcessors.isEmpty()) {

			int len = gestures.size();
			for (int i = 0; i < len; i++) {
				Gesture g = gestures.get(i);

				switch (g.type) {
				case Gesture.DOUBLETAP:
					for (GestureProcessor gp : gestureProcessors)
						gp.onDoubleTap(g.motionEvent1);
					break;
				case Gesture.FLING:
					for (GestureProcessor gp : gestureProcessors)
						gp.onFling(g.motionEvent1, g.motionEvent2, g.velX, g.velY);
					break;
				case Gesture.LONGPRESS:
					for (GestureProcessor gp : gestureProcessors)
						gp.onLongPress(g.motionEvent1);
					break;
				case Gesture.SCROLL:
					for (GestureProcessor gp : gestureProcessors)
						gp.onScroll(g.motionEvent1, g.motionEvent2, g.distX, g.distY);
					break;
				case Gesture.TAP:
					for (GestureProcessor gp : gestureProcessors)
						gp.onSingleTapConfirmed(g.motionEvent1);
					break;
	            case Gesture.SCALE:
	                 for (GestureProcessor gp : gestureProcessors)
	                     gp.onScale(g.detector);
	                 break;
				}

			}
		}

		if (gestures.size() == 0) {
			for (int i = 0; i < deltaX.length; i++) {
				deltaX[0] = 0;
				deltaY[0] = 0;
			}
		}

	}

	public void addGestureProcessor (GestureProcessor gestureProcessor) {
		this.gestureProcessors.add(gestureProcessor);
	}

	public void addInputProcessor (InputProcessor inputProcessor) {
		this.inputProcessors.add(inputProcessor);

	}
}
