package com.engine.sevenge.input;

import java.util.LinkedList;
import java.util.Queue;

import android.view.MotionEvent;

public class InputEvent {
	public String type;
	public float x1, x2, y1, y2;

	public InputEvent(String type) {
		this.type = type;
	}

	public Queue<MotionEvent> events = new LinkedList<MotionEvent>();
}