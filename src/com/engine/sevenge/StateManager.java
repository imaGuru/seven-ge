package com.engine.sevenge;

public class StateManager
{

	private GameState currentState = null;

	public GameState getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(GameState currentState)
	{
		this.currentState = currentState;
	}

	public void update()
	{
		if (currentState != null)
			this.currentState.update();
	}

	public void draw()
	{
		if (currentState != null)
			this.currentState.draw();
	}
}
