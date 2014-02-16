package com.engine.sevenge;

import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.engine.sevenge.graphics.RenderQueue;
import com.engine.sevenge.sample.SampleGameState;

public class GameRenderer implements Renderer {

	// private static final String TAG = "GameEngine";

	private long startTime = 0;
	private long dt = 0, sleepTime = 0;
	private int framesSkipped = 0;

	private static final long FRAME_TIME = 32;
	private static final int MAX_FRAME_SKIPS = 5;

	private RenderQueue renderQueue;
	private SampleGameState ss;

	private int mWidth, mHeight;

	GameRenderer(Context context) {
		ss = new SampleGameState(context);
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		dt = (System.currentTimeMillis() - startTime);
		sleepTime = FRAME_TIME - dt;
		if (sleepTime > 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		framesSkipped = 0;
		while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
			sleepTime += FRAME_TIME;
			ss.update();
			framesSkipped++;
		}
		// Log.v(TAG, "FramesSkipped: " + framesSkipped + " FPS: " + (double) 1
		// / (System.currentTimeMillis() - startTime) * 1000);
		startTime = System.currentTimeMillis();

		ss.update();
		ss.draw();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		mWidth = width;
		mHeight = height;
		ss.onSurfaceChange(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		renderQueue = new RenderQueue();
		ss.onStart();
	}

	public RenderQueue getRenderQueue() {
		return renderQueue;
	}

	public int getSurfaceHeight() {
		return mHeight;
	}

	public int getSurfaceWidth() {
		return mWidth;
	}
}