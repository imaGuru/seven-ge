package com.engine.sevenge;

public class GameStateManager {

	private GameState currentState = null;

	public GameState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(GameState currentState) {
		if (this.currentState != null)
			this.currentState.onFinish();
		this.currentState = currentState;
		this.currentState.onStart();
	}

	public void update() {
		if (currentState != null)
			this.currentState.update();
	}

	public void draw() {
		if (currentState != null)
			this.currentState.draw();
	}
}
