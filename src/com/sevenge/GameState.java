package com.sevenge;

/**
 * Abstract class invoked in the main game loop. The user should extend this
 * class and define his own game states in order to interact with the engine and
 * run code in the main loop
 */
public abstract class GameState {

	public GameState() {
	}

	/** Handles destruction and cleanup of the game state */
	public abstract void dispose();

	/**
	 * Handles all drawing by the game state
	 * 
	 * @param interpolationAlpha
	 *            interpolation variable telling us how much to draw of the
	 *            current state
	 */
	public abstract void draw(float interpolationAlpha);

	/** Handles all game logic updates of the game state */
	public abstract void update();

	/** Handles pausing logic of the game state */
	public abstract void pause();

	/** Handles resuming of the game state */
	public abstract void resume();

	/** Handles loading assets */
	public abstract void load();

}
