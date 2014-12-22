
package com.sevenge.ecs;

import com.sevenge.utils.FixedSizeArray;

/** Class managing entities and systems. Heart of ECS */
public class EntityManager {

	private FixedSizeArray<Entity> mEntities;
	private FixedSizeArray<SubSystem> mSystemArrays;
	private int mAvailibleId = 0;
	private int mAssignedCount = 0;

	/** Create new EntityManager with specified capacity
	 * @param size maximum number of entities
	 * @param systemsSize maximum number of systems */
	public EntityManager (int size, int systemsSize) {
		mEntities = new FixedSizeArray<Entity>(size, new EntityComparator());
		mSystemArrays = new FixedSizeArray<SubSystem>(systemsSize, new SystemComparator());
	}

	/** Creates new entity with specified component array and mask
	 * @param components contained by this entity
	 * @param mask of entity
	 * @return creted entity object */
	public Entity createEntity (Component[] components, int mask) {
		Entity newEntity = new Entity(mAvailibleId, components.clone(), mask);
		mEntities.add(newEntity);
		mAvailibleId++;
		return newEntity;
	}

	public Entity createEntity (int maxComponents) {
		Entity newEntity = new Entity(mAvailibleId, maxComponents);
		mEntities.add(newEntity);
		mAvailibleId++;
		return newEntity;
	}

	/** Remove entity with specified id
	 * @param index */
	public Entity removeEntity (int id) {
		Entity toRemove = new Entity(id, 0);
		int realid = mEntities.find(toRemove, false);
		if (realid == -1) return null;
		toRemove = mEntities.get(realid);
		mEntities.remove(realid);
		for (int j = 0; j < mSystemArrays.getCount(); j++) {
			SubSystem curs = mSystemArrays.get(j);
			curs.mEntities.remove(toRemove);
		}
		return toRemove;
	}

	public void clearEntities () {
		mEntities.clear();
		for (int j = 0; j < mSystemArrays.getCount(); j++)
			mSystemArrays.get(j).mEntities.clear();
	}

	/** Register system for receving entities to process
	 * @param system to add */
	public void registerSystem (SubSystem system) {
		mSystemArrays.add(system);
	}

	/** Assigns entities to systems that want to process them */
	public void assignEntities () {
		for (int i = mAssignedCount; i < mAvailibleId; i++) {
			Entity cure = mEntities.get(i);
			for (int j = 0; j < mSystemArrays.getCount(); j++) {
				SubSystem curs = mSystemArrays.get(j);
				if ((cure.mMask & curs.mMask) == curs.mMask) curs.mEntities.add(cure);
			}
		}
		mAssignedCount = mAvailibleId;
	}
}
