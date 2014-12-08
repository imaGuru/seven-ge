
package com.sevenge.assets;

public class Texture extends Asset {

	public final int glID;
	public final int width;
	public final int height;

	public Texture (int id, int w, int h) {
		glID = id;
		width = w;
		height = h;
	}
}
