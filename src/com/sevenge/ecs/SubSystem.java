
package com.sevenge.ecs;

import com.sevenge.utils.FixedSizeArray;

public abstract class SubSystem {

	protected int mMask;

	protected FixedSizeArray<Entity> mEntities;

	public SubSystem (int mask, int size) {
		mMask = mask;
		mEntities = new FixedSizeArray<Entity>(size, new EntityComparator());
	}

	public void process () {

	}
}
