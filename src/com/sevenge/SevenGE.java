
package com.sevenge;

import static android.opengl.GLES20.glViewport;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.SystemClock;

import com.sevenge.assets.AssetManager;
import com.sevenge.audio.Audio;
import com.sevenge.input.Input;
import com.sevenge.sample.SampleGameState;
import com.sevenge.utils.DebugLog;

import fi.iki.elonen.HelloServer;

/** Class exposing game engine subsystems anywhere in the code. */
public class SevenGE implements Renderer {
	private static final String TAG = "GameEngine";
	public static Input input;
	public static Audio audio;
	public static AssetManager assetManager;
	public static GameStateManager stateManager;

	enum GLGameState {
		Initialized, Running, Paused, Finished, Idle
	}

	private long mStartTime = 0;
	private long mLastTime = 0;
	private long mAccum = 0;
	public static final long FRAME_TIME = 33;

	private GLSurfaceView mGLSurfaceView;
	private Activity mActivity;
	/** Current state the game engine is in */
	private GLGameState state;
	private Object stateChanged = new Object();

	public SevenGE (Activity activity, GLSurfaceView glSurfaceView) {
		final ActivityManager activityManager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
			|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL
					.contains("Android SDK built for x86")));

		IO.initialize(activity);
		SevenGE.input = new Input(activity);
		SevenGE.audio = new Audio(activity);
		SevenGE.assetManager = new AssetManager();
		SevenGE.stateManager = new GameStateManager();
		mActivity = activity;
		mGLSurfaceView = glSurfaceView;
		HelloServer server = new HelloServer(activity);
		try {
			server.start();
		} catch (IOException e) {

			e.printStackTrace();
		}

		if (supportsEs2) {
			mGLSurfaceView.setEGLContextClientVersion(2);
			mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			mGLSurfaceView.setPreserveEGLContextOnPause(true);
			mGLSurfaceView.setRenderer(this);
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			mGLSurfaceView.setOnTouchListener(SevenGE.input);
			state = GLGameState.Initialized;
		} else
			state = GLGameState.Finished;
	}

	public void onPause () {
		synchronized (stateChanged) {
			if (mActivity.isFinishing()) {
				state = GLGameState.Finished;
				DebugLog.d(TAG, "Finished");
			} else {
				state = GLGameState.Paused;
				DebugLog.d(TAG, "Paused");
			}
			while (true) {
				try {
					stateChanged.wait();
					break;
				} catch (InterruptedException e) {
				}
			}
		}
		mGLSurfaceView.onPause();
	}

	public void onResume () {
		mGLSurfaceView.onResume();
	}

	/** Called continuously by the glsurface. Implements the frame limited game loop Allows for extension by user by creating a
	 * custom state which is updated and drawn every frame */
	@Override
	public void onDrawFrame (GL10 gl) {
		GLGameState state = null;
		synchronized (stateChanged) {
			state = this.state;
		}
		if (state == GLGameState.Running) {

			mStartTime = SystemClock.uptimeMillis();
			long deltaTime = mStartTime - mLastTime;
			mLastTime = mStartTime;
			if (deltaTime > 250) deltaTime = 250;

			mAccum += deltaTime;
			while (mAccum >= FRAME_TIME) {
				SevenGE.stateManager.update();
				mAccum -= FRAME_TIME;
			}
			float interpolationAlpha = 1.0f * mAccum / FRAME_TIME;
			SevenGE.stateManager.draw(interpolationAlpha);

		} else if (state == GLGameState.Paused) {
			SevenGE.stateManager.pause();
			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				DebugLog.d(TAG, "Idle");
				stateChanged.notifyAll();
			}
		} else if (state == GLGameState.Finished) {
			SevenGE.stateManager.pause();
			SevenGE.stateManager.dispose();

			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				DebugLog.d(TAG, "Idle");
				stateChanged.notifyAll();
			}
		}

	}

	/** Handles setup and changes to the size of the glsurface */
	@Override
	public void onSurfaceChanged (GL10 gl, int width, int height) {
		DebugLog.d(TAG, "onSurfaceChanged");
		// Double calls. fix this
		// Also loading resources add load method to gamestate TODO
		synchronized (stateChanged) {
			// TODO gamestate manager
			if (state == GLGameState.Initialized) SevenGE.stateManager.setCurrentState(new SampleGameState());
			state = GLGameState.Running;
			DebugLog.d(TAG, "Running");
			SevenGE.stateManager.resume();
		}
		SevenGE.stateManager.onSurfaceChange(width, height);
		glViewport(0, 0, width, height);

	}

	/** Function required by glsurface. Not used */
	@Override
	public void onSurfaceCreated (GL10 gl, EGLConfig config) {
		DebugLog.d(TAG, "onSurfaceCreated");

	}

}
