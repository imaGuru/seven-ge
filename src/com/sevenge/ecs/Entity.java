
package com.sevenge.ecs;

public class Entity {
	public int mId;
	public int mMask;
	public Component[] mComponents;

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

	public void addComponent (Component comp, int index) {
		mComponents[index] = comp;
		mMask |= (int)Math.pow(2, index);
	}

	public void removeComonent (int index) {
		mComponents[index] = null;
		mMask -= (int)Math.pow(2, index);
	}
}
