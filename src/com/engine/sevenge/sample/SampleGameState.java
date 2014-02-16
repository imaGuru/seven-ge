package com.engine.sevenge.sample;

import java.util.Random;

import android.content.Context;

import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.audio.Music;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.RenderQueue;
import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.input.SingleTouchDetector;

public class SampleGameState extends GameState {

	private SpriteBatch spriteBatch;
	private Camera2D camera;
	private InputProcessor inputProcessor;
	private Context mContext;
	private RenderQueue mRenderQueue;
	private int mHeight;
	private int mWidth;

	public SampleGameState(Context context) {
		mContext = context;
		inputProcessor = new InputProcessor();
		SevenGE.input.addDetector(new SingleTouchDetector(mContext,
				new SampleGestureListener(inputProcessor)));
	}

	@Override
	public void onStart() {
		mRenderQueue = SevenGE.renderer.getRenderQueue();
		SevenGE.assetManager.clearAssets();
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

		Music music = (Music) SevenGE.assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();
	}

	public void onSurfaceChange(int width, int height) {
		if (mWidth != width || mHeight != height) {
			camera.setProjectionOrtho(width, height);
			camera.lookAt(500, 500);
			camera.zoom(1.2f);
			mHeight = height;
			mWidth = width;
		}
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw() {
		spriteBatch.setVPMatrix(camera.getViewProjectionMatrix());
		mRenderQueue.add(spriteBatch);
		mRenderQueue.render();
	}

	@Override
	public void update() {
		inputProcessor.process(camera);
	}

}
