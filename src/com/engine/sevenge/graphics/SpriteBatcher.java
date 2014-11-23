
package com.engine.sevenge.graphics;

import java.util.HashMap;

import com.engine.sevenge.SevenGE;

/** Class responsible for creating sprite batches and puting sprites in the correct batches for fast and easy drawing */
public class SpriteBatcher {
	private SpriteBatch[] batches;
	private HashMap<Integer, Integer> batchIndex;
	private int usedBatches;

	/** Creates a new spriteBatcher
	 * @param size number of batches
	 * @param batchSizeHint hint number of sprites in a batch */
	public SpriteBatcher (int sizeHint, int batchSizeHint) {
		usedBatches = 0;
		batches = new SpriteBatch[sizeHint];
		batchIndex = new HashMap<Integer, Integer>(sizeHint);
		TextureShaderProgram tsp = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		for (int i = 0; i < sizeHint; i++) {
			batches[i] = new SpriteBatch(batchSizeHint);
			batches[i].setProgram(tsp);
		}
	}

	/** Add a sprite to the correct batch for drawing
	 * @param vdata sprite vertex and uv data
	 * @param tex texture to be used for this sprite */
	public void addSprite (float[] vdata, Texture tex) {
		int glid;
		int i;
		glid = tex.getGLID();
		if (batchIndex.containsKey(glid)) {
			i = batchIndex.get(glid);
		} else {
			i = usedBatches;
			batchIndex.put(glid, i);
			batches[i].setTexture(tex);
			usedBatches++;
		}
		batches[i].add(vdata);
	}

	/** Draw the created batches using specified view projection matrix
	 * @param vpm view projection matrix */
	public void draw (float[] vpm) {
		for (int i = 0; i < usedBatches; i++) {
			batches[i].upload();
			batches[i].draw(vpm);
		}
	}

	public void clear () {
		for (int i = 0; i < usedBatches; i++)
			batches[i].clear();
	}
}
