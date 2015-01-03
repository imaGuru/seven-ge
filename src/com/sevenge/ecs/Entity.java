
package com.sevenge.ecs;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class Entity {
	public int mId;
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

	Entity (int id, int maxComponents) {
		this.mId = id;
		mMask = 0;
		mComponents = new Component[maxComponents];
	}

	Entity (int id, Component[] compArray, int mask) {
		this.mId = id;
		this.mMask = mask;
		mComponents = compArray;
	}

	public void addChild (Entity child) {
		this.children.add(child);
		child.parent = this;
	}

	public void addComponent (Component comp, int index) {
		mComponents[index] = comp;
		mMask |= (int)Math.pow(2, index);
	}

	public void removeComonent (int index) {
		mComponents[index] = null;
		mMask -= (int)Math.pow(2, index);
	}

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
}
