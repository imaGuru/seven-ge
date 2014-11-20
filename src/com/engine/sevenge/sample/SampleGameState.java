
package com.engine.sevenge.sample;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.audio.Music;
import com.engine.sevenge.ecs.AnimationComponent;
import com.engine.sevenge.ecs.AnimationSystem;
import com.engine.sevenge.ecs.CameraComponent;
import com.engine.sevenge.ecs.CameraSystem;
import com.engine.sevenge.ecs.Entity;
import com.engine.sevenge.ecs.PositionComponent;
import com.engine.sevenge.ecs.RendererSystem;
import com.engine.sevenge.ecs.ScriptingSystem;
import com.engine.sevenge.ecs.SpriteComponent;
import com.engine.sevenge.graphics.TextureRegion;

public class SampleGameState extends GameState {

	private Music music;
	private RendererSystem rendererSystem;
	private CameraSystem cameraSystem;
	private AnimationSystem animationSystem;
	private List<Entity> entities;
	private int changeme = 0;
	private ScriptingSystem scriptingSystem;

	// TODO context and activity as one entity
	public SampleGameState (GameActivity gameActivity) {
		super(gameActivity);

		SevenGE.assetManager.loadAssets("sample.pkg");

		rendererSystem = new RendererSystem();
		cameraSystem = new CameraSystem();
		animationSystem = new AnimationSystem();
		scriptingSystem = new ScriptingSystem();

		Random rng = new Random();
		entities = new ArrayList<Entity>();
		Entity cam = new Entity();
		entities.add(cam);
		for (int i = 0; i < 100; i++) {
			Entity e = new Entity();
			SpriteComponent cs = new SpriteComponent();
			PositionComponent cp = new PositionComponent();
			cs.scale = 1.0f;
			float rnd = rng.nextFloat();
			if (rnd < 0.5f) {
				cs.subTexture = (TextureRegion)SevenGE.assetManager.getAsset("applesp");
				cs.scale = 0.6f;
			} else if (rnd < 0.7f)
				cs.subTexture = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_small2");
			else if (rnd < 0.95f)
				cs.subTexture = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_tiny2");
			else {
				cs.subTexture = (TextureRegion)SevenGE.assetManager.getAsset("enemyRed1");
				AnimationComponent ca = new AnimationComponent();
				ca.frameList = new TextureRegion[] {(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack1"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack2"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack3"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack4"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack5")};
				ca.durations = new int[] {500, 1000, 2000, 234, 666};
				ca.isPlaying = true;
				e.add(ca, 4);
			}

			cp.rotation = rng.nextFloat() * 360.0f;
			cp.x = rng.nextFloat() * 2000f;
			cp.y = rng.nextFloat() * 2000f;

			e.add(cp, 1);
			e.add(cs, 2);
			entities.add(e);
		}

		music = (Music)SevenGE.assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();

	}

	@Override
	public void onSurfaceChange (int width, int height) {
		Entity e = entities.get(0);
		CameraComponent cc = new CameraComponent();
		cc.height = height;
		cc.width = width;
		cc.scale = 0.7f;
		PositionComponent cp = new PositionComponent();
		cp.x = 0;
		cp.y = 0;
		setLookAtM(cc.viewMatrix, 0, cp.x, cp.y, 1f, cp.x, cp.y, 0f, 0f, 1.0f, 0.0f);
		orthoM(cc.projectionMatrix, 0, -width / cc.scale / 2, width / cc.scale / 2, -height / cc.scale / 2, height / cc.scale / 2,
			0f, 1f);
		multiplyMM(cc.viewProjectionMatrix, 0, cc.projectionMatrix, 0, cc.viewMatrix, 0);
		invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
		e.add(cp, 1);
		e.add(cc, 8);
	}

	@Override
	public void update () {
		animationSystem.process(entities);
		cameraSystem.process(entities);
		if (changeme >= 30) {
			changeme = 0;
			scriptingSystem.process(entities);
		}
		changeme++;
	}

	@Override
	public void draw () {
		rendererSystem.process(entities);
	}

	@Override
	public void dispose () {

	}

	@Override
	public void pause () {
		music.pause();

	}

	@Override
	public void resume () {
		music.play();

	}

}
