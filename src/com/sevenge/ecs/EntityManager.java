
package com.sevenge.ecs;

import com.sevenge.utils.FixedArray;

public class EntityManager {

	private Entity[] mEntities;
	private FixedArray<System> mSystemArrays;
	private int mAvailibleId = 0;

	public EntityManager (int size, int systemsSize) {
		mEntities = new Entity[size];
		mSystemArrays = new FixedArray<System>(systemsSize, new SystemComparator());
	}

	public Entity createEntity (Component[] components, int mask) {
		mEntities[mAvailibleId] = new Entity(mAvailibleId, components.clone(), mask);
		mAvailibleId++;
		return mEntities[mAvailibleId - 1];
	}

	public void removeEntity (int index) {
		mEntities[index] = null;
	}

	public void addSystem (System system) {
		mSystemArrays.add(system);
	}

	public System getSystem (int mask) {
		for (int i = 0; i < mSystemArrays.getCount(); i++) {
			System s = mSystemArrays.get(i);
			if (s.mMask == mask) return s;
		}
		return null;
	}

	public void assignEntities () {
		for (int j = 0; j < mSystemArrays.getCount(); j++)
			mSystemArrays.get(j).entities.clear();

		for (int i = 0; i < mAvailibleId; i++) {
			Entity cure = mEntities[i];
			if (cure == null) continue;
			for (int j = 0; j < mSystemArrays.getCount(); j++) {
				System curs = mSystemArrays.get(j);
				if ((cure.mask & curs.mMask) == curs.mMask) curs.entities.add(cure);
			}
		}
	}
}
