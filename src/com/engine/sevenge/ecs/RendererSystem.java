
package com.engine.sevenge.ecs;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import java.util.List;

import android.opengl.Matrix;

import com.engine.sevenge.assets.TextureRegion;
import com.engine.sevenge.graphics.SpriteBatcher;

public class RendererSystem extends System {

	private static int SYSTEM_MASK = SpriteComponent.MASK | PositionComponent.MASK;

	private int i, j;
	private SpriteBatcher spriteBatcher;
	private float[] uvs;
	private float[] v;
	private float[] r = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];

	public RendererSystem () {

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		spriteBatcher = new SpriteBatcher(10, 400);
	}

	@Override
	public void process (Component[] entities, float[] vpm, int count) {
		for (j = 0; j < count; j += 2) {
			PositionComponent cp = (PositionComponent)entities[j];
			SpriteComponent cs = (SpriteComponent)entities[j + 1];
			TextureRegion sprite = cs.textureRegion;
			uvs = sprite.UVs;
			v = sprite.vertices;

			for (i = 0; i < 4; i++) {
				temp[0] = v[i * 2];
				temp[1] = v[i * 2 + 1];
				temp[2] = 0;
				temp[3] = 1;
				// Matrix.multiplyMV(tempr, 0, cp.scaleMatrix, 0, temp, 0);
				Matrix.multiplyMV(tempr, 0, cp.transform, 0, temp, 0);
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

			spriteBatcher.addSprite(t, sprite.texture);
		}
		spriteBatcher.draw(vpm);
	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void process (List<Entity> entities) {
		// TODO Auto-generated method stub

	}
}
