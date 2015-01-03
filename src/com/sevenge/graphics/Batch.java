
package com.sevenge.graphics;

public abstract class Batch {

	abstract void draw (float[] viewProjectionMatrix);

	void add (float[] vertexData) {

	}

	void add (float[] vertexData, int nVertices) {

	}

	abstract void clear ();

	abstract void release ();

	abstract int getRemaining ();

}
