package com.engine.sevenge.graphics;

import android.graphics.PointF;
import android.graphics.RectF;

public class Sprite {
	private float angle;
	private float scale;
	private SubTexture2D subTexture;
	private final RectF base;
	private final PointF translation;
	private final float[] verticesData;

	public Sprite(SubTexture2D subTex) {
		base = new RectF(-subTex.getWidth() / 2, subTex.getHeight() / 2,
				subTex.getWidth() / 2, -subTex.getHeight() / 2);
		translation = new PointF(0, 0);
		scale = 1f;
		angle = 0f;
		verticesData = new float[16];
		subTexture = subTex;
	}

	public Texture2D getTexture2D() {
		return subTexture.getTexture();
	}

	public void setSubTexture(SubTexture2D subTex) {
		subTexture = subTex;
	}

	public void translate(float x, float y) {
		translation.x = x;
		translation.y = y;
	}

	public void scale(float s) {
		scale = s;
	}

	public void rotate(float a) {
		angle = a;
	}

	public final float[] getTransformedVertices() {
		float x1 = base.left * scale;
		float x2 = base.right * scale;
		float y1 = base.bottom * scale;
		float y2 = base.top * scale;

		PointF one = new PointF(x1, y2);
		PointF two = new PointF(x1, y1);
		PointF three = new PointF(x2, y1);
		PointF four = new PointF(x2, y2);

		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);

		one.x = x1 * c - y2 * s;
		one.y = x1 * s + y2 * c;
		two.x = x1 * c - y1 * s;
		two.y = x1 * s + y1 * c;
		three.x = x2 * c - y1 * s;
		three.y = x2 * s + y1 * c;
		four.x = x2 * c - y2 * s;
		four.y = x2 * s + y2 * c;

		one.x += translation.x;
		one.y += translation.y;
		two.x += translation.x;
		two.y += translation.y;
		three.x += translation.x;
		three.y += translation.y;
		four.x += translation.x;
		four.y += translation.y;

		float[] uvs = subTexture.getUVs();

		verticesData[0] = one.x;
		verticesData[1] = one.y;
		verticesData[2] = uvs[0];
		verticesData[3] = uvs[1];

		verticesData[4] = two.x;
		verticesData[5] = two.y;
		verticesData[6] = uvs[2];
		verticesData[7] = uvs[3];

		verticesData[8] = three.x;
		verticesData[9] = three.y;
		verticesData[10] = uvs[4];
		verticesData[11] = uvs[5];

		verticesData[12] = four.x;
		verticesData[13] = four.y;
		verticesData[14] = uvs[6];
		verticesData[15] = uvs[7];

		return verticesData;
	}
}