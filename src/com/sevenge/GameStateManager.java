
package com.sevenge;

public class GameStateManager {

	private GameState currentState = null;
	private int width;
	private int height;

	/** Sets the current game state
	 * @param currentState the GameState instance which will be set */
	public void setCurrentState (GameState currentState) {
		if (currentState == null) {
			throw new NullPointerException("Cannot set a null State");
		}

		boolean forceOnSurfaceChange = false;

		if (this.currentState != null) forceOnSurfaceChange = true;

		if (this.currentState != currentState) {
			if (this.currentState != null) {
				this.currentState.pause();
				this.currentState.dispose();
			}
			currentState.resume();
			// currentState.update();
			this.currentState = currentState;
		}

		/*
		 * ensures that onSurfaceChange will be called at the beginning of new state and not before width and height are set at
		 * least once
		 */
		if (forceOnSurfaceChange) this.currentState.onSurfaceChange(width, height);

	}

	/* Invokes the update() method of the current GameState */
	public void update () {
		this.currentState.update();
	}

	/* Invokes the draw() method of the current GameState */
	public void draw (float a, boolean updated) {
		this.currentState.draw(a, updated);
	}

	/* Invokes the pause() method of the current GameState */
	public void pause () {
		this.currentState.pause();
	}

	/* Invokes the resume() method of the current GameState */
	public void resume () {
		this.currentState.resume();
	}

	/* Invokes the dispose() method of the current GameState */
	public void dispose () {
		this.currentState.dispose();
	}

	/* Invokes the onSurfaceChange() method of the current GameState */
	public void onSurfaceChange (int width, int height) {
		this.currentState.onSurfaceChange(width, height);
		this.width = width;
		this.height = height;
	}

}
