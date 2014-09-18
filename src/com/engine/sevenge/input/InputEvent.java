package com.engine.sevenge.input;

import android.view.MotionEvent;

public class InputEvent {
	public enum Type {
		TAP, DOUBLETAP, DOWN, UP, FLING, SCROLL, LONGPRESS
	}
	public Type type;
	public MotionEvent me1, me2;
	public float distx, disty;
	public float velx, vely;
	
	public InputEvent(Type t,MotionEvent m){
		me1=m;
		type=t;
	}
	public InputEvent(Type t,MotionEvent m1, MotionEvent m2){
		me1=m1;
		me2=m2;
		type=t;
	}
	public void setVelocity(float vx, float vy){
		velx=vx;
		vely=vy;
	}
	public void setDistance(float dx, float dy){
		distx=dx;
		disty=dy;
	}
}