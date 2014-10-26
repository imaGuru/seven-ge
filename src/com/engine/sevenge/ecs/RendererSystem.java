
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

import android.graphics.Matrix;

import com.engine.sevenge.graphics.SpriteBatcher;
import com.engine.sevenge.graphics.TextureRegion;

public class RendererSystem extends System {

	private static int SYSTEM_MASK = SpriteComponent.MASK | PositionComponent.MASK;

	private SpriteBatcher spriteBatcher;
	private float[] uvs, v = new float[8], t = new float[16];
	private Matrix transform = new Matrix();
	private int hw, hh;

	public RendererSystem () {

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		spriteBatcher = new SpriteBatcher(2, 200);
		// Texture2D tex = (Texture2D)SevenGE.assetManager.getAsset("spaceSheet");
		// spriteBatch = new SpriteBatch(tex, tsp, 1000);
	}

	@Override
	public void process (List<Entity> entities) {
		glClear(GL_COLOR_BUFFER_BIT);
		float[] vpm = null;
		for (Entity entity : entities) {
			if ((SYSTEM_MASK & entity.mask) == SYSTEM_MASK) {
				PositionComponent cp = (PositionComponent)entity.components.get(1);
				SpriteComponent cs = (SpriteComponent)entity.components.get(2);
				TextureRegion sprite = cs.subTexture;

				hw = sprite.getWidth() / 2;
				hh = sprite.getHeight() / 2;
				transform.setTranslate(hw, hh);
				transform.preRotate(cp.rotation);
				transform.preTranslate(cp.x, cp.y);
				transform.preScale(cs.scale, cs.scale);
				v[0] = cp.x + hw;
				v[1] = cp.y + hh;
				v[2] = cp.x - hw;
				v[3] = cp.y + hh;
				v[4] = cp.x - hw;
				v[5] = cp.y - hh;
				v[6] = cp.x + hw;
				v[7] = cp.y - hh;
				transform.mapPoints(v);
				uvs = sprite.getUVs();
				t[0] = v[0];
				t[1] = v[1];
				t[2] = uvs[0];
				t[3] = uvs[1];
				t[4] = v[2];
				t[5] = v[3];
				t[6] = uvs[2];
				t[7] = uvs[3];
				t[8] = v[4];
				t[9] = v[5];
				t[10] = uvs[4];
				t[11] = uvs[5];
				t[12] = v[6];
				t[13] = v[7];
				t[14] = uvs[6];
				t[15] = uvs[7];
				spriteBatcher.addSprite(t, sprite.getTexture());
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
