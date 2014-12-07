
package com.sevenge.ecs;


public class Entity {
	public int id;
	public int mask;
	public Component[] components;

	Entity (int id, Component[] compArray) {
		this.id = id;
		mask = 0;
		components = compArray;
	}

	Entity (int id, Component[] compArray, int mask) {
		this.id = id;
		this.mask = mask;
		components = compArray;
	}

	public void addComponent (Component comp, int index) {
		components[index] = comp;
		mask |= (int)Math.pow(2, index);
	}

	public void removeComonent (int index) {
		components[index] = null;
		mask -= (int)Math.pow(2, index);
	}
}
