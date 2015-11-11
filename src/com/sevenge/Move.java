package com.sevenge;

import com.badlogic.gdx.math.Vector2;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.PhysicsComponent;
import com.sevenge.ecs.PositionComponent;

public class Move extends Action {

	private Vector2 target;
	private Entity entity;
	private int counter = 0;
	private final float WORLD_TO_BOX = 1 / 30f;
	private final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
	private float maxForce = 1000;
	private float maxSpeed = 10;
	private float slowingRadius = 400;
	private float distance;

	public Move(Entity entity, Vector2 target) {

		this.target = target.cpy();
		this.entity = entity;

	}

	@Override
	public boolean execute() {

		Vector2 tar = target.cpy();

		PhysicsComponent phiC = (PhysicsComponent) entity.mComponents[4];
		PositionComponent posC = (PositionComponent) entity.mComponents[0];

		Vector2 pos = phiC.getBody().getPosition().cpy();
		Vector2 vel = phiC.getBody().getLinearVelocity().cpy();

		pos.mul(BOX_TO_WORLD);
		Vector2 tarcpy = tar.cpy();

		tarcpy.sub(pos);
		tarcpy.mul(WORLD_TO_BOX);

		Vector2 desired_velocity = tarcpy.nor().mul(maxSpeed).cpy();
		Vector2 temp1 = tar.cpy();

		distance = temp1.sub(pos).len();
		if (distance < 2 && vel.len() < 10) {
			phiC.getBody().setLinearVelocity(new Vector2(0, 0));
			return true;
		}

		if (distance < slowingRadius) {
			// Inside the slowing area
			// Log.d("move", "slowing dist = " + distance);
			Vector2 temp = desired_velocity.cpy();
			desired_velocity = temp.nor().mul(maxSpeed)
					.mul(distance / slowingRadius).cpy();
		} else {
			// Log.d("move", "not slowing dist = " + distance);
			// Outside the slowing area.
			Vector2 temp = desired_velocity.cpy();
			desired_velocity = temp.nor().mul(maxSpeed).cpy();
		}

		Vector2 steering = desired_velocity.cpy();
		steering.sub(vel);

		if (steering.len() > maxForce) {
			float temp = maxForce / steering.len();
			steering.mul(temp);
		}
		if ((vel.add(steering)).len() > maxSpeed) {
			float temp = maxSpeed / (vel.add(steering)).len();
			(vel.add(steering)).mul(temp);
		} else {
			vel.add(steering);
		}
		phiC.getBody().applyLinearImpulse(vel, phiC.getBody().getPosition());
		return false;
	}
}
