package com.engine.sevenge.graphics;

import android.graphics.PointF;
import android.graphics.RectF;

import com.engine.sevenge.assets.Asset;

public class Sprite extends Asset {
	private float angle;
	private float scale;
	private RectF base;
	private PointF translation;
	private Texture2D texture;
	private TextureShaderProgram spriteShader;
	float[] uvs;

	public Sprite(float w, float h, float[] uvCoords, Texture2D tex,
			TextureShaderProgram ss) {
		base = new RectF(-w / 2, h / 2, w / 2, -h / 2);
		translation = new PointF(0, 0);
		scale = 1f;
		angle = 0f;
		uvs = uvCoords;
		texture = tex;
		spriteShader = ss;
	}

	public Texture2D getTexture2D() {
		return texture;
	}

	public TextureShaderProgram getTextureShaderProgram() {
		return spriteShader;
	}

	public void setUVs(float[] uvCoords) {
		uvs = uvCoords;
	}

	public void translate(float x, float y) {
		// Update our location.
		translation.x = x;
		translation.y = y;
	}

	public void scale(float s) {
		scale = s;
	}

	public void rotate(float a) {
		angle = a;
	}

	public float[] getTransformedVertices() {
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

		return new float[] { one.x, one.y, 0.0f, uvs[0], uvs[1], two.x, two.y,
				0.0f, uvs[2], uvs[3], three.x, three.y, 0.0f, uvs[4], uvs[5],
				four.x, four.y, 0.0f, uvs[6], uvs[7] };
	}

	@Override
	public void dispose() {

	}
}