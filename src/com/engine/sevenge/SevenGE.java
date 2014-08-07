package com.engine.sevenge;

import com.engine.sevenge.assets.AssetManager;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.input.InputListener;
import com.engine.sevenge.io.IO;

/**
 * 
 * Class exposing game engine subsystems to the world. This is a bad approach
 * and should be changed.
 * 
 */
public class SevenGE
{

	/**
	 * Handle to touch input events listener
	 */
	public static InputListener input;
	/**
	 * Handle to file input/output subsystem
	 */
	public static IO io;
	/**
	 * Handle to audio... yeah this should be a manager
	 */
	public static Audio audio;
	/**
	 * Handle to assetManager
	 */
	public static AssetManager assetManager;
	/**
	 * Handle to the GameState machine
	 */
	public static GameStateManager stateManager;

}
