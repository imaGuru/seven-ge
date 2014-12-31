
package com.sevenge.graphics;

import com.sevenge.utils.FixedSizeArray;

public class PrimitiveBatchPool {
	private final FixedSizeArray<PrimitiveBatch> mFreeObjects;

	public PrimitiveBatchPool (int maxSize, ColorShaderProgram csp, int batchSize) {
		mFreeObjects = new FixedSizeArray<PrimitiveBatch>(maxSize, null);
		for (int i = 0; i < maxSize; i++)
			mFreeObjects.add(new PrimitiveBatch(0, csp, batchSize, this));
	}

	public PrimitiveBatch get () {
		return mFreeObjects.removeLast();
	}

	public void release (PrimitiveBatch object) {
		mFreeObjects.add(object);
	}
}
