
package com.engine.sevenge.assets;

public abstract class AssetLoader {

	protected AssetManager assetManager;

	protected AssetLoader (AssetManager as) {
		this.assetManager = as;
	}

	public abstract void load (String content);
}
