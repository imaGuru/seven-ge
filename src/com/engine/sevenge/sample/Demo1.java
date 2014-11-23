
package com.engine.sevenge.sample;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.Texture;
import com.engine.sevenge.graphics.TextureRegion;
import com.engine.sevenge.graphics.TextureShaderProgram;

public class Demo1 extends GameState {

	private SpriteBatch spriteBatch;// = new SpriteBatch(100);
	float[] projectionMatrix = new float[16];
	float[] viewProjectionMatrix = new float[16];
	float[] viewMatrix = new float[16];
	int cameraX = 0;
	int cameraY = 0;
	float angle = 0;
	long lastTime = 0;

	public Demo1 (GameActivity gameActivity) {
		super(gameActivity);

		SevenGE.assetManager.loadAssets("sample.pkg");
		Texture texture = (Texture)SevenGE.assetManager.getAsset("spaceSheet");
		TextureShaderProgram tsp = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		this.spriteBatch = new SpriteBatch(texture, tsp, 100);

		TextureRegion tr1 = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_big2");
		TextureRegion tr2 = (TextureRegion)SevenGE.assetManager.getAsset("playerShip1_red");
		TextureRegion tr3 = (TextureRegion)SevenGE.assetManager.getAsset("laserBlue05");

		this.spriteBatch.add(tr1, 1.5f, 100, 500, 100);
		this.spriteBatch.add(tr2, 1.5f, 66, 150, 150);
		this.spriteBatch.add(tr3, 1.5f, 99, 200, 100);

		this.spriteBatch.upload();
		lastTime = System.currentTimeMillis() + 5000;

		angle = (float)((angle + 0.1f) % (2 * Math.PI));
		cameraX = (int)(Math.cos(angle) * 100) + 116;
		cameraY = (int)(Math.sin(angle) * 100) + 283;

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw () {
		glClear(GL_COLOR_BUFFER_BIT);
		spriteBatch.draw(viewProjectionMatrix);
	}

	@Override
	public void update () {

		angle = (float)((angle + 0.1f) % (2 * Math.PI));
		cameraX = (int)(Math.cos(angle) * 100) + 116;
		cameraY = (int)(Math.sin(angle) * 100) + 283;

		Camera2D.lookAt(cameraX, cameraY, viewMatrix);
		Camera2D.getVPM(viewProjectionMatrix, projectionMatrix, viewMatrix);

		if (lastTime < System.currentTimeMillis()) {
			SevenGE.stateManager.setCurrentState(new Demo2(gameActivity));
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
