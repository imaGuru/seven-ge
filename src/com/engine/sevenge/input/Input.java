
package com.engine.sevenge.input;

import java.util.ArrayList;

import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.utils.Pool;
import com.engine.sevenge.utils.Pool.PoolObjectFactory;

public class Input implements OnTouchListener {

	private static final int POOL_SIZE = 100;
	private static final int SLEEP_TIME = 10;
	public static final int NUM_TOUCHPOINTS = 5;
	public static final int INVALID_POINTER_ID = -1;

	private ArrayList<InputProcessor> inputProcessors = new ArrayList<InputProcessor>();
	private ArrayList<GestureProcessor> gestureProcessors = new ArrayList<GestureProcessor>();

	private InputProcessor inputProcessor;
	private GestureProcessor gestureProcessor;
	private long currentEventTimeStamp = System.nanoTime();
	TouchHandler touchHandler = new TouchHandler();
	GestureHandler gestureHandler = new GestureHandler(this);
	GestureDetectorCompat gestureDetector;

	int[] touchX = new int[NUM_TOUCHPOINTS];
	int[] touchY = new int[NUM_TOUCHPOINTS];

	int[] deltaX = new int[NUM_TOUCHPOINTS];
	int[] deltaY = new int[NUM_TOUCHPOINTS];

	int[] pointerIds = new int[NUM_TOUCHPOINTS];

	boolean[] isPointerTouched = new boolean[NUM_TOUCHPOINTS];

	ArrayList<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	ArrayList<Gesture> gestures = new ArrayList<Gesture>();
	Pool<TouchEvent> touchEventPool;
	Pool<Gesture> gesturePool;

	public Input (GameActivity ga) {
		gestureDetector = new GestureDetectorCompat(ga, gestureHandler);

		PoolObjectFactory<TouchEvent> touchEventFactory = new PoolObjectFactory<TouchEvent>() {
			@Override
			public TouchEvent createObject () {
				return new TouchEvent();
			}
		};

		PoolObjectFactory<Gesture> gestureFactory = new PoolObjectFactory<Gesture>() {
			@Override
			public Gesture createObject () {
				return new Gesture();
			}
		};

		touchEventPool = new Pool<TouchEvent>(touchEventFactory, POOL_SIZE);
		gesturePool = new Pool<Gesture>(gestureFactory, POOL_SIZE);
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

		int type;
		MotionEvent motionEvent1, motionEvent2;
		float distX, distY;
		float velX, velY;

// public Gesture (Type type, MotionEvent motionEvent) {
// this.motionEvent1 = motionEvent;
// this.type = type;
// }
//
// public Gesture (Type type, MotionEvent motionEvent1, MotionEvent motionEvent2) {
// this.motionEvent1 = motionEvent1;
// this.motionEvent2 = motionEvent2;
// this.type = type;
// }

	}

	@Override
	public boolean onTouch (View v, MotionEvent event) {
		// synchronized in handler.postTouchEvent()
		touchHandler.onTouchEvent(event, this);
		gestureDetector.onTouchEvent(event);

		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {

		}
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

	public void setInputProcessor (InputProcessor processor) {
		synchronized (this) {
			this.inputProcessor = processor;
		}
	}

	public void setGestureProcessor (GestureProcessor processor) {
		synchronized (this) {
			this.gestureProcessor = processor;
		}
	}

	public void process () {

		processTouchEvents();
		processGestures();

	}

	private void processTouchEvents () {

		if (inputProcessor != null) {
			final InputProcessor processor = this.inputProcessor;

			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent e = touchEvents.get(i);
				currentEventTimeStamp = e.timeStamp;
				switch (e.type) {
				case TouchEvent.TOUCH_DOWN:
					processor.touchDown(e.x, e.y, e.pointer, e.button);
					break;
				case TouchEvent.TOUCH_UP:
					processor.touchUp(e.x, e.y, e.pointer, e.button);
					break;
				case TouchEvent.TOUCH_MOVED:
					processor.touchMove(e.x, e.y, e.pointer);
					break;
				}
				touchEventPool.free(e);
			}
		} else {
			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent e = touchEvents.get(i);
				touchEventPool.free(e);
			}
		}

		if (touchEvents.size() == 0) {
			for (int i = 0; i < deltaX.length; i++) {
				deltaX[0] = 0;
				deltaY[0] = 0;
			}
		}

		touchEvents.clear();

	}

	private void processGestures () {

		if (gestureProcessor != null) {
			final GestureProcessor processor = this.gestureProcessor;

			int len = gestures.size();
			for (int i = 0; i < len; i++) {
				Gesture g = gestures.get(i);

				switch (g.type) {

				case Gesture.DOUBLETAP:
					processor.onDoubleTap(g.motionEvent1);
					break;
				case Gesture.FLING:
					processor.onFling(g.motionEvent1, g.motionEvent2, g.velX, g.velY);
					break;
				case Gesture.LONGPRESS:
					processor.onLongPress(g.motionEvent1);
					break;
				case Gesture.SCROLL:
					processor.onScroll(g.motionEvent1, g.motionEvent2, g.distX, g.distY);
					break;
				case Gesture.TAP:
					processor.onSingleTapConfirmed(g.motionEvent1);
					break;
				}
				gesturePool.free(g);
			}
		} else {
			int len = gestures.size();
			for (int i = 0; i < len; i++) {
				Gesture g = gestures.get(i);
				gesturePool.free(g);
			}
		}

		if (gestures.size() == 0) {
			for (int i = 0; i < deltaX.length; i++) {
				deltaX[0] = 0;
				deltaY[0] = 0;
			}
		}

		gestures.clear();

	}

	// todo - support multiple listeners/processors
	public void addGestureProcessor (GestureProcessor gestureProcessor) {
		this.gestureProcessors.add(gestureProcessor);
	}

	// todo - support multiple listeners/processors
	public void addInputProcessor (InputProcessor inputProcessor) {
		this.inputProcessors.add(inputProcessor);

	}
}
