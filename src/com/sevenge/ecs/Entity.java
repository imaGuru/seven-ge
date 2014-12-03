
package com.sevenge.ecs;

import java.util.HashMap;

public class Entity {
	private static int idc = 0;
	public int id;
	public int mask;
	public HashMap<Integer, Component> components;

	public Entity () {
		id = idc++;
		mask = 0;
		components = new HashMap<Integer, Component>();
	}

	public void add (Component comp, int type) {
		components.put(type, comp);
		mask |= type;
	}
}
