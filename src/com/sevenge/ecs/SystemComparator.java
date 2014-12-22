
package com.sevenge.ecs;

import java.util.Comparator;

public class SystemComparator implements Comparator<SubSystem> {
	@Override
	public int compare (SubSystem lhs, SubSystem rhs) {
		return lhs.mMask - rhs.mMask;
	}
}
