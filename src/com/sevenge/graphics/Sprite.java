
package com.sevenge.graphics;

import com.sevenge.assets.Texture;

public class Sprite {
	public float vdata[] = new float[16];
	public int texture;
	public float scaleX;
	public float scaleY;
	public float x;
	public float y;
	public float rotation;
	private boolean scaleChanged;
	private boolean positionChanged;
	private boolean rotationChanged;

	public Sprite (TextureRegion tex) {
		int hw = tex.width / 2;
		int hh = tex.height / 2;
		texture = tex.texture;
		vdata[0] = hw;
		vdata[1] = hh;
		vdata[2] = tex.UVs[0];
		vdata[3] = tex.UVs[1];
		vdata[4] = -hw;
		vdata[5] = hh;
		vdata[6] = tex.UVs[2];
		vdata[7] = tex.UVs[3];
		vdata[8] = -hw;
		vdata[9] = -hh;
		vdata[10] = tex.UVs[4];
		vdata[11] = tex.UVs[5];
		vdata[12] = hw;
		vdata[13] = -hh;
		vdata[14] = tex.UVs[6];
		vdata[15] = tex.UVs[7];
	}

	public Sprite (Texture tex) {

	}

	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		positionChanged = true;
	}

	public void setRotation (float angle) {
		this.rotation = angle;
		rotationChanged = true;
	}

	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		scaleChanged = true;
	}

	public void update () {
		rotation = (float)Math.toRadians(-rotation);
		float cos = (float)Math.cos(rotation);
		float sin = (float)Math.sin(rotation);
		
		if(scaleChanged)
			float scaledX = v[0] * scaleX;
			float scaledY = v[1] * scaleY;
		if(rotationChanged)
			vdata[i * 4] = scaledX * cos - scaledY * sin + x;
			vdata[i * 4 + 1] = vx * sin + vy * cos + y;
			vdata[i * 4 + 2] = uvs[i * 2];
			vdata[i * 4 + 3] = uvs[i * 2 + 1];
		}
	}
}
