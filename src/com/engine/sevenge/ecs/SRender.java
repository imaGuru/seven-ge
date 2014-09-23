package com.engine.sevenge.ecs;

import java.util.List;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;

import android.graphics.Matrix;

public class SRender extends System {

	private SpriteBatch spriteBatch;
	private Camera2D camera;

	public SRender(Camera2D cam, TextureShaderProgram tsp, Texture2D tex) {
		camera = cam;
		spriteBatch = new SpriteBatch(tex, tsp, 1000);
	}

	@Override
	public void process(List<Entity> entities) {
		for (Entity entity : entities) {
			if ((3 & entity.mask) == 3) {
				CPosition cp = (CPosition) entity.components.get(1);
				CSprite cs = (CSprite) entity.components.get(2);
				SubTexture2D sprite = (SubTexture2D) SevenGE.assetManager
						.getAsset(cs.subTexture);
				Matrix transform = new Matrix();
				transform.setTranslate(sprite.getWidth()/2, -sprite.getHeight()/2);
				transform.preScale(cs.scale, cs.scale);
				transform.preRotate(cp.rotation);
				transform.preTranslate(cp.x, cp.y);
				float[] v = { cp.x + sprite.getWidth() / 2,
						cp.y + sprite.getHeight() / 2,
						cp.x - sprite.getWidth() / 2,
						cp.y + sprite.getHeight() / 2,
						cp.x - sprite.getWidth() / 2,
						cp.y - sprite.getHeight() / 2,
						cp.x + sprite.getWidth() / 2,
						cp.y - sprite.getHeight() / 2 };
				transform.mapPoints(v);
				float[] uvs = sprite.getUVs();
				float[] t = { v[0], v[1], uvs[0], uvs[1], v[2], v[3], uvs[2],
						uvs[3], v[4], v[5], uvs[4], uvs[5], v[6], v[7], uvs[6],
						uvs[7] };
				spriteBatch.add(t);
			}
		}
		spriteBatch.upload();
		spriteBatch.setVPMatrix(camera.getViewProjectionMatrix());
		spriteBatch.draw();
	}
}
