
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import android.opengl.Matrix;

import com.sevenge.SevenGE;
import com.sevenge.assets.TextureRegion;

/** Class responsible for creating sprite batches and puting sprites in the correct batches for fast and easy drawing */
public class SpriteBatcher {
	private SpriteBatch[] batches;
	private int usedBatches;
	private int currentGlid = -1;

	private float[] uvs;
	private float[] v;
	private float[] transform = new float[16];
	private float[] r = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];

	/** Creates a new spriteBatcher
	 * @param size number of batches
	 * @param batchSizeHint hint number of sprites in a batch */
	public SpriteBatcher (int sizeHint, int batchSizeHint) {
		usedBatches = -1;
		batches = new SpriteBatch[sizeHint];
		TextureShaderProgram tsp = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		for (int i = 0; i < sizeHint; i++) {
			batches[i] = new SpriteBatch(batchSizeHint);
			batches[i].program = tsp;
		}
	}

	/** Add a sprite to the correct batch for drawing
	 * @param vdata sprite vertex and uv data
	 * @param tex texture to be used for this sprite */
	public void addSprite (float[] vdata, int tex) {
		if (tex == currentGlid)
			batches[usedBatches].add(vdata);
		else {
			usedBatches++;
			batches[usedBatches].texture = tex;
			batches[usedBatches].add(vdata);
			currentGlid = tex;
		}
	}

	/** Draw the created batches using specified view projection matrix
	 * @param vpm view projection matrix */
	public void draw (float[] vpm) {
		glClear(GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < usedBatches + 1; i++) {
			batches[i].draw(vpm);
		}
	}

	/** Removes content of all batches */
	public void clear () {
		for (int i = 0; i < usedBatches + 1; i++) {
			batches[i].clear();
		}
		usedBatches = -1;
		currentGlid = -1;
	}

	public void addSprite (float[] transform, TextureRegion sprite) {
		uvs = sprite.UVs;
		v = sprite.vertices;

		for (int i = 0; i < 4; i++) {
			temp[0] = v[i * 2];
			temp[1] = v[i * 2 + 1];
			temp[2] = 0;
			temp[3] = 1;
			// Matrix.multiplyMV(tempr, 0, cp.scaleMatrix, 0, temp, 0);
			Matrix.multiplyMV(tempr, 0, transform, 0, temp, 0);
			r[i * 2] = tempr[0];
			r[i * 2 + 1] = tempr[1];
		}

		t[0] = r[0];
		t[1] = r[1];
		t[2] = uvs[0];
		t[3] = uvs[1];
		t[4] = r[2];
		t[5] = r[3];
		t[6] = uvs[2];
		t[7] = uvs[3];
		t[8] = r[4];
		t[9] = r[5];
		t[10] = uvs[4];
		t[11] = uvs[5];
		t[12] = r[6];
		t[13] = r[7];
		t[14] = uvs[6];
		t[15] = uvs[7];

		addSprite(t, sprite.texture);

	}

	public void addSprite (float x, float y, TextureRegion sprite) {
		uvs = sprite.UVs;
		v = sprite.vertices;

		Matrix.setIdentityM(transform, 0);
		Matrix.translateM(transform, 0, x, y, 0f);
		for (int i = 0; i < 4; i++) {
			temp[0] = v[i * 2];
			temp[1] = v[i * 2 + 1];
			temp[2] = 0;
			temp[3] = 1;
			// Matrix.multiplyMV(tempr, 0, cp.scaleMatrix, 0, temp, 0);
			Matrix.multiplyMV(tempr, 0, transform, 0, temp, 0);
			r[i * 2] = tempr[0];
			r[i * 2 + 1] = tempr[1];
		}

		t[0] = r[0];
		t[1] = r[1];
		t[2] = uvs[0];
		t[3] = uvs[1];
		t[4] = r[2];
		t[5] = r[3];
		t[6] = uvs[2];
		t[7] = uvs[3];
		t[8] = r[4];
		t[9] = r[5];
		t[10] = uvs[4];
		t[11] = uvs[5];
		t[12] = r[6];
		t[13] = r[7];
		t[14] = uvs[6];
		t[15] = uvs[7];

		addSprite(t, sprite.texture);

	}

	/** Adds the sprite in the specified world x, y location and with specified rotation and scale
	 * @param x world coordinate
	 * @param y world coordinate
	 * @param rotation of the sprite
	 * @param scale of the sprite
	 * @param sprite to draw */
	public void addSprite (float x, float y, float rotation, float scale, TextureRegion sprite) {
		uvs = sprite.UVs;
		v = sprite.vertices;

		Matrix.setIdentityM(transform, 0);
		Matrix.translateM(transform, 0, x, y, 0f);
		Matrix.rotateM(transform, 0, rotation, 0f, 0f, -1.0f);
		Matrix.scaleM(transform, 0, scale, scale, 1);
		for (int i = 0; i < 4; i++) {
			temp[0] = v[i * 2];
			temp[1] = v[i * 2 + 1];
			temp[2] = 0;
			temp[3] = 1;
			// Matrix.multiplyMV(tempr, 0, cp.scaleMatrix, 0, temp, 0);
			Matrix.multiplyMV(tempr, 0, transform, 0, temp, 0);
			r[i * 2] = tempr[0];
			r[i * 2 + 1] = tempr[1];
		}

		t[0] = r[0];
		t[1] = r[1];
		t[2] = uvs[0];
		t[3] = uvs[1];
		t[4] = r[2];
		t[5] = r[3];
		t[6] = uvs[2];
		t[7] = uvs[3];
		t[8] = r[4];
		t[9] = r[5];
		t[10] = uvs[4];
		t[11] = uvs[5];
		t[12] = r[6];
		t[13] = r[7];
		t[14] = uvs[6];
		t[15] = uvs[7];

		addSprite(t, sprite.texture);
	}
}
