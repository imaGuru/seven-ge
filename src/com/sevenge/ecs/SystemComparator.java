
package com.sevenge.ecs;

import java.util.Comparator;

public class SystemComparator implements Comparator<System> {
	@Override
	public int compare (System lhs, System rhs) {
		return lhs.mMask - rhs.mMask;
	}
}
