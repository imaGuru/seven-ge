
package com.engine.sevenge.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.sevenge.ecs.Component;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.EntityManager;
import com.sevenge.ecs.SubSystem;

public class EntityManagerTest extends AndroidTestCase {

	private EntityManager mEM;

	public EntityManagerTest () {
		mEM = new EntityManager(20, 5);
	}

	@Override
	public void setUp () {
	}

	@Override
	public void tearDown () {
		mEM.clearEntities();
	}

	public void testCreateEntity () {
		Entity testEntity = mEM.createEntity(5);
		Entity testEntity2 = mEM.removeEntity(testEntity.mId);
		if (testEntity2 == null) Assert.fail("Null entity");
		if (testEntity2.mId != testEntity.mId) Assert.fail("ID mismatch");
	}

	public void testRemoveEntity () {
		Entity[] testEntities = new Entity[] {mEM.createEntity(5), mEM.createEntity(5), mEM.createEntity(5), mEM.createEntity(5),
			mEM.createEntity(5)};
		if (mEM.removeEntity(testEntities[2].mId) == null) Assert.fail("Null entity");
		if (mEM.removeEntity(testEntities[2].mId) != null) Assert.fail("Entity not removed");
		if (mEM.removeEntity(testEntities[1].mId) == null) Assert.fail("Entity should exist");
		if (mEM.removeEntity(testEntities[3].mId) == null) Assert.fail("Entity should exist");
	}

	public void testAssignEntities () {
		int correntNumberOfAssignedEntites = 3;
		TestSystem ts = new TestSystem(4, 10);
		Entity[] testEntities = new Entity[] {mEM.createEntity(new Component[1], 4), mEM.createEntity(new Component[1], 4),
			mEM.createEntity(5), mEM.createEntity(5), mEM.createEntity(new Component[1], 5)};
		mEM.registerSystem(ts);
		mEM.assignEntities();
		if (ts.getCount() != correntNumberOfAssignedEntites) Assert.fail("Incorrectly assigned entites");
		mEM.removeEntity(testEntities[0].mId);
		mEM.assignEntities();
		if (ts.getCount() != correntNumberOfAssignedEntites - 1)
			Assert.fail("Incorrectly assigned entites after remove of system entity");
		mEM.removeEntity(testEntities[2].mId);
		mEM.assignEntities();
		if (ts.getCount() != correntNumberOfAssignedEntites - 1)
			Assert.fail("Incorrectly assigned entites after remove of non system entity");
	}

	class TestSystem extends SubSystem {

		public TestSystem (int mask, int size) {
			super(4, size);
		}

		public int getCount () {
			return mEntities.getCount();
		}
	}
}
