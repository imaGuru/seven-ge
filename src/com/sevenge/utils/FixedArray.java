
package com.sevenge.utils;

import java.util.Comparator;

public class FixedArray<T> {

	private final T[] mArray;
	private int mCount;
	private Comparator<T> mComparator;

	/** Creates gameloop friendly collection without costly allocations
	 * @param size of the container
	 * @param comparator for finding and sorting elements */
	@SuppressWarnings("unchecked")
	public FixedArray (int size, Comparator<T> comparator) {
		super();
		mArray = (T[])new Object[size];
		mCount = 0;
		mComparator = comparator;
	}

	/** Adds element to collection
	 * @param object to add */
	public final void add (T object) {
		if (mCount < mArray.length) {
			mArray[mCount] = object;
			mCount++;
		}
	}

	/** Removes element from collection and shifts remaining elements to fill the gap
	 * @param object to remove */
	public final void remove (T object) {
		// changeme
		if (mCount < mArray.length) {
			mArray[mCount] = object;
			mCount++;
		}
	}

	/** Removes element with specified index from collection and shifts remaining elements to fill the gap
	 * @param index of the object to remove */
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

	/** Removes all elements from this collection */
	public void clear () {
		for (int x = 0; x < mCount; x++) {
			mArray[x] = null;
		}
		mCount = 0;
	}

	/** Retrieves element with given index
	 * @param index of the object to retrieve
	 * @return stored object */
	public T get (int index) {
		T result = null;
		if (index < mCount && index >= 0) {
			result = mArray[index];
		}
		return result;
	}

	/** Returns the current size of the collection
	 * @return count of elements contained */
	public int getCount () {
		return mCount;
	}

	/** Returns the capacity of this collection
	 * @return maximum count of elements that can be store in this array */
	public int getCapacity () {
		return mArray.length;
	}

	/** Finds the specified object in the array
	 * @param object to find
	 * @param ignoreComparator whether to use comparator or not
	 * @return index of the object if found and -1 otherwise */
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
