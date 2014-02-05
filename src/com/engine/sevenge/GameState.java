package com.engine.sevenge;

public abstract class GameState 
{
	public abstract void onStart();
	public abstract void onFinish();
	public abstract void draw();
	public abstract void update();
}
