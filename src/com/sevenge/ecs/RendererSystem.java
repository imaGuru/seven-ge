
package com.sevenge.ecs;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import com.sevenge.assets.TextureRegion;
import com.sevenge.graphics.Camera2D;
import com.sevenge.graphics.SpriteBatcher;

public class RendererSystem extends System {

	private SpriteBatcher spriteBatcher;
	private float[] uvs;
	private float[] v;
	private float[] r = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];
	private PositionComponent mCameraCP;
	private CameraComponent mCameraCC;

	public RendererSystem (int size) {
		super(SpriteComponent.MASK | PositionComponent.MASK, size);

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		spriteBatcher = new SpriteBatcher(5, 1000);
	}

	public void setCamera (Entity camera) {
		mCameraCP = (PositionComponent)camera.components[0];
		mCameraCC = (CameraComponent)camera.components[2];
	}

	public void process (float interpolationAlpha) {
		spriteBatcher.clear();
		for (int j = 0; j < entities.getCount(); j++) {
			Entity entity = entities.get(j);
			PositionComponent cp = (PositionComponent)entity.components[0];
			SpriteComponent cs = (SpriteComponent)entity.components[1];

			TextureRegion sprite = cs.textureRegion;

			// spriteBatcher.addSprite(t, sprite.texture);
			spriteBatcher.addSprite(cp.transform, sprite);
		}
		Camera2D.lookAt(mCameraCP.x * interpolationAlpha + mCameraCP.px * (1 - interpolationAlpha), mCameraCP.y
			* interpolationAlpha + mCameraCP.py * (1 - interpolationAlpha), mCameraCC.viewMatrix);
		Camera2D.getVPM(mCameraCC.viewProjectionMatrix, mCameraCC.projectionMatrix, mCameraCC.viewMatrix);
		// Matrix.invertM(mCameraCC.invertedVPMatrix, 0, mCameraCC.viewProjectionMatrix, 0);
		spriteBatcher.draw(mCameraCC.viewProjectionMatrix);
	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}
}
