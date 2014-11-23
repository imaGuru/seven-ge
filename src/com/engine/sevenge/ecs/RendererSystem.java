
package com.engine.sevenge.ecs;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import java.util.List;

import android.opengl.Matrix;

import com.engine.sevenge.graphics.SpriteBatcher;
import com.engine.sevenge.graphics.TextureRegion;

public class RendererSystem extends System {

	private static int SYSTEM_MASK = SpriteComponent.MASK | PositionComponent.MASK;

	private int i;
	private SpriteBatcher spriteBatcher;
	private float[] uvs;
	private float[] v;
	private float[] r = new float[8], sv = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];
	private float[] transform = new float[16], scaleMatrix = new float[16];
	private int hw, hh;

	public RendererSystem () {

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		spriteBatcher = new SpriteBatcher(3, 400);
	}

	@Override
	public void process (List<Entity> entities) {
		glClear(GL_COLOR_BUFFER_BIT);
		float[] vpm = null;
		spriteBatcher.clear();
		for (Entity entity : entities) {
			if ((SYSTEM_MASK & entity.mask) == SYSTEM_MASK) {
				PositionComponent cp = (PositionComponent)entity.components.get(1);
				SpriteComponent cs = (SpriteComponent)entity.components.get(2);
				TextureRegion sprite = cs.subTexture;

				hw = sprite.width / 2;
				hh = sprite.height / 2;
				uvs = sprite.uvs;
				v = sprite.v;

				// TODO Move this to components so we only multiply. Avoid useless creation of transforms

				Matrix.setIdentityM(scaleMatrix, 0);
				Matrix.scaleM(scaleMatrix, 0, cs.scale, cs.scale, 1.0f);

				Matrix.setIdentityM(transform, 0);
				Matrix.translateM(transform, 0, hw, hh, 0f);
				Matrix.rotateM(transform, 0, cp.rotation, 0f, 0f, 1.0f);
				Matrix.translateM(transform, 0, cp.x, cp.y, 0f);
				for (i = 0; i < 4; i++) {
					temp[0] = v[i * 2];
					temp[1] = v[i * 2 + 1];
					temp[2] = 0;
					temp[3] = 1;
					Matrix.multiplyMV(tempr, 0, scaleMatrix, 0, temp, 0);
					Matrix.multiplyMV(temp, 0, transform, 0, tempr, 0);
					r[i * 2] = temp[0];
					r[i * 2 + 1] = temp[1];
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
			} else if ((entity.mask & CameraComponent.MASK) == CameraComponent.MASK) {
				CameraComponent cc = (CameraComponent)entity.components.get(8);
				vpm = cc.viewProjectionMatrix;
			}
		}
		if (vpm != null) {
			spriteBatcher.draw(vpm);
		}
	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}
}
