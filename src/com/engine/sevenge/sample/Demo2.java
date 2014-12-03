
package com.engine.sevenge.sample;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import java.util.Random;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.assets.Texture;
import com.engine.sevenge.assets.TextureRegion;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.TextureShaderProgram;

public class Demo2 extends GameState {

	private SpriteBatch spriteBatch;// = new SpriteBatch(100);
	float[] projectionMatrix = new float[16];
	float[] viewProjectionMatrix = new float[16];
	float[] viewMatrix = new float[16];
	int cameraX = 0;
	int cameraY = 0;
	float angle = 0;
	long lastTime = 0;

	public Demo2 (GameActivity gameActivity) {
		super(gameActivity);

		SevenGE.assetManager.loadAssets("sample.pkg");
		Texture texture = (Texture)SevenGE.assetManager.getAsset("spaceSheet");
		TextureShaderProgram tsp = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		Random rng = new Random();
		this.spriteBatch = new SpriteBatch(texture.glID, tsp, 100);

		for (int i = 0; i < 50; i++) {

			float scale = 1.0f;
			float rotation = 1.0f;
			float x, y;
			TextureRegion tr;

			float rnd = rng.nextFloat();
			if (rnd < 0.25f) {
				tr = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_big1");
				scale = 1f;
			} else if (rnd < 0.5f)
				tr = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_small2");
			else if (rnd < 0.75f)
				tr = (TextureRegion)SevenGE.assetManager.getAsset("enemyBlue1");
			else {
				tr = (TextureRegion)SevenGE.assetManager.getAsset("enemyRed1");
			}

			rotation = rng.nextFloat() * 360.0f;
			x = rng.nextFloat() * 500f;
			y = rng.nextFloat() * 500f;

			this.spriteBatch.add(tr, scale, rotation, (int)x, (int)y);
		}

		lastTime = System.currentTimeMillis() + 5000;

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw (float a, boolean updated) {

		this.spriteBatch.draw(viewProjectionMatrix, updated);
	}

	@Override
	public void update () {

		if (lastTime < System.currentTimeMillis()) {
			SevenGE.stateManager.setCurrentState(new Demo1(gameActivity));
		}

	}

	@Override
	public void pause () {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceChange (int width, int height) {

		Camera2D.lookAt(cameraX, cameraY, viewMatrix);
		Camera2D.setOrthoProjection(width, height, projectionMatrix);
		Camera2D.getVPM(viewProjectionMatrix, projectionMatrix, viewMatrix);

	}

}
