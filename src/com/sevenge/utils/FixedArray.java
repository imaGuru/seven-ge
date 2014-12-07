
package com.sevenge.utils;

import java.util.Comparator;

public class FixedArray<T> {

	private final T[] mArray;
	private int mCount;
	private Comparator<T> mComparator;

	@SuppressWarnings("unchecked")
	public FixedArray (int size, Comparator<T> comparator) {
		super();
		mArray = (T[])new Object[size];
		mCount = 0;
		mComparator = comparator;
	}

	public final void add (T object) {
		if (mCount < mArray.length) {
			mArray[mCount] = object;
			mCount++;
		}
	}

	public final void remove (T object) {
		if (mCount < mArray.length) {
			mArray[mCount] = object;
			mCount++;
		}
	}

	public void remove (int index) {
		if (index < mCount) {
			for (int x = index; x < mCount; x++) {
				if (x + 1 < mArray.length && x + 1 < mCount) {
					mArray[x] = mArray[x + 1];
				} else {
					mArray[x] = null;
				}
			}
			mCount--;
		}
	}

	public void clear () {
		for (int x = 0; x < mCount; x++) {
			mArray[x] = null;
		}
		mCount = 0;
	}

	public T get (int index) {
		T result = null;
		if (index < mCount && index >= 0) {
			result = mArray[index];
		}
		return result;
	}

	public int getCount () {
		return mCount;
	}

	public int getCapacity () {
		return mArray.length;
	}

	public int find (T object, boolean ignoreComparator) {
		int index = -1;
		final int count = mCount;
		final Comparator<T> comparator = mComparator;
		final T[] contents = mArray;
		if (comparator != null && !ignoreComparator) {
			for (int x = 0; x < count; x++) {
				final int result = comparator.compare(contents[x], object);
				if (result == 0) {
					index = x;
					break;
				}
			}
		} else {
			for (int x = 0; x < count; x++) {
				if (contents[x] == object) {
					index = x;
					break;
				}
			}
		}
		return index;
	}

}
