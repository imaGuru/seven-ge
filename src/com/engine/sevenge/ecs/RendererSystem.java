
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
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.SpriteBatcher;

public class RendererSystem extends System {

	private static int SYSTEM_MASK = SpriteComponent.MASK | PositionComponent.MASK;

	private SpriteBatcher spriteBatcher;
	private float[] uvs;
	private float[] v;
	private float[] r = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];

	public RendererSystem () {

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		spriteBatcher = new SpriteBatcher(1, 1000);
	}

	public void process (Component[] entities, CameraComponent c, int count, float a, boolean updated) {
		if (updated) {
			spriteBatcher.clear();
			for (int j = 0; j < count; j += 2) {
				PositionComponent cp = (PositionComponent)entities[j];
				SpriteComponent cs = (SpriteComponent)entities[j + 1];
				TextureRegion sprite = cs.textureRegion;
				uvs = sprite.UVs;
				v = sprite.vertices;

				for (int i = 0; i < 4; i++) {
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
		}
		Camera2D.lookAt(c.x * a + c.px * (1 - a), c.y * a + c.py * (1 - a), c.viewMatrix);
		Camera2D.getVPM(c.viewProjectionMatrix, c.projectionMatrix, c.viewMatrix);
		spriteBatcher.draw(c.viewProjectionMatrix, updated);
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
