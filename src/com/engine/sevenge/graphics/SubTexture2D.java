
package com.engine.sevenge.graphics;

import com.engine.sevenge.assets.Asset;

public class SubTexture2D extends Asset {
	private final float[] uvs;
	private final String name;
	private final Texture2D texture;
	private final int height, width;

	public SubTexture2D (String name, int width, int height, int x, int y, Texture2D texture) {
		uvs = new float[8];
		this.name = name;
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

	public String getName () {
		return name;
	}

	public Texture2D getTexture () {
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
