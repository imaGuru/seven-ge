
package com.sevenge.input;

import android.view.MotionEvent;

public class InputEvent {
	public enum Type {
		TAP, DOUBLETAP, DOWN, UP, FLING, SCROLL, LONGPRESS
	}

	public Type type;
	public MotionEvent motionEvent1, motionEvent2;
	public float distX, distY;
	public float velX, velY;

	public InputEvent () {
	}

	public InputEvent (Type type, MotionEvent motionEvent) {
		this.motionEvent1 = motionEvent;
		this.type = type;
	}

	public InputEvent (Type type, MotionEvent motionEvent1, MotionEvent motionEvent2) {
		this.motionEvent1 = motionEvent1;
		this.motionEvent2 = motionEvent2;
		this.type = type;
	}

	public void setVelocity (float velX, float velY) {
		this.velX = velX;
		this.velY = velY;
	}

	public void setDistance (float distX, float distY) {
		this.distX = distX;
		this.distY = distY;
	}
}
