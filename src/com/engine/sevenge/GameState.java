
package com.engine.sevenge;

/* Abstract class invoked in the main game loop. 
 * The user should extend this class and define his own game states in order to interact with the engine
 * and run code in the main loop */
public abstract class GameState {
	protected final GameActivity gameActivity;

	public GameState (GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	public abstract void dispose ();

	public abstract void draw ();

	public abstract void update ();

	public abstract void pause ();

	public abstract void resume ();

	public abstract void onSurfaceChange (int width, int height);

}
