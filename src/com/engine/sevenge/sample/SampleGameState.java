package com.engine.sevenge.sample;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import java.util.Queue;
import java.util.Random;

import android.graphics.PointF;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.audio.Music;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.input.InputEvent;

public class SampleGameState extends GameState {

	private SpriteBatch spriteBatch;
	private Camera2D camera;
	private int mHeight;
	private int mWidth;

	private Music music;

	// TODO context and activity as one entity
	public SampleGameState(GameActivity gameActivity) {
		super(gameActivity);

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		SevenGE.assetManager.loadAssets(SevenGE.io.asset("sample.pkg"));
		camera = new Camera2D();
		TextureShaderProgram tsp = (TextureShaderProgram) SevenGE.assetManager
				.getAsset("spriteShader");
		Texture2D tex = (Texture2D) SevenGE.assetManager.getAsset("spaceSheet");
		spriteBatch = new SpriteBatch(tex, tsp, 1000);
		Random rng = new Random();
		for (int i = 0; i < 350; i++) {
			Sprite sprite;
			if (rng.nextInt(10) < 3)
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("meteorBrown_big1"));
			else if (rng.nextInt(10) < 6)
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("meteorBrown_small2"));
			else if (rng.nextInt(10) < 9)
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("meteorBrown_tiny2"));
			else
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("enemyRed1"));

			sprite.rotate(rng.nextFloat() * 6.28f);
			sprite.translate(rng.nextFloat() * 3000f, rng.nextFloat() * 3000f);
			spriteBatch.add(sprite.getTransformedVertices());
		}
		spriteBatch.upload();

		music = (Music) SevenGE.assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();

	}

	@Override
	public void onSurfaceChange(int width, int height) {
		if (mWidth != width || mHeight != height) {
			camera.setProjectionOrtho(width, height);
			camera.lookAt(500, 500);
			camera.zoom(1.0f);
			mHeight = height;
			mWidth = width;
		}
	}

	@Override
	public void draw() {
		glClear(GL_COLOR_BUFFER_BIT);
		spriteBatch.setVPMatrix(camera.getViewProjectionMatrix());
		spriteBatch.draw();
	}

	@Override
	public void update() {
		Queue<InputEvent> q = SevenGE.input.getQueue();
		InputEvent curIE;
		float[] coords1 = new float[4];
		float[] coords2 = new float[4];
		while ((curIE = q.poll())!=null) {
			if (curIE.type == InputEvent.Type.SCROLL) {
				coords1 = camera.unProject(curIE.me2.getX()+curIE.distx, curIE.me2.getY()+curIE.disty);
				float x1 = coords1[0];
				float y1 = coords1[1];
				coords2 = camera.unProject(curIE.me2.getX(), curIE.me2.getY());
				float x2 = coords2[0];
				float y2 = coords2[1];
				PointF cameraxy = camera.getCameraPosition();
				camera.lookAt(cameraxy.x - (x2 - x1), cameraxy.y - (y2 - y1));
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		music.pause();

	}

	@Override
	public void resume() {
		music.play();

	}

}
