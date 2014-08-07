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

	public abstract void dispose();

	public abstract void draw();

	public abstract void update();

	public abstract void pause();

	public abstract void resume();

	public void onSurfaceChange(int width, int height)
	{
		// TODO Auto-generated method stub

	}

}
