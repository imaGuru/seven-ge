
package com.engine.sevenge;

public class GameStateManager {

	private GameState currentState = null;
	
	/** Sets the current game state
	 * @param currentState the GameState instance which will be set  */
	public void setCurrentState (GameState currentState) {
		if (currentState == null) {
			throw new NullPointerException("Cannot set a null State");
		}

		if (this.currentState != currentState) {
			if (this.currentState != null) {
				this.currentState.pause();
				this.currentState.dispose();
			}
			currentState.resume();
			// currentState.update();
			this.currentState = currentState;
		}
	}
	/*Invokes the update() method of the current GameState*/
	public void update () {
		if (currentState != null) this.currentState.update();
	}
	/*Invokes the draw() method of the current GameState*/
	public void draw () {
		if (currentState != null) this.currentState.draw();
	}
	/*Invokes the pause() method of the current GameState*/
	public void pause () {
		if (currentState != null) this.currentState.pause();
	}
	/*Invokes the resume() method of the current GameState*/
	public void resume () {
		if (currentState != null) this.currentState.resume();
	}
	/*Invokes the dispose() method of the current GameState*/
	public void dispose () {
		if (currentState != null) this.currentState.dispose();
	}
	/*Invokes the onSurfaceChange() method of the current GameState*/
	public void onSurfaceChange (int width, int height) {
		if (currentState != null) this.currentState.onSurfaceChange(width, height);
	}

}
