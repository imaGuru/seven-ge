
package com.sevenge.ecs;

import java.util.Comparator;

public class EntityComparator implements Comparator<Entity> {
	@Override
	public int compare (Entity lhs, Entity rhs) {
		return lhs.id - rhs.id;
	}
}
