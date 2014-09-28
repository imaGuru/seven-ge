package com.engine.sevenge.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import android.graphics.PointF;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.audio.Music;
import com.engine.sevenge.ecs.CPosition;
import com.engine.sevenge.ecs.CSprite;
import com.engine.sevenge.ecs.Entity;
import com.engine.sevenge.ecs.SRender;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.input.InputEvent;

public class SampleGameState extends GameState {
	private Camera2D camera;
	private int mHeight;
	private int mWidth;

	private Music music;
	private SRender renderSystem;
	private List<Entity> entities;

	// TODO context and activity as one entity
	public SampleGameState(GameActivity gameActivity) {
		super(gameActivity);

		SevenGE.assetManager.loadAssets(SevenGE.io.asset("sample.pkg"));
		camera = new Camera2D();
		TextureShaderProgram tsp = (TextureShaderProgram) SevenGE.assetManager
				.getAsset("spriteShader");
		Texture2D tex = (Texture2D) SevenGE.assetManager.getAsset("spaceSheet");

		renderSystem = new SRender(camera, tsp, tex);
		
		Random rng = new Random();
		entities = new ArrayList<Entity>();
		for (int i = 0; i < 350; i++) {
			
			Entity e = new Entity();
			CSprite cs = new CSprite();
			CPosition cp = new CPosition();

			if (rng.nextInt(10) < 3)
				cs.subTexture = "meteorBrown_big1";
			else if (rng.nextInt(10) < 6)
				cs.subTexture = "meteorBrown_small2";
			else if (rng.nextInt(10) < 9)
				cs.subTexture = "meteorBrown_tiny2";
			else
				cs.subTexture = "enemyRed1";
			
			cp.rotation = rng.nextFloat() * 360.0f;
			cp.x = rng.nextFloat() * 1000f;
			cp.y = rng.nextFloat() * 1000f;
			cs.scale = 1.0f;
			
			e.add(cp,1);
			e.add(cs,2);
			entities.add(e);
		}

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
		renderSystem.process(entities);
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
