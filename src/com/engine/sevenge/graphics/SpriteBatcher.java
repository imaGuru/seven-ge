
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;

import com.engine.sevenge.SevenGE;

/** Class responsible for creating sprite batches and puting sprites in the correct batches for fast and easy drawing */
public class SpriteBatcher {
	private SpriteBatch[] batches;
	private int usedBatches;
	private int currentGlid = -1;

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
			batches[i].clear();
		}
		usedBatches = -1;
		currentGlid = -1;
	}
}
