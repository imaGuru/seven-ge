
package com.sevenge.ecs;

import com.sevenge.utils.FixedArray;

public abstract class System {

	int mMask;

	FixedArray<Entity> entities;

	public System (int mask, int size) {
		mMask = mask;
		entities = new FixedArray<Entity>(size, new EntityComparator());
	}

	public abstract void handleMessage (Message m, Entity e);

	public void process () {

	}

	public void process (float interpolationAlpha) {

	}
}
