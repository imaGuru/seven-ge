
package com.sevenge.sample;

import com.sevenge.ecs.Entity;
import com.sevenge.ecs.PositionComponent;

public class SceneManager {

	private Entity root = new Entity();

	public Entity getRoot () {
		return root;
	}

	public void update () {

		traverse(root);

	}

	private void traverse (Entity entity) {

		if (entity.isRelative) {

			PositionComponent positionComponent = (PositionComponent)entity.mComponents[0];
			PositionComponent parentPositionComponent = (PositionComponent)entity.parent.mComponents[0];

			positionComponent.x = parentPositionComponent.x + entity.relativeX;
			positionComponent.y = parentPositionComponent.y + entity.relativeY;
			positionComponent.rotation = parentPositionComponent.rotation + entity.relativeRotation;

		}

		for (Entity e : entity.children) {

			traverse(e);
		}

	}

}
