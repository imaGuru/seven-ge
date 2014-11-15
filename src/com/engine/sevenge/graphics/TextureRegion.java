
package com.engine.sevenge.graphics;

import com.engine.sevenge.assets.Asset;

public class TextureRegion extends Asset {
	public final float[] uvs, v;
	public final int height, width;
	public final Texture texture;

	public TextureRegion (int width, int height, int x, int y, Texture texture) {
		uvs = new float[8];
		v = new float[8];
		this.width = width;
		this.height = height;
		this.texture = texture;
		int hw = width / 2, hh = height / 2;

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

		v[0] = hw;
		v[1] = hh;
		v[2] = -hw;
		v[3] = hh;
		v[4] = -hw;
		v[5] = -hh;
		v[6] = hw;
		v[7] = -hh;
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}
}
