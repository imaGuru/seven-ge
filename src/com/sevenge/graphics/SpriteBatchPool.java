
package com.sevenge.graphics;

import com.sevenge.utils.FixedSizeArray;

public class SpriteBatchPool {
	private final FixedSizeArray<SpriteBatch> mFreeObjects;

	public SpriteBatchPool (int maxSize, TextureShaderProgram tsp, int batchSize) {
		mFreeObjects = new FixedSizeArray<SpriteBatch>(maxSize, null);
		for (int i = 0; i < maxSize; i++)
			mFreeObjects.add(new SpriteBatch(0, tsp, batchSize, this));
	}

	public SpriteBatch get () {
		return mFreeObjects.removeLast();
	}

	public void release (SpriteBatch object) {
		mFreeObjects.add(object);
	}

}
