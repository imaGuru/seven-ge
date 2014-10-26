
package com.engine.sevenge.graphics;

import com.engine.sevenge.assets.Asset;

public class TextureRegion extends Asset {
	private final float[] uvs;
	private final Texture texture;
	private final int height, width;

	public TextureRegion (int width, int height, int x, int y, Texture texture) {
		uvs = new float[8];
		this.texture = texture;
		this.width = width;
		this.height = height;

		int texWidth = texture.getWidth();
		int texHeight = texture.getHeight();
		float xMin = (float)x / texWidth, xMax = (float)(x + width) / texWidth;
		float yMin = (float)y / texHeight, yMax = (float)(y + height) / texHeight;

		uvs[0] = xMin;
		uvs[1] = yMin;
		uvs[2] = xMin;
		uvs[3] = yMax;
		uvs[4] = xMax;
		uvs[5] = yMax;
		uvs[6] = xMax;
		uvs[7] = yMin;
	}

	public Texture getTexture () {
		return texture;
	}

	public float[] getUVs () {
		return uvs;
	}

	public int getHeight () {
		return height;
	}

	public int getWidth () {
		return width;
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}
}
