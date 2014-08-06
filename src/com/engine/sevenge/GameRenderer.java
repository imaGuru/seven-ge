package com.engine.sevenge;

import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

import com.engine.sevenge.graphics.Drawable;
import com.engine.sevenge.graphics.RenderQueue;

public class GameRenderer implements Renderer
{

	enum State
	{
		Initialized, Running, Paused, Finished, Idle
	}

	Object stateChanged = new Object();
	State state;

	private static final String TAG = "GameEngine";
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
	 * The render queue for handing out to game states to outsource drawing
	 * commands
	 */
	private RenderQueue mRenderQueue;
	/**
	 * The width of the surface
	 */
	private int mWidth;
	/**
	 * The height of the surface
	 */
	private int mHeight;

	private GameActivity mGameActivity;

	/**
	 * GameState initialization should take place here
	 */
	GameRenderer(GameActivity context)
	{
		mGameActivity = context;
		// ss = new SampleGameState(context);
	}

	/**
	 * Function invoked by the GLThread. In this function current GameState is
	 * updated and drawn
	 */
	@Override
	public void onDrawFrame(GL10 arg0)
	{
		State drawState;

		synchronized (stateChanged)
		{
			drawState = this.state;
		}

		if (drawState == State.Running)
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
				/* GameState update */
				SevenGE.stateManager.update();
				// ss.update();
				mFramesSkipped++;
			}
			// Log.v(TAG, "FramesSkipped: " + framesSkipped + " FPS: " +
			// (double) 1
			// / (System.currentTimeMillis() - startTime) * 1000);
			mStartTime = System.currentTimeMillis();

			/* GameState update and draw */
			SevenGE.stateManager.update();
			SevenGE.stateManager.draw();
			// ss.update();
			// ss.draw();
		}

		if (drawState == State.Paused)
		{
			SevenGE.stateManager.pause();
			synchronized (stateChanged)
			{
				this.state = State.Idle;
				stateChanged.notifyAll();
			}
		}
		if (drawState == State.Finished)
		{
			SevenGE.stateManager.pause();
			// SevenGE.stateManager.dispose();
			synchronized (stateChanged)
			{
				this.state = State.Idle;
				stateChanged.notifyAll();
			}
		}

	}

	/**
	 * Invoked when the surface changes such as screen rotation. We notify the
	 * current GameState and set the OpenGL viewport to the new resolution
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		glViewport(0, 0, width, height);
		mWidth = width;
		mHeight = height;
		SevenGE.stateManager.onSurfaceChange(width, height);
		// ss.onSurfaceChange(width, height);
	}

	/**
	 * Invoked when the surface is created. We start the current GameState
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{

		mRenderQueue = new RenderQueue();

		synchronized (stateChanged)
		{
			if (state == State.Initialized)
				SevenGE.stateManager.setCurrentState(mGameActivity
						.getStartStage());

			SevenGE.stateManager.resume();

			// screen = mGameActivity.getStartStage();
			state = State.Running;
			// screen.resume();

		}

	}

	public RenderQueue getRenderQueue()
	{
		return mRenderQueue;
	}

	public void addToRenderQueue(Drawable d)
	{
		mRenderQueue.add(d);
	}

	public void render()
	{
		mRenderQueue.render();
	}

	public int getSurfaceHeight()
	{
		return mHeight;
	}

	public int getSurfaceWidth()
	{
		return mWidth;
	}
}