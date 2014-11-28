
package com.engine.sevenge.assets;


/** Responsible for creating vertex and uv data for some region of the specified texture */
public class TextureRegion extends Asset {
	public final float[] UVs, vertices;
	public final int height, width;
	public int texture;

	/** Creates a texture region vertex data
	 * @param width of the region
	 * @param height of the region
	 * @param x position of the region in the texture
	 * @param y position of the region in the texture
	 * @param texture we want the region from */
	public TextureRegion (int width, int height, int x, int y, Texture texture) {
		UVs = new float[8];
		vertices = new float[8];
		this.width = width;
		this.height = height;
		int hw = width / 2, hh = height / 2;
		this.texture = texture.glID;

		float xMin = (float)x / texture.width, xMax = (float)(x + width) / texture.width;
		float yMin = (float)y / texture.height, yMax = (float)(y + height) / texture.height;

		UVs[0] = xMin;
		UVs[1] = yMin;
		UVs[2] = xMin;
		UVs[3] = yMax;
		UVs[4] = xMax;
		UVs[5] = yMax;
		UVs[6] = xMax;
		UVs[7] = yMin;

		vertices[0] = hw;
		vertices[1] = hh;
		vertices[2] = -hw;
		vertices[3] = hh;
		vertices[4] = -hw;
		vertices[5] = -hh;
		vertices[6] = hw;
		vertices[7] = -hh;
	}
}
