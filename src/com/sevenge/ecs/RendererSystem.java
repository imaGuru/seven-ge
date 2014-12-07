
package com.sevenge.ecs;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import android.opengl.Matrix;

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
			uvs = sprite.UVs;
			v = sprite.vertices;

			Matrix.setIdentityM(cp.scaleMatrix, 0);
			Matrix.scaleM(cp.scaleMatrix, 0, cs.scale, cs.scale, 1.0f);

			Matrix.setIdentityM(cp.transform, 0);
			Matrix.translateM(cp.transform, 0, cs.textureRegion.width / 2, cs.textureRegion.height / 2, 0f);
			Matrix.rotateM(cp.transform, 0, cp.rotation, 0f, 0f, 1.0f);
			Matrix.translateM(cp.transform, 0, cp.x, cp.y, 0f);

			for (int i = 0; i < 4; i++) {
				temp[0] = v[i * 2];
				temp[1] = v[i * 2 + 1];
				temp[2] = 0;
				temp[3] = 1;
				// Matrix.multiplyMV(tempr, 0, cp.scaleMatrix, 0, temp, 0);
				Matrix.multiplyMV(tempr, 0, cp.transform, 0, temp, 0);
				r[i * 2] = tempr[0];
				r[i * 2 + 1] = tempr[1];
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
