package com.sevenge.input;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import com.sevenge.utils.Pool;
import com.sevenge.utils.Pool.PoolObjectFactory;

/** Responsible for processing input events and notifying subscribers. **/

public class Input implements OnTouchListener {

	private static final int POOL_SIZE = 100;
	private static final int SLEEP_TIME = 10;
	public static final int NUM_TOUCHPOINTS = 5;
	public static final int INVALID_POINTER_ID = -1;

	private ArrayList<InputProcessor> inputProcessors = new ArrayList<InputProcessor>(
			10);
	private ArrayList<GestureProcessor> gestureProcessors = new ArrayList<GestureProcessor>(
			10);

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

	Pool<TouchEvent> touchEventPool;
	Pool<Gesture> gesturePool;

	public Input(Activity activity) {
		gestureDetector = new GestureDetectorCompat(activity, gestureHandler);
		SGD = new ScaleGestureDetector(activity, gestureHandler);

		PoolObjectFactory<TouchEvent> touchEventFactory = new PoolObjectFactory<TouchEvent>() {
			@Override
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};

		PoolObjectFactory<Gesture> gestureFactory = new PoolObjectFactory<Gesture>() {
			@Override
			public Gesture createObject() {
				return new Gesture();
			}
		};

		touchEventPool = new Pool<TouchEvent>(touchEventFactory, POOL_SIZE);
		gesturePool = new Pool<Gesture>(gestureFactory, POOL_SIZE);
	}

	/** class representing a simple touch event **/
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

	/** class representing a gesture event **/
	static class Gesture {
		static final int TAP = 0;
		static final int DOUBLETAP = 1;
		static final int FLING = 2;
		static final int SCROLL = 3;
		static final int LONGPRESS = 4;
		static final int SCALE = 5;
		static final int SCALE_END = 6;
		static final int SCALE_BEGIN = 7;

		int type;
		MotionEvent motionEvent1, motionEvent2;
		float distX, distY;
		float velX, velY;
		float currentSpan;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
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

	/** returns the x coordinate for the first touch pointer **/
	public int getX() {
		synchronized (this) {
			return touchX[0];
		}
	}

	/** returns the y coordinate for the first touch pointer **/
	public int getY() {
		synchronized (this) {
			return touchY[0];
		}
	}

	/**
	 * returns the x coordinate for the touch pointer
	 * 
	 * @param pointer
	 *            id
	 **/
	public int getX(int pointer) {
		synchronized (this) {
			return touchX[pointer];
		}
	}

	/**
	 * returns the x coordinate for the touch pointer
	 * 
	 * @param pointer
	 *            id
	 **/
	public int getY(int pointer) {
		synchronized (this) {
			return touchX[pointer];
		}
	}

	/**
	 * @param pointer
	 *            id
	 * @return true if the pointer with specified id is touching the screen,
	 *         false otherwise
	 **/
	public boolean isTouched(int pointer) {
		synchronized (this) {
			return isPointerTouched[pointer];
		}
	}

	/**
	 * processes the input events and notifies listeners
	 */
	public void process() {

		synchronized (this) {

			touchEvents.clear();
			gestures.clear();
			for (int i = 0; i < touchEventsBuffer.size(); i++) {
				touchEvents.add(touchEventsBuffer.get(i));
			}
			for (int i = 0; i < gesturesBuffer.size(); i++) {
				gestures.add(gesturesBuffer.get(i));
			}
			touchEventsBuffer.clear();
			gesturesBuffer.clear();

		}

		processTouchEvents();
		processGestures();

		synchronized (this) {
			for (int i = 0; i < touchEvents.size(); i++) {
				touchEventPool.free(touchEvents.get(i));
			}

			for (int i = 0; i < gestures.size(); i++) {
				gesturePool.free(gestures.get(i));
			}
		}

	}

	/**
	 * processes intercepted touch events and notifies the processors
	 */
	private void processTouchEvents() {

		if (!inputProcessors.isEmpty()) {

			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent e = touchEvents.get(i);
				currentEventTimeStamp = e.timeStamp;
				switch (e.type) {
				case TouchEvent.TOUCH_DOWN:
					for (int j = 0; j < inputProcessors.size(); j++)
						inputProcessors.get(j).touchDown(e.x, e.y, e.pointer,
								e.button);
					break;
				case TouchEvent.TOUCH_UP:
					for (int j = 0; j < inputProcessors.size(); j++)
						inputProcessors.get(j).touchUp(e.x, e.y, e.pointer,
								e.button);
					break;
				case TouchEvent.TOUCH_MOVED:
					for (int j = 0; j < inputProcessors.size(); j++)
						inputProcessors.get(j).touchMove(e.x, e.y, e.pointer);
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

	/**
	 * processes intercepted gesture events and notifies the processors
	 */
	private void processGestures() {

		if (!gestureProcessors.isEmpty()) {

			int len = gestures.size();
			for (int i = 0; i < len; i++) {
				Gesture g = gestures.get(i);

				switch (g.type) {
				case Gesture.DOUBLETAP:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onDoubleTap(g.motionEvent1);
					break;
				case Gesture.FLING:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onFling(g.motionEvent1,
								g.motionEvent2, g.velX, g.velY);
					break;
				case Gesture.LONGPRESS:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onLongPress(g.motionEvent1);
					break;
				case Gesture.SCROLL:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onScroll(g.motionEvent1,
								g.motionEvent2, g.distX, g.distY);
					break;
				case Gesture.TAP:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onSingleTapConfirmed(
								g.motionEvent1);
					break;
				case Gesture.SCALE:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onScale(g.currentSpan);
					break;
				case Gesture.SCALE_END:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onScaleEnd(g.currentSpan);
					break;
				case Gesture.SCALE_BEGIN:
					for (int j = 0; j < gestureProcessors.size(); j++)
						gestureProcessors.get(j).onScaleBegin(g.currentSpan);
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

	/**
	 * adds the processor to the list of processors subscribing for gesture
	 * events
	 **/
	public void addGestureProcessor(GestureProcessor gestureProcessor) {
		this.gestureProcessors.add(gestureProcessor);
	}

	/**
	 * adds the processor to the list of processors subscribing for input events
	 **/
	public void addInputProcessor(InputProcessor inputProcessor) {
		this.inputProcessors.add(inputProcessor);
	}

	/**
	 * removes the processor from the list of processors subscribing for input
	 * events
	 **/
	public void removeGestureProcessor(GestureProcessor gestureProcessor) {
		this.gestureProcessors.remove(gestureProcessor);
	}

	/**
	 * removes the processor from the list of processors subscribing for input
	 * events
	 **/
	public void removeInputProcessor(InputProcessor inputProcessor) {
		this.inputProcessors.remove(inputProcessor);
	}

}
