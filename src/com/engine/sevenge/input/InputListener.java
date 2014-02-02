package com.engine.sevenge.input;

import java.util.LinkedList;
import java.util.Queue;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class InputListener implements OnTouchListener {
	private final Queue<Detector> detectors = new LinkedList<Detector>();

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Detector d = detectors.peek();
		return d.onTouchEvent(event);
	}

	public void addDetector(Detector d) {
		detectors.add(d);
	}

	public void clearDetectors() {
		detectors.clear();
	}
}