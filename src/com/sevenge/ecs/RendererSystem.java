
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
import com.sevenge.assets.Font;
import com.sevenge.assets.Shader;
import com.sevenge.assets.Texture;
import com.sevenge.assets.TextureRegion;
import com.sevenge.graphics.Camera2D;
import com.sevenge.graphics.ParticleShaderProgram;
import com.sevenge.graphics.ParticleShooter;
import com.sevenge.graphics.ParticleSystem;
import com.sevenge.graphics.SpriteBatcher;
import com.sevenge.graphics.Vector;

public class RendererSystem extends SubSystem {

	private SpriteBatcher mSpriteBatcher;
	private PositionComponent mCameraCP;
	private CameraComponent mCameraCC;
	private Font font;
	private long globalStartTime;
	private ParticleSystem particleSystem;
	private ParticleShooter redParticleShooter;
	private Texture t;

	public RendererSystem (int size) {
		super(SpriteComponent.MASK | PositionComponent.MASK, size);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		mEntities.setComparator(Entity.SortByLayerAndTexture);
	}

	public void setCamera (Entity camera) {
		mCameraCP = (PositionComponent)camera.mComponents[0];
		mCameraCC = (CameraComponent)camera.mComponents[2];
	}

	public void process (float interpolationAlpha) {
		glClear(GL_COLOR_BUFFER_BIT);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		mEntities.sort(false);
		mSpriteBatcher.clear();
		float interpX = mCameraCP.x * interpolationAlpha + mCameraCP.px * (1 - interpolationAlpha);
		float interpY = mCameraCP.y * interpolationAlpha + mCameraCP.py * (1 - interpolationAlpha);

		int lastLayer = 1337;
		for (int j = 0; j < mEntities.getCount(); j++) {
			Entity entity = mEntities.get(j);
			PositionComponent cp = (PositionComponent)entity.mComponents[0];
			SpriteComponent cs = (SpriteComponent)entity.mComponents[1];
			TextureRegion sprite = cs.textureRegion;
			if (lastLayer != cp.layer) {
				if (lastLayer != 1337) mSpriteBatcher.flush(mCameraCC.viewProjectionMatrix);
				Camera2D.lookAt(interpX * cp.parallaxFactor, interpY * cp.parallaxFactor, mCameraCC.viewMatrix);
				Camera2D.getVPM(mCameraCC.viewProjectionMatrix, mCameraCC.projectionMatrix, mCameraCC.viewMatrix);
				lastLayer = cp.layer;
			}
			mSpriteBatcher.drawSprite(cp.x, cp.y, cp.rotation, cs.scale, cs.scale, sprite);
		}
		mSpriteBatcher.flush(mCameraCC.viewProjectionMatrix);
	}
}
