
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

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;

public class RendererSystem extends System {

	private SpriteBatch spriteBatch;
	private Camera2D camera;
	private float[] uvs, v = new float[8], t = new float[16];
	private Matrix transform = new Matrix();

	public RendererSystem (Camera2D cam, TextureShaderProgram tsp, Texture2D tex) {

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		camera = cam;
		spriteBatch = new SpriteBatch(tex, tsp, 1000);
	}

	@Override
	public void process (List<Entity> entities) {
		spriteBatch.clear();

		for (Entity entity : entities) {
			if ((3 & entity.mask) == 3) {
				PositionComponent cp = (PositionComponent)entity.components.get(1);
				SpriteComponent cs = (SpriteComponent)entity.components.get(2);
				SubTexture2D sprite = (SubTexture2D)SevenGE.assetManager.getAsset(cs.subTexture);

				transform.setTranslate(sprite.getWidth() / 2, -sprite.getHeight() / 2);
				transform.preScale(cs.scale, cs.scale);
				transform.preRotate(cp.rotation);
				transform.preTranslate(cp.x, cp.y);
				v[0] = cp.x + sprite.getWidth() / 2;
				v[1] = cp.y + sprite.getHeight() / 2;
				v[2] = cp.x - sprite.getWidth() / 2;
				v[3] = cp.y + sprite.getHeight() / 2;
				v[4] = cp.x - sprite.getWidth() / 2;
				v[5] = cp.y - sprite.getHeight() / 2;
				v[6] = cp.x + sprite.getWidth() / 2;
				v[7] = cp.y - sprite.getHeight() / 2;
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
				spriteBatch.add(t);
			}
		}
		spriteBatch.upload();
		spriteBatch.setVPMatrix(camera.getViewProjectionMatrix());
		glClear(GL_COLOR_BUFFER_BIT);
		spriteBatch.draw();
	}
}
