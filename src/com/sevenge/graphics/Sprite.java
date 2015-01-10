
package com.sevenge.graphics;

import java.util.Comparator;

import com.sevenge.assets.Texture;
import com.sevenge.utils.AABB;

public class Sprite {
	private final float vdata[] = new float[16];
	public final int texture;
	private float scaleX = 1.0f;
	private float scaleY = 1.0f;
	private float x;
	private float y;
	private float width;
	private float height;
	private float rotation = 0;
	private boolean changed = true;
	private boolean aabbUpdated = false;
	private AABB aabb;

	public Sprite (TextureRegion tex) {
		width = tex.width;
		height = tex.height;
		texture = tex.texture;

		vdata[0] = width;
		vdata[1] = height;
		vdata[2] = tex.UVs[0];
		vdata[3] = tex.UVs[1];

		vdata[4] = 0;
		vdata[5] = height;
		vdata[6] = tex.UVs[2];
		vdata[7] = tex.UVs[3];

		vdata[8] = 0;
		vdata[9] = 0;
		vdata[10] = tex.UVs[4];
		vdata[11] = tex.UVs[5];

		vdata[12] = width;
		vdata[13] = 0;
		vdata[14] = tex.UVs[6];
		vdata[15] = tex.UVs[7];
	}

	public Sprite (Texture tex) {
		texture = tex.glID;
		width = tex.width;
		height = tex.height;

		vdata[0] = width;
		vdata[1] = height;
		vdata[2] = 1.0f;
		vdata[3] = 1.0f;

		vdata[4] = 0.0f;
		vdata[5] = height;
		vdata[6] = 0.0f;
		vdata[7] = 1.0f;

		vdata[8] = 0.0f;
		vdata[9] = 0.0f;
		vdata[10] = 0.0f;
		vdata[11] = 0.0f;

		vdata[12] = width;
		vdata[13] = 0.0f;
		vdata[14] = 1.0f;
		vdata[15] = 0.0f;
	}

	public void setPosition (float x, float y) {
		translate(x - this.x, y - this.y);
	}

	public void setCenter (float x, float y) {
		translate((x - width / 2) - this.x, (y - height / 2) - this.y);
	}

	public void translate (float xdist, float ydist) {
		this.x += xdist;
		this.y += ydist;
		if (changed == true) return;
		vdata[0] += xdist;
		vdata[1] += ydist;

		vdata[4] += xdist;
		vdata[5] += ydist;

		vdata[8] += xdist;
		vdata[9] += ydist;

		vdata[12] += xdist;
		vdata[13] += ydist;
	}

	public void translateX (float xdist) {
		this.x += xdist;
		if (changed == true) return;
		vdata[0] += xdist;
		vdata[4] += xdist;
		vdata[8] += xdist;
		vdata[12] += xdist;
	}

	public void translateY (float ydist) {
		this.y += ydist;
		if (changed == true) return;
		vdata[1] += ydist;
		vdata[5] += ydist;
		vdata[9] += ydist;
		vdata[13] += ydist;
	}

	public void setRotation (float angle) {
		this.rotation = angle;
		changed = true;
	}

	public void rotate (float angleAmmount) {
		rotation += angleAmmount;
		changed = true;
	}

	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		changed = true;
	}

	public void scale (float scaleX, float scaleY) {
		this.scaleX += scaleX;
		this.scaleY += scaleY;
		changed = true;
	}

	public void scaleX (float scaleX) {
		this.scaleX += scaleX;
		changed = true;
	}

	public void scaleY (float scaleY) {
		this.scaleY += scaleY;
		changed = true;
	}

	public float getScaleX () {
		return scaleX;
	}

	public float getScaleY () {
		return scaleY;
	}

	public float getRotation () {
		return rotation;
	}

	public float getX () {
		return x;
	}

	public float getY () {
		return y;
	}

	public float[] getVertices () {
		if (changed) {
			if (rotation != 0) {
				rotation = (float)Math.toRadians(-rotation);
				float cos = (float)Math.cos(rotation);
				float sin = (float)Math.sin(rotation);

				float vx = width * scaleX;
				float vy = height * scaleY;
				vdata[0] = vx * cos - vy * sin + x;
				vdata[1] = vx * sin + vy * cos + y;

				vx = 0;
				vy = height * scaleY;
				vdata[4] = vx * cos - vy * sin + x;
				vdata[5] = vx * sin + vy * cos + y;

				vx = 0;
				vy = 0;
				vdata[8] = vx * cos - vy * sin + x;
				vdata[9] = vx * sin + vy * cos + y;

				vx = width * scaleX;
				vy = 0;
				vdata[12] = vx * cos - vy * sin + x;
				vdata[13] = vx * sin + vy * cos + y;
			} else {
				float vx = width * scaleX;
				float vy = height * scaleY;
				vdata[0] = vx + x;
				vdata[1] = vy + y;

				vx = 0;
				vy = height * scaleY;
				vdata[4] = vx + x;
				vdata[5] = vy + y;

				vx = 0;
				vy = 0;
				vdata[8] = vx + x;
				vdata[9] = vy + y;

				vx = width * scaleX;
				vy = 0;
				vdata[12] = vx + x;
				vdata[13] = vy + y;
			}

			changed = false;
			aabbUpdated = false;
		}
		return vdata;
	}

	public AABB getAxisAlignedBoundingBox () {
		final float[] vertices = getVertices();
		if (!aabbUpdated) {
			float minx = vertices[0];
			float miny = vertices[1];
			float maxx = vertices[0];
			float maxy = vertices[1];

			minx = minx > vertices[4] ? vertices[4] : minx;
			minx = minx > vertices[8] ? vertices[8] : minx;
			minx = minx > vertices[12] ? vertices[12] : minx;

			maxx = maxx < vertices[4] ? vertices[4] : maxx;
			maxx = maxx < vertices[8] ? vertices[8] : maxx;
			maxx = maxx < vertices[12] ? vertices[12] : maxx;

			miny = miny > vertices[5] ? vertices[5] : miny;
			miny = miny > vertices[9] ? vertices[9] : miny;
			miny = miny > vertices[13] ? vertices[13] : miny;

			maxy = maxy < vertices[5] ? vertices[5] : maxy;
			maxy = maxy < vertices[9] ? vertices[9] : maxy;
			maxy = maxy < vertices[13] ? vertices[13] : maxy;
			if (aabb == null) aabb = new AABB();
			aabb.x = minx;
			aabb.y = miny;
			aabb.width = maxx - minx;
			aabb.height = maxy - miny;
			aabbUpdated = true;
		}
		return aabb;
	}

	public static Comparator<Sprite> SortByTexture = new Comparator<Sprite>() {
		@Override
		public int compare (Sprite lhs, Sprite rhs) {
			return lhs.texture - rhs.texture;
		}
	};
}
