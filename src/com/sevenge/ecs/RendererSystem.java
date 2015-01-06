
package com.sevenge.ecs;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import android.graphics.Color;

import com.sevenge.SevenGE;
import com.sevenge.assets.Shader;
import com.sevenge.assets.Texture;
import com.sevenge.assets.TextureRegion;
import com.sevenge.graphics.Camera;
import com.sevenge.graphics.ParticleShooter;
import com.sevenge.graphics.ParticleSystem;
import com.sevenge.graphics.SpriteBatcher;
import com.sevenge.graphics.TexturedParticleShaderProgram;
import com.sevenge.graphics.Vector;
import com.sevenge.utils.Vector2;

public class RendererSystem extends SubSystem {

	private SpriteBatcher mSpriteBatcher;
	private long globalStartTime;
	private ParticleSystem particleSystem;
	private ParticleShooter redParticleShooter;
	private Texture t;
	private Camera mCamera;

	public RendererSystem (int size) {
		super(SpriteComponent.MASK | PositionComponent.MASK, size);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		mEntities.setComparator(Entity.SortByLayerAndTexture);
	}

	public void setCamera (Camera camera) {
		this.mCamera = camera;
	}

	public void process (float interpolationAlpha) {
		glClear(GL_COLOR_BUFFER_BIT);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		mEntities.sort(false);
		mSpriteBatcher.clear();
		Vector2 camPos = mCamera.getPosition();
		Vector2 camLastPos = mCamera.getLastPosition();
		float interpX = camPos.x * interpolationAlpha + camPos.x * (1 - interpolationAlpha);
		float interpY = camPos.y * interpolationAlpha + camPos.y * (1 - interpolationAlpha);

		int lastLayer = 1337;
		float lastParallax = 0;
		for (int j = 0; j < mEntities.getCount(); j++) {
			Entity entity = mEntities.get(j);
			PositionComponent cp = (PositionComponent)entity.mComponents[0];
			SpriteComponent cs = (SpriteComponent)entity.mComponents[1];
			TextureRegion sprite = cs.textureRegion;
			if (lastLayer != cp.layer) {
				if (lastLayer != 1337) mSpriteBatcher.flush(mCamera.getCameraMatrix(lastParallax));
				lastLayer = cp.layer;
				lastParallax = cp.parallaxFactor;
			}
			mSpriteBatcher.drawSprite(cp.x, cp.y, cp.rotation, cs.scale, cs.scale, sprite);
		}
		mSpriteBatcher.flush(mCameraCC.viewProjectionMatrix);
	}
}
