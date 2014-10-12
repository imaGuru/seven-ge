
package com.engine.sevenge.sample;

import android.view.MotionEvent;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.input.GestureProcessor;
import com.engine.sevenge.input.InputProcessor;
import com.engine.sevenge.utils.Log;

public class SampleInputState extends GameState implements InputProcessor, GestureProcessor {

	private final String TAG = "SampleInputGame";

	public SampleInputState (GameActivity gameActivity) {
		super(gameActivity);
		SevenGE.input.setInputProcessor(this);
		SevenGE.input.setGestureProcessor(this);
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		Log.d(TAG, "touchDown" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		Log.d(TAG, "touchUp" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchMove (int x, int y, int pointer) {
		Log.d(TAG, "touchMove" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw () {
		// TODO Auto-generated method stub

	}

	@Override
	public void update () {
		SevenGE.input.process();
	}

	@Override
	public void pause () {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceChange (int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onDoubleTap (MotionEvent me) {
		Log.d(TAG, "onDoubleTap");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent arg0) {
		Log.d(TAG, "onSingleTapConfirmed");
		return false;
	}

	@Override
	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.d(TAG, "onFling");
		return false;
	}

	@Override
	public void onLongPress (MotionEvent arg0) {
		Log.d(TAG, "onLongPress");

	}

	@Override
	public boolean onScroll (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.d(TAG, "onScroll");
		return false;
	}

}
