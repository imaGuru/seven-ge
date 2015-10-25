package com.sevenge.utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.sevenge.R;
import com.sevenge.SevenGE;
import com.sevenge.sample.SampleGameState;
import com.sevenge.sample.TestState;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/** Helper Activity for performing unit tests **/
public class UnitTestActivity extends Activity {
	private GLSurfaceView surfaceView;
	private SevenGE gameEngine;

	public UnitTestActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test);
		surfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
		gameEngine = new SevenGE(this, surfaceView, new SampleGameState());

		// surfaceView = new GLSurfaceView(this);
		// surfaceView.setEGLContextClientVersion(2);
		// surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		// // surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		// surfaceView.setRenderer(new EmptyRenderer());
		// setContentView(surfaceView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		surfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		surfaceView.onPause();
	}

	public GLSurfaceView getSurfaceView() throws InterruptedException {
		return surfaceView;
	}

	private static class EmptyRenderer implements GLSurfaceView.Renderer {
		public EmptyRenderer() {
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
		}

		@Override
		public void onDrawFrame(GL10 gl) {
		}

		@Override
		public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub

		}
	}
}
