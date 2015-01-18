
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

	/** Creates a new sprite with given TextureRegion
	 * @param tex textureRegion to be used for this sprite */
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

	/** Creates a sprite with the given texture
	 * @param tex texture to be used for this sprite */
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

	/** Sets the position of this sprite
	 * @param x new x coordinate
	 * @param y new y coordinate */
	public void setPosition (float x, float y) {
		translate(x - this.x, y - this.y);
	}

	/** Sets the center of this sprite at x,y
	 * @param x new x coordinate of the center
	 * @param y new y coordinate of the center */
	public void setCenter (float x, float y) {
		translate((x - width / 2) - this.x, (y - height / 2) - this.y);
	}

	/** Translates the position of this sprite by xdist,ydist
	 * @param xdist amount to add to x
	 * @param ydist amount to add to y */
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

	/** Translates the position of this sprite by xdist,ydist
	 * @param xdist amount to add to x */
	public void translateX (float xdist) {
		this.x += xdist;
		if (changed == true) return;
		vdata[0] += xdist;
		vdata[4] += xdist;
		vdata[8] += xdist;
		vdata[12] += xdist;
	}

	/** Translates the position of this sprite by xdist,ydist
	 * @param ydist amount to add to y */
	public void translateY (float ydist) {
		this.y += ydist;
		if (changed == true) return;
		vdata[1] += ydist;
		vdata[5] += ydist;
		vdata[9] += ydist;
		vdata[13] += ydist;
	}

	/** Sets the rotation of this sprite to angle
	 * @param angle in degrees */
	public void setRotation (float angle) {
		this.rotation = angle;
		changed = true;
	}

	/** Rotates this sprite by angleAmount
	 * @param angleAmount in degrees */
	public void rotate (float angleAmount) {
		rotation += angleAmount;
		changed = true;
	}

	/** Sets the scale of this sprite to scaleX,scaleY
	 * @param scaleX amount to scale in X
	 * @param scaleY amount to scale in Y */
	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		changed = true;
	}

	/** Adds scaleX, scaleY to the current scaling factors
	 * @param scaleX amount to add to scaleX
	 * @param scaleY amount to add to scaleY */
	public void scale (float scaleX, float scaleY) {
		this.scaleX += scaleX;
		this.scaleY += scaleY;
		changed = true;
	}

	/** Adds scaleX to the current scaling factor on x
	 * @param scaleX */
	public void scaleX (float scaleX) {
		this.scaleX += scaleX;
		changed = true;
	}

	/** Adds scaleY to the current scaling factor on y
	 * @param scaleY */
	public void scaleY (float scaleY) {
		this.scaleY += scaleY;
		changed = true;
	}

	/** Returns current scaling factor on x
	 * @return scaling factor */
	public float getScaleX () {
		return scaleX;
	}

	/** Returns current scaling factor on y
	 * @return scaling factor */
	public float getScaleY () {
		return scaleY;
	}

	/** Returns the current rotation
	 * @return angle in degrees */
	public float getRotation () {
		return rotation;
	}

	/** Gets the x coordinate of the left bottom corner of this sprite
	 * @return x coordinate */
	public float getX () {
		return x;
	}

	/** Gets the y coordinate of the left bottom corner of this sprite
	 * @return y coordinate */
	public float getY () {
		return y;
	}

	/** Returns the computed vertices of the sprite in world coordinates. If no changes to the sprite rotation or scaling were made,
	 * this method will return previously computed value to save cpu time
	 * @return 16 float array with vertex data of the format (x,y,u,v) */
	public float[] getVertices () {
		if (changed) {
			if (rotation != 0) {
				float rotation = (float)Math.toRadians(-this.rotation);
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

	/** Returns the axis aligned bounding box of this sprite. If no changes to the sprite rotation or scaling were made, this method
	 * will return previously computed value to save cpu time
	 * @return AABB of this sprite */
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

	/** Sorts the sprites by texture */
	public static Comparator<Sprite> SortByTexture = new Comparator<Sprite>() {
		@Override
		public int compare (Sprite lhs, Sprite rhs) {
			return lhs.texture - rhs.texture;
		}
	};
}
