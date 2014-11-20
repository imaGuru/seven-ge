
package com.engine.sevenge;

import com.engine.sevenge.assets.AssetManager;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.input.InputListener;
import com.engine.sevenge.io.IO;

/** Class exposing game engine subsystems anywhere in the code. */
public class SevenGE {

	public static InputListener input;
	public static IO io;
	public static Audio audio;
	public static AssetManager assetManager;
	public static GameStateManager stateManager;

}
