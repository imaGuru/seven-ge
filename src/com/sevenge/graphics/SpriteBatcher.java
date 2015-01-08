
package com.sevenge.graphics;

import com.sevenge.SevenGE;
import com.sevenge.assets.Shader;

/** Class responsible for creating sprite batches and puting sprites in the correct batches for fast and easy drawing */
public class SpriteBatcher {
	private Batch[] mBatches;
	private int mUsedBatches;
	private int mCurrentGlid = -1;
	private float DOUBLEPI = (float)(Math.PI * 2.0f);
	private TextureShaderProgram mTSP;
	private ColorShaderProgram mCSP;
	private SpriteBatchPool spriteBatchPool;
	private PrimitiveBatchPool primitiveBatchPool;

	private float[] uvs;
	private float[] v;
	private float[] transform = new float[16];
	private float[] r = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];

	/** Creates a new spriteBatcher
	 * @param size number of batches
	 * @param batchSizeHint hint number of sprites in a batch */
	public SpriteBatcher (int sizeHint, int batchSizeHint) {
		mUsedBatches = -1;
		mBatches = new Batch[sizeHint];
		mTSP = (TextureShaderProgram)SevenGE.getAssetManager().getAsset("spriteShader");
		Shader v = (Shader)SevenGE.getAssetManager().getAsset("shader3");
		Shader f = (Shader)SevenGE.getAssetManager().getAsset("shader4");
		mCSP = new ColorShaderProgram(v.glID, f.glID);
		spriteBatchPool = new SpriteBatchPool(sizeHint, mTSP, batchSizeHint);
		primitiveBatchPool = new PrimitiveBatchPool(sizeHint, mCSP, batchSizeHint * 2);
	}

	/** Draw the created batches using specified view projection matrix
	 * @param vpm view projection matrix */
	public void flush (float[] vpm) {
		for (int i = 0; i < mUsedBatches + 1; i++) {
			mBatches[i].draw(vpm);
			mBatches[i].clear();
			mBatches[i].release();
		}
		mUsedBatches = -1;
		mCurrentGlid = -1;
	}

	/** Removes content of all batches */
	public void clear () {
		for (int i = 0; i < mUsedBatches + 1; i++) {
			mBatches[i].clear();
		}
		mUsedBatches = -1;
		mCurrentGlid = -1;
	}

	public void drawCircle (float cx, float cy, float r, float rotation, int nsegments, float red, float green, float blue) {
		float[] vertices = new float[nsegments * 5];
		float t, x = (float)Math.cos(-rotation) * r, y = (float)Math.sin(-rotation) * r;
		float c = (float)Math.cos(DOUBLEPI / nsegments);
		float s = (float)Math.sin(DOUBLEPI / nsegments);
		for (short i = 0; i < nsegments; i++) {
			vertices[i * 5] = x + cx;
			vertices[i * 5 + 1] = y + cy;
			vertices[i * 5 + 2] = red;
			vertices[i * 5 + 3] = green;
			vertices[i * 5 + 4] = blue;
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}
		if (mCurrentGlid == -2 && mBatches[mUsedBatches].getRemaining() >= nsegments)
			mBatches[mUsedBatches].add(vertices, nsegments);
		else {
			mUsedBatches++;
			PrimitiveBatch pb = primitiveBatchPool.get();
			pb.add(vertices, nsegments);
			mBatches[mUsedBatches] = pb;
			mCurrentGlid = -2;
		}
	}

	public void drawRectangle (float cx, float cy, float w, float h, float rotation, float red, float green, float blue) {
		float[] vertices = new float[20];
		float c = (float)Math.cos(-rotation);
		float s = (float)Math.sin(-rotation);
		float x = w / 2, y = h / 2;
		vertices[0] = x * c - s * y + cx;
		vertices[1] = s * x + c * y + cy;
		x = -w / 2;
		vertices[5] = x * c - s * y + cx;
		vertices[6] = s * x + c * y + cy;
		y = -h / 2;
		vertices[10] = x * c - s * y + cx;
		vertices[11] = s * x + c * y + cy;
		x = w / 2;
		vertices[15] = x * c - s * y + cx;
		vertices[16] = s * x + c * y + cy;
		for (int i = 0; i < 4; i++) {
			vertices[i * 5 + 2] = red;
			vertices[i * 5 + 3] = green;
			vertices[i * 5 + 4] = blue;
		}
		if (mCurrentGlid == -2 && mBatches[mUsedBatches].getRemaining() >= 4)
			mBatches[mUsedBatches].add(vertices, 4);
		else {
			mUsedBatches++;
			PrimitiveBatch pb = primitiveBatchPool.get();
			pb.add(vertices, 4);
			mBatches[mUsedBatches] = pb;
			mCurrentGlid = -2;
		}
	}
}
