
package com.sevenge.utils;
/** Quadtree implementation **/
public class Quadtree<T> {
	private final int MAX_OBJECTS;
	private final int MAX_LEVELS;

	private int level;
	private FixedSizeArray<T> objects;
	private FixedSizeArray<AABB> objectAABBs;
	private AABB bounds;
	private Quadtree<T>[] nodes;
	private boolean[] indices;

	/*
	 * Constructor
	 */
	@SuppressWarnings("unchecked")
	public Quadtree (int pLevel, AABB pBounds, int maxObjects, int maxLevels) {
		level = pLevel;
		MAX_LEVELS = maxLevels;
		MAX_OBJECTS = maxObjects;
		objects = new FixedSizeArray<T>(MAX_OBJECTS, null);
		bounds = pBounds;
		nodes = (Quadtree<T>[])new Object[4];
		indices = new boolean[4];
	}

	/*
	 * Clears the quadtree
	 */
	public void clear () {
		objects.clear();
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = null;
		}
	}

	/*
	 * Splits the node into 4 subnodes
	 */
	private void split () {
		float subWidth = bounds.width / 2;
		float subHeight = bounds.height / 2;
		float x = bounds.x;
		float y = bounds.y;

		nodes[0] = new Quadtree<T>(level + 1, new AABB(x + subWidth, y + subHeight, subWidth, subHeight), MAX_OBJECTS, MAX_LEVELS);
		nodes[1] = new Quadtree<T>(level + 1, new AABB(x, y + subHeight, subWidth, subHeight), MAX_OBJECTS, MAX_LEVELS);
		nodes[2] = new Quadtree<T>(level + 1, new AABB(x, y, subWidth, subHeight), MAX_OBJECTS, MAX_LEVELS);
		nodes[3] = new Quadtree<T>(level + 1, new AABB(x + subWidth, y, subWidth, subHeight), MAX_OBJECTS, MAX_LEVELS);
	}

	/*
	 * Determine which node the object belongs to. -1 means object cannot completely fit within a child node and is part of the
	 * parent node
	 */
	private boolean[] getIndices (AABB pRect) {

		indices[0] = false;
		indices[1] = false;
		indices[2] = false;
		indices[3] = false;

		double verticalMidpoint = bounds.x + (bounds.width / 2);
		double horizontalMidpoint = bounds.y + (bounds.height / 2);

		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (pRect.y < horizontalMidpoint && pRect.y + pRect.height < horizontalMidpoint);
		// Object can completely fit within the top quadrants
		boolean topQuadrant = (pRect.y > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (pRect.x < verticalMidpoint && pRect.x + pRect.width < verticalMidpoint) {
			if (topQuadrant) {
				indices[1] = true;
			} else if (bottomQuadrant) {
				indices[2] = true;
			} else {
				indices[1] = true;
				indices[2] = true;
			}

		}
		// Object can completely fit within the right quadrants
		else if (pRect.x > verticalMidpoint) {
			if (topQuadrant) {
				indices[0] = true;
			} else if (bottomQuadrant) {
				indices[3] = true;
			} else {
				indices[0] = true;
				indices[3] = true;
			}
		}

		return indices;
	}

	/*
	 * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their
	 * corresponding nodes.
	 */
	public void insert (T object, AABB pRect) {
		if (nodes[0] != null) {
			getIndices(pRect);
			for (int i = 0; i < 4; i++) {
				if (indices[i]) {
					nodes[i].insert(object, pRect);
				}
			}
		}

		objects.add(object);
		if (objects.getCount() > MAX_OBJECTS && level < MAX_LEVELS) {
			if (nodes[0] == null) {
				split();
			}

			int i = 0;
			while (i < objects.getCount()) {
				getIndices(objectAABBs.get(i));
				for (int j = 0; j < 4; j++) {
					if (indices[j]) {
						objectAABBs.swapWithLast(i);
						objects.swapWithLast(i);
						nodes[j].insert(objects.removeLast(), objectAABBs.removeLast());
					}
				}
			}
		}
	}

	/*
	 * Return all objects that could collide with the given object
	 */
	/*
	 * public FixedSizeArray<T> query (AABB pRect) { int index = getIndex(pRect); if (index != -1 && nodes[0] != null) {
	 * nodes[index].retrieve(pRect); }
	 * 
	 * returnObjects.addAll(objects);
	 * 
	 * return returnObjects; }
	 */
}
