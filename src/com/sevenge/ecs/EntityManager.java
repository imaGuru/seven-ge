
package com.sevenge.ecs;

public class EntityManager {

	private Entity[] mEntities;
	private Entity[][] mSystemIndex;
	private int[] mReclaimedIndices;
	private int mReclaimedIndicesAvailible;
	private int mId = 0;

	public EntityManager (int size, Entity[][] systemIndex) {
		mEntities = new Entity[size];
		mSystemIndex = systemIndex;
		mReclaimedIndices = new int[(int)(size * 0.4f)];
		mReclaimedIndicesAvailible = 0;
	}

	public void addEntity (Component[] components, int mask) {
		mEntities[mId] = new Entity(mId, components, mask);
		mId++;
	}

	public void removeEntity (int index) {
		mReclaimedIndices[mReclaimedIndicesAvailible] = index;
		mReclaimedIndicesAvailible++;
	}
}
