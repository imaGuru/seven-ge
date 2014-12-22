
package com.sevenge.ecs;

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
}
