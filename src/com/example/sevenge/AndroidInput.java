package com.example.sevenge;

import java.util.ArrayList;
import java.util.List;

import com.example.sevenge.Input.TouchEvent.TouchType;
import com.example.utils.Log;
import com.example.utils.Pool;
import com.example.utils.Pool.PoolObjectFactory;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class AndroidInput implements Input, OnTouchListener
{
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

	public AndroidInput()
	{
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>()
		{
			public TouchEvent createObject()
			{
				return new TouchEvent();
			}
		};

		touchEventPool = new Pool<TouchEvent>(factory, 100);
		touchEvents = new ArrayList<TouchEvent>();
		touchEventsBuffer = new ArrayList<TouchEvent>();
	}

	@Override
	public boolean isTouched(int pointerIds)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTouchX(int pointerIds)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTouchY(int pointerIds)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<TouchEvent> getTouchEvents()
	{
		synchronized (this)
		{
			for (int i = 0; i < touchEvents.size(); i++)
			{
				touchEventPool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		synchronized (this)
		{
			int action = event.getActionMasked(); 
			int pointerIndex = event.getActionIndex();
			int pointerCount = event.getPointerCount();
			TouchEvent touchEvent;

			for (int i = 0; i < MAX_TOUCHPOINTS; i++)
			{
				if (i >= pointerCount)
				{
					isPointerTouched[i] = false;
					pointerIds[i] = INVALID_POINTER_ID;
					continue;
				}

				int pointerId = event.getPointerId(i);
				if (event.getAction() != MotionEvent.ACTION_MOVE
						&& i != pointerIndex)
				{
					// if it's an up/down/cancel/out event, mask the id to see
					// if we should
					// process it for this touch
					// point
					continue;
				}

				switch (action)
				{
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchType.DOWN;
					touchEvent.pointerID = pointerId;
					touchEvent.x = pointerX[i] = (int) (event.getX(i));
					touchEvent.y = pointerY[i] = (int) (event.getY(i));
					isPointerTouched[i] = true;
					pointerIds[i] = pointerId;
					touchEventsBuffer.add(touchEvent);
					Log.v(TAG, "down");
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchType.UP;
					touchEvent.pointerID = pointerId;
					touchEvent.x = pointerX[i] = (int) (event.getX(i));
					touchEvent.y = pointerY[i] = (int) (event.getY(i));
					isPointerTouched[i] = false;
					pointerIds[i] = INVALID_POINTER_ID;
					touchEventsBuffer.add(touchEvent);
					Log.v(TAG, "up");
					break;
				case MotionEvent.ACTION_MOVE:
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchType.MOVE;
					touchEvent.pointerID = pointerId;
					touchEvent.x = pointerX[i] = (int) (event.getX(i));
					touchEvent.y = pointerY[i] = (int) (event.getY(i));
					isPointerTouched[i] = true;
					pointerIds[i] = pointerId;
					touchEventsBuffer.add(touchEvent);
					Log.v(TAG, "move");
					break;
				}
			}

		}

		try
		{
			Thread.sleep(SLEEP_TIME_BETWEEN_INTERCEPTING_TOUCH_EVENTS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}
