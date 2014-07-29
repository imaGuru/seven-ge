package com.engine.sevenge;

/**
 * 
 * Abstract class invoked in the main game loop. User should extend this class
 * and define his own gamestates in order to interact with the engine and run
 * code in the main loop
 * 
 */
public abstract class GameState
{

	protected final GameActivity gameActivity;

	public GameState(GameActivity gameActivity)
	{
		this.gameActivity = gameActivity;
	}

	// /**
	// * Invoked on creation of a surface
	// */
	// public abstract void onStart();
	//
	// /**
	// * Invoked after user switches gamestates. You should release game state
	// * resources here
	// */
	public abstract void onFinish();

	/**
	 * Invoked in the main game loop to draw the current gamestate
	 */
	public abstract void draw();

	/**
	 * Invoked in the main game loop to update the simulation by a fixed
	 * timestep
	 */
	public abstract void update();

	public abstract void onPause();

	public abstract void onResume();

	public abstract void onSurfaceChange(int width, int height);
}
