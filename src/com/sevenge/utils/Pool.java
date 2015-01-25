
package com.sevenge.utils;

import java.util.ArrayList;
import java.util.List;
/** Pool class which is used to avoid repetitive calls of garbage collector **/
public class Pool<T> {
	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;

	public Pool (PoolObjectFactory<T> factory, int maxSize) {
		this.factory = factory;
		this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(maxSize);
	}

	public interface PoolObjectFactory<T> {
		public T createObject ();
	}
	/** Returns an instance of T. The instance is taken from the pool or created if there are 
	 * no available objects in the pool. **/
	public T newObject () {
		T object = null;
		if (freeObjects.isEmpty()) {
			object = factory.createObject();
		} else {
			object = freeObjects.remove(freeObjects.size() - 1);
		}
		return object;
	}
	/** Adds the object to the list of free objects in the pool **/
	public void free (T object) {

		if (freeObjects.size() < maxSize) freeObjects.add(object);
	}

}
