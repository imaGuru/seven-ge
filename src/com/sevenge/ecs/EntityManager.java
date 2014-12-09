
package com.sevenge.ecs;

import com.sevenge.utils.FixedArray;

/** Class managing entities and systems. Heart of ECS */
public class EntityManager {

	private Entity[] mEntities;
	private FixedArray<System> mSystemArrays;
	private int mAvailibleId = 0;

	/** Create new EntityManager with specified capacity
	 * @param size maximum number of entities
	 * @param systemsSize maximum number of systems */
	public EntityManager (int size, int systemsSize) {
		mEntities = new Entity[size];
		mSystemArrays = new FixedArray<System>(systemsSize, new SystemComparator());
	}

	/** Creates new entity with specified component array and mask
	 * @param components contained by this entity
	 * @param mask of entity
	 * @return creted entity object */
	public Entity createEntity (Component[] components, int mask) {
		mEntities[mAvailibleId] = new Entity(mAvailibleId, components.clone(), mask);
		mAvailibleId++;
		return mEntities[mAvailibleId - 1];
	}

	/** Remove entity with specified index
	 * @param index */
	public void removeEntity (int index) {
		mEntities[index] = null;
	}

	/** Register system for receving entities to process
	 * @param system to add */
	public void addSystem (System system) {
		mSystemArrays.add(system);
	}

	/** Retrieves system with specified component mask
	 * @param mask of entities processed by this system
	 * @return */
	public System getSystem (int mask) {
		for (int i = 0; i < mSystemArrays.getCount(); i++) {
			System s = mSystemArrays.get(i);
			if (s.mMask == mask) return s;
		}
		return null;
	}

	/** Assigns entities to systems that want to process them */
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
