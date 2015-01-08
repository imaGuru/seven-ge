
package com.sevenge.ecs;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

import com.sevenge.IO;
import com.sevenge.assets.Font;
import com.sevenge.graphics.Camera;
import com.sevenge.graphics.FontUtils;
import com.sevenge.graphics.SpriteBatch;
import com.sevenge.graphics.TextureRegion;

public class RendererSystem extends SubSystem {

	private Camera mCamera;
	private SpriteBatch mSpriteBatch;
	private Font font;
	private float[] matrix = new float[16];

	public RendererSystem (int size) {
		super(SpriteComponent.MASK | PositionComponent.MASK, size);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		mEntities.setComparator(Entity.SortByLayerAndTexture);
		mSpriteBatch = new SpriteBatch(300);
		font = FontUtils.load(IO.getAssetManager(), "Fonts/OpenSansBold.ttf", 20, 2, 0);
	}

	public void setCamera (Camera camera) {
		this.mCamera = camera;
	}

	public void process (float interpolationAlpha) {
		glClear(GL_COLOR_BUFFER_BIT);
		mSpriteBatch.begin();
		mSpriteBatch.enableBlending();
		mSpriteBatch.setBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		mEntities.sort(false);
		mSpriteBatch.setProjection(mCamera.getCameraMatrix(0.7f, matrix));
		for (int j = 0; j < mEntities.getCount(); j++) {
			Entity entity = mEntities.get(j);
			PositionComponent cp = (PositionComponent)entity.mComponents[0];
			SpriteComponent cs = (SpriteComponent)entity.mComponents[1];
			TextureRegion sprite = cs.textureRegion;
			mSpriteBatch.drawSprite(cp.x, cp.y, cp.rotation, cs.scale, cs.scale, sprite);
		}
		mSpriteBatch.setProjection(mCamera.getCameraMatrix(1.0f, matrix));
		mSpriteBatch.drawText("This is a test of the new SpriteBatch API FPS:", 500, 500, font);
		mSpriteBatch.end();
	}
}
