package com.engine.sevenge;

import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.engine.sevenge.assets.AssetManager;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.input.InputListener;
import com.engine.sevenge.io.IO;
import com.engine.sevenge.utils.Log;

public abstract class GameActivity extends Activity implements Renderer
{
	private static final String TAG = "GameEngine";
	private static final String sTAG = "GLGameState";

	/**
	 * Time recorded at the start of the frame
	 */
	private long mStartTime = 0;
	/**
	 * Time updating and drawing the frame took
	 */
	private long mDeltaTime = 0;
	/**
	 * Time to sleep in order to cap the rendering to desired framerate
	 */
	private long mSleepTime = 0;
	/**
	 * Number of frames skipped because the rendering took too long
	 */
	private int mFramesSkipped = 0;
	/**
	 * The maximum frame time in milliseconds
	 */
	private static final long FRAME_TIME = 32;
	/**
	 * The maximum number of frames until the simulation falls behind. Prevents
	 * spiral of death
	 */
	private static final int MAX_FRAME_SKIPS = 5;
	/**
	 * The width of the surface
	 */
	private int mWidth;
	/**
	 * The height of the surface
	 */
	private int mHeight;

	private GLSurfaceView glSurfaceView;

	enum GLGameState
	{
		Initialized, Running, Paused, Finished, Idle
	}

	private GLGameState state = GLGameState.Initialized;
	private Object stateChanged = new Object();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.d(sTAG, "onCreate");

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT
						.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator") || Build.MODEL
							.contains("Android SDK built for x86")));

		SevenGE.input = new InputListener();
		SevenGE.io = new IO(this);
		SevenGE.audio = new Audio(this);
		SevenGE.assetManager = new AssetManager();
		SevenGE.stateManager = new GameStateManager();

		if (state == GLGameState.Initialized)
			Log.d(sTAG, "Initialized");

		if (supportsEs2)
		{
			glSurfaceView = new GLSurfaceView(this);
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			glSurfaceView.setPreserveEGLContextOnPause(true);
			glSurfaceView.setRenderer(this);
			// glSurfaceView.setOnTouchListener(SevenGE.input);
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			setContentView(glSurfaceView);

		}
		else
		{
			Toast.makeText(this, "This device does not support OpenGL ES 2.0",
					Toast.LENGTH_LONG).show();
			return;
		}

	}

	@Override
	protected void onPause()
	{
		Log.d(sTAG, "onPause");

		synchronized (stateChanged)
		{
			if (isFinishing())
			{
				state = GLGameState.Finished;
				Log.d(sTAG, "Finished");
			}
			else
			{
				state = GLGameState.Paused;
				Log.d(sTAG, "Paused");
			}
			while (true)
			{
				try
				{
					stateChanged.wait();
					break;
				} catch (InterruptedException e)
				{
				}
			}
		}

		glSurfaceView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		Log.d(sTAG, "onResume");

		super.onResume();
		glSurfaceView.onResume();

	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLGameState state = null;
		synchronized (stateChanged)
		{
			state = this.state;
		}
		if (state == GLGameState.Running)
		{

			mDeltaTime = (System.currentTimeMillis() - mStartTime);
			mSleepTime = FRAME_TIME - mDeltaTime;
			if (mSleepTime > 0)
			{
				try
				{
					Thread.sleep(mSleepTime);
				} catch (InterruptedException e)
				{
				}
			}
			mFramesSkipped = 0;
			while (mSleepTime < 0 && mFramesSkipped < MAX_FRAME_SKIPS)
			{
				mSleepTime += FRAME_TIME;
				SevenGE.stateManager.update();
				mFramesSkipped++;
			}

			mStartTime = System.currentTimeMillis();

			SevenGE.stateManager.update();
			SevenGE.stateManager.draw();

		}
		if (state == GLGameState.Paused)
		{
			SevenGE.stateManager.pause();
			synchronized (stateChanged)
			{
				this.state = GLGameState.Idle;
				Log.d(sTAG, "Idle");
				stateChanged.notifyAll();
			}
		}
		if (state == GLGameState.Finished)
		{
			SevenGE.stateManager.pause();
			SevenGE.stateManager.dispose();

			synchronized (stateChanged)
			{
				this.state = GLGameState.Idle;
				Log.d(sTAG, "Idle");
				stateChanged.notifyAll();
			}
		}

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		Log.d(sTAG, "onSurfaceChanged");

		synchronized (stateChanged)
		{
			if (state == GLGameState.Initialized)
				SevenGE.stateManager.setCurrentState(getStartStage());
			state = GLGameState.Running;
			Log.d(sTAG, "Running");
			SevenGE.stateManager.resume();
		}
		SevenGE.stateManager.onSurfaceChange(width, height);
		glViewport(0, 0, width, height);
		mWidth = width;
		mHeight = height;

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		Log.d(sTAG, "onSurfaceCreated");

	}

	public abstract GameState getStartStage();

}
