
package com.sevenge.ecs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsSystem extends SubSystem {

	static final float BOX_STEP = 1 / 30f;
	static final int BOX_VELOCITY_ITERATIONS = 6;
	static final int BOX_POSITION_ITERATIONS = 2;
	public static final float WORLD_TO_BOX = 1 / 30f;
	public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;

	private static final Vector2 gravityVector = new Vector2(0, 0);

	private World world;

	public PhysicsSystem (int size) {
		super(PhysicsComponent.MASK | PositionComponent.MASK, size);

		this.world = new World(gravityVector, true);

	}

	public World getWorld () {
		return this.world;
	}

	@Override
	public void process () {

		world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

		for (int j = 0; j < mEntities.getCount(); j++) {
			Entity entity = mEntities.get(j);
			PositionComponent positionComponent = (PositionComponent)entity.mComponents[0];
			PhysicsComponent physicsComponent = (PhysicsComponent)entity.mComponents[4];

			positionComponent.x = BOX_TO_WORLD * physicsComponent.getBody().getPosition().x;
			positionComponent.y = BOX_TO_WORLD * physicsComponent.getBody().getPosition().y;
			positionComponent.rotation = (float)-Math.toDegrees(physicsComponent.getBody().getAngle());

		}
	}
}
