
package com.engine.sevenge.assets;

/** Abstract class implemented by all asset loaders
 * @author guru */
public abstract class AssetLoader {

	/** Injected assetManager reference for addition of the loaded asset */
	protected AssetManager assetManager;

	protected AssetLoader (AssetManager as) {
		this.assetManager = as;
	}

	/** Handle loading from text
	 * @param content string containing json description of the asset to be loaded */
	public abstract void load (String content);
}
