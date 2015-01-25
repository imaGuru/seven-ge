
package com.sevenge;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.glEnable;
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
import com.sevenge.script.ScriptingEngine;
import com.sevenge.utils.DebugLog;
import com.sevenge.utils.WebConsole;

/** Main game engine class. Initializes all engine subsystems and stores its settings. */
public class SevenGE implements Renderer {
	private static final String TAG = "GameEngine";
	public static long FRAME_TIME = 33;

	private enum GLGameState {
		Initialized, Running, Paused, Finished, Idle
	}

	private GLGameState mState;
	private static Input sInput;
	private static Audio sAudio;
	private static AssetManager sAssetManager;
	private static GameStateManager mGameStateManager;
	private GLSurfaceView mGLSurfaceView;
	private Activity mActivity;
	private static WebConsole sServer;
	private GameState mInitalGameState;

	private long mStartTime = 0;
	private long mLastTime = 0;
	private long mAccum = 0;

	private static int sWidth;
	private static int sHeight;

	/** Sets up game engine subsystems and configures given glSurface for rendering OpenGL ES 2.0
	 * @param activity activity displaying the GLSurface
	 * @param glSurfaceView GLSurface to be used for rendering */
	public SevenGE (Activity activity, GLSurfaceView glSurfaceView, GameState initalGameState) {
		final ActivityManager activityManager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
			|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL
					.contains("Android SDK built for x86")));

		IO.initialize(activity);
		SevenGE.sInput = new Input(activity);
		SevenGE.sAudio = new Audio(activity);
		SevenGE.sAssetManager = new AssetManager();
		SevenGE.mGameStateManager = new GameStateManager();
		mActivity = activity;
		mGLSurfaceView = glSurfaceView;
		mInitalGameState = initalGameState;
		sServer = new WebConsole(activity);
		try {
			sServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (supportsEs2) {
			mGLSurfaceView.setEGLContextClientVersion(2);
			mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			mGLSurfaceView.setPreserveEGLContextOnPause(true);
			mGLSurfaceView.setRenderer(this);
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			mGLSurfaceView.setOnTouchListener(SevenGE.sInput);
			mState = GLGameState.Initialized;
		} else
			mState = GLGameState.Finished;
	}

	/** Pauses the game engine */
	public void onPause () {
		synchronized (this) {
			if (mActivity.isFinishing()) {
				mState = GLGameState.Finished;
				DebugLog.d(TAG, "Finished");
			} else {
				mState = GLGameState.Paused;
				DebugLog.d(TAG, "Paused");
			}
			while (true) {
				try {
					wait();
					break;
				} catch (InterruptedException e) {
				}
			}
		}
		mGLSurfaceView.onPause();
	}

	/** Resumes running of the engine */
	public void onResume () {
		mGLSurfaceView.onResume();
	}

	/** Called continuously by the GLSurface. Implements the frame limited game loop. Executes update and draw calls of the
	 * currently selected GameState */
	@Override
	public void onDrawFrame (GL10 gl) {
		GLGameState state = null;
		synchronized (this) {
			state = this.mState;
		}
		if (state == GLGameState.Running) {

			mStartTime = SystemClock.uptimeMillis();
			long deltaTime = mStartTime - mLastTime;
			mLastTime = mStartTime;
			if (deltaTime > 250) deltaTime = 250;

			mAccum += deltaTime;
			while (mAccum >= FRAME_TIME) {
				SevenGE.mGameStateManager.update();
				mAccum -= FRAME_TIME;
			}
			float interpolationAlpha = 1.0f * mAccum / FRAME_TIME;
			SevenGE.mGameStateManager.draw(interpolationAlpha);

		} else if (state == GLGameState.Paused) {
			SevenGE.mGameStateManager.pause();
			synchronized (this) {
				this.mState = GLGameState.Idle;
				DebugLog.d(TAG, "Idle");
				notifyAll();
			}
		} else if (state == GLGameState.Finished) {
			SevenGE.mGameStateManager.pause();
			SevenGE.mGameStateManager.dispose();

			synchronized (this) {
				this.mState = GLGameState.Idle;
				DebugLog.d(TAG, "Idle");
				notifyAll();
			}
		}

	}

	/** Handles setup and changes to the size of the GLSurface */
	@Override
	public void onSurfaceChanged (GL10 gl, int width, int height) {

		SevenGE.sWidth = width;
		SevenGE.sHeight = height;

		DebugLog.d(TAG, "onSurfaceChanged");
		synchronized (this) {
			if (mState == GLGameState.Initialized) SevenGE.mGameStateManager.setCurrentState(mInitalGameState);
			mState = GLGameState.Running;
			DebugLog.d(TAG, "Running");
			SevenGE.mGameStateManager.resume();
		}
		glViewport(0, 0, width, height);

	}

	/** Called each time GLSurface is created */
	@Override
	public void onSurfaceCreated (GL10 gl, EGLConfig config) {
		DebugLog.d(TAG, "onSurfaceCreated");
		glEnable(GL_CULL_FACE);
	}

	/** Retrieves game engine's AssetManager
	 * @return AssetManager */
	public static AssetManager getAssetManager () {
		return sAssetManager;
	}

	/** Retrieves game engine's Audio
	 * @return Audio */
	public static Audio getAudio () {
		return sAudio;
	}

	/** Retrieves game engine's Input
	 * @return Input */
	public static Input getInput () {
		return sInput;
	}

	/** Retrieves game engine's GameStateManager
	 * @return GameStateManager */
	public static GameStateManager getStateManager () {
		return mGameStateManager;
	}

	/** Retrieves current GLSurface width
	 * @return width of the surface */
	public static int getWidth () {
		return sWidth;
	}

	/** Retrieves current GLSurface height
	 * @return height of the surface */
	public static int getHeight () {
		return sHeight;
	}

	/** Attaches scripting engine to webconsole */
	public static void attachScriptingEngineToServer (ScriptingEngine scriptingEngine) {
		sServer.setScriptingEngine(scriptingEngine);
	}

}
