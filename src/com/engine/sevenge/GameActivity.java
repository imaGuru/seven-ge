
package com.engine.sevenge;

import static android.opengl.GLES20.glViewport;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.widget.Toast;

import com.engine.sevenge.assets.AssetManager;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.input.InputListener;
import com.engine.sevenge.io.IO;
import com.engine.sevenge.utils.Log;

import fi.iki.elonen.HelloServer;

/** Class responsible for setting up and running the game engine It contains framelimited game loop and necessary handles for
 * android lifecycle support */
public abstract class GameActivity extends Activity implements Renderer {
	private static final String TAG = "GameEngine";

	/** Time recorded at the start of the frame */
	private long startTime = 0;
	/** Time updating and drawing the frame took */
	private long deltaTime = 0;
	/** Time to sleep in order to cap the rendering to desired framerate */
	private long sleepTime = 0;
	/** Number of frames skipped because the rendering took too long */
	private int framesSkipped = 0;
	/** The maximum frame time in milliseconds */
	public static final long FRAME_TIME = 32;
	/** The maximum number of frames until the simulation falls behind. Prevents spiral of death */
	private static final int MAX_FRAME_SKIPS = 5;

	private GLSurfaceView glSurfaceView;

	/** Keep track of the state we are in. Changes according to android lifecycle */
	enum GLGameState {
		Initialized, Running, Paused, Finished, Idle
	}

	/** Current state the game engine is in */
	private GLGameState state = GLGameState.Initialized;
	private Object stateChanged = new Object();

	/** Google gesture detector for input recogniction */
	private GestureDetectorCompat gestureDetector;

	/** Tiny webserver for serving a web console (not yet implemented) */
	private HelloServer hs;

	/** Function required by android lifecycle, responsible for initialization of the game engine and setting up the OpenGL surface */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		hs = new HelloServer();
		try {
			hs.start();
		} catch (IOException ioe) {
			Log.w("Httpd", "The server could not start.");
		}
		Log.w("Httpd", "Web server initialized.");

		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
			|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL
					.contains("Android SDK built for x86")));

		SevenGE.input = new InputListener();
		SevenGE.io = new IO(this);
		SevenGE.audio = new Audio(this);
		SevenGE.assetManager = new AssetManager();
		SevenGE.stateManager = new GameStateManager();

		if (state == GLGameState.Initialized) Log.d(TAG, "Initialized");

		if (supportsEs2) {
			glSurfaceView = new GLSurfaceView(this);
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			glSurfaceView.setPreserveEGLContextOnPause(true);
			glSurfaceView.setRenderer(this);
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			gestureDetector = new GestureDetectorCompat(this, SevenGE.input);
			gestureDetector.setOnDoubleTapListener(SevenGE.input);
			setContentView(glSurfaceView);

		} else {
			Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
			return;
		}

	}

	/** Gather input events and recognize simple touch events */
	@Override
	public boolean onTouchEvent (MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return false;
	}

	/** Handles pausing the game engine */
	@Override
	protected void onPause () {
		Log.d(TAG, "onPause");

		synchronized (stateChanged) {
			if (isFinishing()) {
				state = GLGameState.Finished;
				Log.d(TAG, "Finished");
			} else {
				state = GLGameState.Paused;
				Log.d(TAG, "Paused");
			}
			while (true) {
				try {
					stateChanged.wait();
					break;
				} catch (InterruptedException e) {
				}
			}
		}

		glSurfaceView.onPause();
		super.onPause();
	}

	/** Handles unpausing the game engine */
	@Override
	protected void onResume () {
		Log.d(TAG, "onResume");

		super.onResume();
		glSurfaceView.onResume();

	}

	/** Handles final destruction of the application */
	@Override
	protected void onDestroy () {
		super.onDestroy();
		hs.stop();
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

			deltaTime = (System.currentTimeMillis() - startTime);
			sleepTime = FRAME_TIME - deltaTime;
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}
			}
			framesSkipped = 0;
			while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
				sleepTime += FRAME_TIME;
				SevenGE.stateManager.update();
				framesSkipped++;
			}

			startTime = System.currentTimeMillis();

			SevenGE.stateManager.update();
			SevenGE.stateManager.draw();

		}
		if (state == GLGameState.Paused) {
			SevenGE.stateManager.pause();
			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				Log.d(TAG, "Idle");
				stateChanged.notifyAll();
			}
		}
		if (state == GLGameState.Finished) {
			SevenGE.stateManager.pause();
			SevenGE.stateManager.dispose();

			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				Log.d(TAG, "Idle");
				stateChanged.notifyAll();
			}
		}

	}

	/** Handles setup and changes to the size of the glsurface */
	@Override
	public void onSurfaceChanged (GL10 gl, int width, int height) {
		Log.d(TAG, "onSurfaceChanged");

		synchronized (stateChanged) {
			if (state == GLGameState.Initialized) SevenGE.stateManager.setCurrentState(getStartStage());
			state = GLGameState.Running;
			Log.d(TAG, "Running");
			SevenGE.stateManager.resume();
		}
		SevenGE.stateManager.onSurfaceChange(width, height);
		glViewport(0, 0, width, height);

	}

	/** Function required by glsurface. Not used */
	@Override
	public void onSurfaceCreated (GL10 gl, EGLConfig config) {
		Log.d(TAG, "onSurfaceCreated");

	}

	/** ??? */
	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public abstract GameState getStartStage ();

}
