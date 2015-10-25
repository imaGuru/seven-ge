
package com.sevenge.ecs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Class representing an entity according to the ECS pattern. */
public class Entity {
	public int mId;
	/** mask containing information about linked components **/
	public int mMask;
	public Component[] mComponents;
	public Entity parent;
	public List<Entity> children = new ArrayList<Entity>();
	public boolean isRelative = false;
	public float relativeX;
	public float relativeY;
	public float relativeRotation;

	public Entity () {

	}

	/** Creates entity with specified id and size of components array
	 * @param id of this entity
	 * @param maxComponents size of the Components array */
	Entity (int id, int maxComponents) {
		mId = id;
		mMask = 0;
		mComponents = new Component[maxComponents];
	}

	/** Creates entity with specified id and size of components array
	 * @param id of this entity
	 * @param compArray array with Components
	 * @param mask matching the contents of Components Array */
	Entity (int id, Component[] componentsArray, int mask) {
		mId = id;
		mMask = mask;
		mComponents = componentsArray;
	}

	/** Attaches an entity to this one
	 * @param child entity */
	public void addChild (Entity child) {
		this.children.add(child);
		child.parent = this;
	}

	/** Inserts a component at specified index in the Components array
	 * @param component to be inserted
	 * @param index in the Components array */
	public void addComponent (Component component, int index) {
		mComponents[index] = component;
		mMask |= (int)Math.pow(2, index);
	}

	/** Removes component at the specified index from entity.
	 * @param index of component to be removed */
	public void removeComonent (int index) {
		mComponents[index] = null;
		mMask -= (int)Math.pow(2, index);
	}

	/** Comparator sorting entities by layers and textures. Used for better batching performance */
	public static Comparator<Entity> SortByLayerAndTexture = new Comparator<Entity>() {

		public int compare (Entity e1, Entity e2) {

			PositionComponent cp1 = (PositionComponent)e1.mComponents[0];
			SpriteComponent cs1 = (SpriteComponent)e1.mComponents[1];

			PositionComponent cp2 = (PositionComponent)e2.mComponents[0];
			SpriteComponent cs2 = (SpriteComponent)e2.mComponents[1];

			int ret = cp1.layer - cp2.layer;
			if (ret != 0)
				return ret;
			else
				return cs1.textureRegion.texture - cs2.textureRegion.texture;
		}

	};

	/** Sorts entities by their ids */
	public static Comparator<Entity> SortByID = new Comparator<Entity>() {
		@Override
		public int compare (Entity lhs, Entity rhs) {
			return lhs.mId - rhs.mId;
		}
	};
}
