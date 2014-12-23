
package com.sevenge.ecs;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import com.sevenge.SevenGE;
import com.sevenge.assets.Font;
import com.sevenge.assets.TextureRegion;
import com.sevenge.graphics.Camera2D;
import com.sevenge.graphics.SpriteBatcher;

public class RendererSystem extends SubSystem {

	private SpriteBatcher mSpriteBatcher;
	private PositionComponent mCameraCP;
	private CameraComponent mCameraCC;
	private Font font;

	public RendererSystem (int size) {
		super(SpriteComponent.MASK | PositionComponent.MASK, size);
		font = (Font)SevenGE.assetManager.getAsset("font");
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		mSpriteBatcher = new SpriteBatcher(1000, 1000);
	}

	public void setCamera (Entity camera) {
		mCameraCP = (PositionComponent)camera.mComponents[0];
		mCameraCC = (CameraComponent)camera.mComponents[2];
	}

	public void process (float interpolationAlpha) {
		mSpriteBatcher.clear();
		for (int j = 0; j < mEntities.getCount(); j++) {
			Entity entity = mEntities.get(j);
			PositionComponent cp = (PositionComponent)entity.mComponents[0];
			SpriteComponent cs = (SpriteComponent)entity.mComponents[1];
			TextureRegion sprite = cs.textureRegion;
			mSpriteBatcher.drawSprite(cp.x, cp.y, cp.rotation, cs.scale, cs.scale, sprite);
			mSpriteBatcher.drawCircle(cp.x, cp.y, sprite.height / 2, (float)Math.toRadians(cp.rotation), 10, 0.7f);
		}
		font.scaleX = 4;
		font.scaleY = 4;
		mSpriteBatcher.drawText("This is a physics test! Animated ships are suppposed to fall!", -500, -130, font);
		Camera2D.lookAt(mCameraCP.x * interpolationAlpha + mCameraCP.px * (1 - interpolationAlpha), mCameraCP.y
			* interpolationAlpha + mCameraCP.py * (1 - interpolationAlpha), mCameraCC.viewMatrix);
		Camera2D.getVPM(mCameraCC.viewProjectionMatrix, mCameraCC.projectionMatrix, mCameraCC.viewMatrix);
		mSpriteBatcher.flush(mCameraCC.viewProjectionMatrix);
	}
}
