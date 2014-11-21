
package com.engine.sevenge;

import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLES20UnitTestActivity extends Activity {
	private GLSurfaceView surfaceView;
	private CountDownLatch latch;

	public OpenGLES20UnitTestActivity () {
		latch = new CountDownLatch(1);
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		surfaceView = new GLSurfaceView(this);
		surfaceView.setEGLContextClientVersion(2);
		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		// surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		surfaceView.setRenderer(new EmptyRenderer(latch));
		setContentView(surfaceView);
	}

	@Override
	protected void onResume () {
		super.onResume();
		surfaceView.onResume();
	}

	@Override
	protected void onPause () {
		super.onPause();
		surfaceView.onPause();
	}

	public GLSurfaceView getSurfaceView () throws InterruptedException {
		if (latch != null) {
			latch.await();
			latch = null;
		}
		return surfaceView;
	}

	private static class EmptyRenderer implements GLSurfaceView.Renderer {
		private CountDownLatch latch;

		public EmptyRenderer (CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void onSurfaceChanged (GL10 gl, int width, int height) {
		}

		@Override
		public void onDrawFrame (GL10 gl) {
			if (latch != null) {
				latch.countDown();
				latch = null;
			}
		}

		@Override
		public void onSurfaceCreated (GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub

		}
	}
}
