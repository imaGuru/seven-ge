package com.sevenge.ecs;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glClearColor;

import com.sevenge.graphics.Camera;
import com.sevenge.graphics.SpriteBatcher;
import com.sevenge.graphics.TextureRegion;

/**
 * System responsible for displaying entities on screen.
 */
public class RendererSystem extends SubSystem {

	private Camera mCamera;
	private SpriteBatcher mSpriteBatcher;
	private float[] matrix = new float[16];

	public RendererSystem(int size) {
		super(SpriteComponent.MASK | PositionComponent.MASK, size);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		mEntities.setComparator(Entity.SortByLayerAndTexture);
		mSpriteBatcher = new SpriteBatcher(size);
	}

	public void setCamera(Camera camera) {
		this.mCamera = camera;
	}

	public void process(float interpolationAlpha) {
		mEntities.sort(false);
		mSpriteBatcher.begin();
		mSpriteBatcher.enableBlending();
		mSpriteBatcher.setBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		mSpriteBatcher.setProjection(mCamera.getCameraMatrix(1f, matrix));
		for (int j = 0; j < mEntities.getCount(); j++) {
			Entity entity = mEntities.get(j);
			PositionComponent cp = (PositionComponent) entity.mComponents[0];
			SpriteComponent cs = (SpriteComponent) entity.mComponents[1];
			TextureRegion sprite = cs.textureRegion;
			mSpriteBatcher.drawSprite(cp.x, cp.y, cp.rotation, cs.scale,
					cs.scale, sprite);
		}

		mSpriteBatcher.end();
	}
}
