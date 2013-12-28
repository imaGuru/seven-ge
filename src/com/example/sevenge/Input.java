package com.example.sevenge;

import java.util.List;

import android.view.MotionEvent;

public interface Input
{

	public static class TouchEvent
	{
		enum TouchType
		{
			DOWN, MOVE, UP, TAP, DRAG;
		}

		public int x;
		public int y;
		public TouchType type;
		public int pointerID;
		public long eventTime;

		// drag fields

		public int startX;
		public int startY;
	}

	public static class Test extends TouchEvent{
		public int magic;
	}
	
	public boolean isTouched(int pointerID);

	public int getTouchX(int pointerID);

	public int getTouchY(int pointerID);

	public List<TouchEvent> getTouchEvents();

}
