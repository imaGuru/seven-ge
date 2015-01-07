
package com.sevenge;

public class GameStateManager {

	private GameState currentState = null;

	/** Sets the current game state
	 * @param currentState the GameState instance which will be set */
	public void setCurrentState (GameState currentState) {
		if (currentState == null) {
			throw new NullPointerException("Cannot set a null State");
		}

		if (this.currentState != currentState) {
			if (this.currentState != null) {
				this.currentState.pause();
				this.currentState.dispose();
			}
			
			currentState.load();
			currentState.resume();
			
			this.currentState = currentState;
			
		}

	}
	public void load(){
		this.currentState.load();
	}

	/** Invokes the update() method of the current GameState */
	public void update () {
		this.currentState.update();
	}

	/** Invokes the draw() method of the current GameState */
	public void draw (float a) {
		this.currentState.draw(a);
	}

	/** Invokes the pause() method of the current GameState */
	public void pause () {
		this.currentState.pause();
	}

	/** Invokes the resume() method of the current GameState */
	public void resume () {
		this.currentState.resume();
	}

	/** Invokes the dispose() method of the current GameState */
	public void dispose () {
		this.currentState.dispose();
	}
}
