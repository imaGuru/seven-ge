
package com.sevenge.utils;

import java.util.Arrays;
import java.util.Comparator;

public class FixedSizeArray<T> {

	private final T[] mArray;
	private int mCount;
	private Comparator<T> mComparator;
	private boolean mSorted;

	/** Creates gameloop friendly collection without costly allocations
	 * @param size of the container
	 * @param comparator for finding and sorting elements */
	@SuppressWarnings("unchecked")
	public FixedSizeArray (int size, Comparator<T> comparator) {
		super();
		mArray = (T[])new Object[size];
		mCount = 0;
		mComparator = comparator;
		mSorted = false;
	}

	/** Adds element to collection
	 * @param object to add */
	public final void add (T object) {
		if (mCount < mArray.length) {
			mArray[mCount] = object;
			mCount++;
			mSorted = false;
		}
	}

	/** Removes element from collection and shifts remaining elements to fill the gap
	 * @param object to remove */
	public final boolean remove (T object) {
		int id = find(object, false);
		if (id != -1) {
			remove(id);
			return true;
		}
		return false;
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

	/** Removes the last element in the array and returns it. This method is faster than calling remove(count -1);
	 * @return The contents of the last element in the array. */
	public T removeLast () {
		T object = null;
		if (mCount > 0) {
			object = mArray[mCount - 1];
			mArray[mCount - 1] = null;
			mCount--;
		}
		return object;
	}

	/** Swaps the element at the passed index with the element at the end of the array. When followed by removeLast(), this is
	 * useful for quickly removing array elements. */
	public void swapWithLast (int index) {
		if (mCount > 0 && index < mCount - 1) {
			T object = mArray[mCount - 1];
			mArray[mCount - 1] = mArray[index];
			mArray[index] = object;
			mSorted = false;
		}
	}

	/** Sets the value of a specific index in the array. An object must have already been added to the array at that index for this
	 * command to complete. */
	public void set (int index, T object) {
		if (index < mCount) {
			mArray[index] = object;
		}
	}

	/** Sorts the array. If the array is already sorted, no work will be performed unless the forceResort parameter is set to true.
	 * If a comparator has been specified with setComparator(), it will be used for the sort; otherwise the object's natural
	 * ordering will be used.
	 * @param forceResort If set to true, the array will be resorted even if the order of the objects in the array has not changed
	 *           since the last sort. */
	public void sort (boolean forceResort) {
		if (!mSorted || forceResort) {
			if (mComparator != null) {
				Arrays.sort(mArray, 0, mCount, mComparator);
			} else {
				DebugLog.d("FixedSizeArray", "No comparator specified for this type, using Arrays.sort().");
				Arrays.sort(mArray, 0, mCount);
			}
			mSorted = true;
		}
	}

	public void setComparator (Comparator<T> comp) {
		mSorted = false;
		mComparator = comp;
	}
}
