
package com.sevenge;

/** Abstract class invoked in the main game loop. The user should extend this class and define his own game states in order to
 * interact with the engine and run code in the main loop */
public abstract class GameState {
	protected final GameActivity gameActivity;

	public GameState (GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	/** Handles destruction and cleanup of the game state */
	public abstract void dispose ();

	/** Handles all drawing by the game state */
	public abstract void draw (float a, boolean updated);

	/** Handles all game logic updates of the game state */
	public abstract void update ();

	/** Handles pausing logic of the game state */
	public abstract void pause ();

	/** Handles resuming of the game state */
	public abstract void resume ();

	/** Informs the the game state of glsurface size
	 * @param width of the glsurface
	 * @param height of the glsurface */
	public abstract void onSurfaceChange (int width, int height);

}
