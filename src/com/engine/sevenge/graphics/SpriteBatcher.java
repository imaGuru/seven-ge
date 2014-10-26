
package com.engine.sevenge.graphics;

import java.util.HashMap;

import com.engine.sevenge.SevenGE;

public class SpriteBatcher {
	private SpriteBatch[] batches;
	private HashMap<Integer, Integer> batchIndex;
	private int[] batchesSize;
	private int usedBatches;

	public SpriteBatcher (int sizeHint, int batchSizeHint) {
		usedBatches = 0;
		batches = new SpriteBatch[sizeHint];
		batchesSize = new int[sizeHint];
		batchIndex = new HashMap<Integer, Integer>(sizeHint);
		TextureShaderProgram tsp = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		for (int i = 0; i < sizeHint; i++) {
			batches[i] = new SpriteBatch(batchSizeHint);
			batchesSize[i] = 0;
			batches[i].setProgram(tsp);
		}
	}

	public void addSprite (float[] vdata, Texture tex) {
		int glid;
		int i;
		glid = tex.getGLID();
		if (batchIndex.containsKey(glid)) {
			i = batchIndex.get(glid);
		} else {
			i = usedBatches;
			batchIndex.put(glid, i);
			usedBatches++;
			batches[i].setTexture(tex);
		}
		batchesSize[i]++;
		batches[i].add(vdata);
	}

	public void draw (float[] vpm) {
		for (int i = 0; i < usedBatches; i++) {
			batches[i].upload();
			batches[i].draw(vpm);
			batches[i].clear();
		}
	}
}
