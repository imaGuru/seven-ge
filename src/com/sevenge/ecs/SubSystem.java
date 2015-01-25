
package com.sevenge.ecs;

import java.util.Comparator;

import com.sevenge.utils.FixedSizeArray;
/**
 * SubSystem is abstract class based on which are created additional systems for example
 * PositionSystem, PhysicsSystem.
 */
public abstract class SubSystem {

	protected int mMask;

	protected FixedSizeArray<Entity> mEntities;

	public SubSystem (int mask, int size) {
		mMask = mask;
		mEntities = new FixedSizeArray<Entity>(size, Entity.SortByID);
		mEntities.setFinder(Entity.SortByID);
	}

	public void process () {

	}

	public static Comparator<SubSystem> SortByMASK = new Comparator<SubSystem>() {
		@Override
		public int compare (SubSystem lhs, SubSystem rhs) {
			return lhs.mMask - rhs.mMask;
		}
	};
}
