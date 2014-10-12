
package com.engine.sevenge.asset;

import com.engine.sevenge.io.FileHandle;

public abstract class AssetLoader<T> {

	private FileHandleResolver resolver;

	AssetLoader (FileHandleResolver resolver) {
		this.resolver = resolver;
	}

	public abstract T load (AssetManager assetManager, String filename);

	public FileHandle resolve (String fileName) {
		return resolver.resolve(fileName);
	}

}
