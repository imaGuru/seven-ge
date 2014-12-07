
package com.sevenge.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.opengl.Matrix;
import android.view.MotionEvent;

import com.sevenge.GameActivity;
import com.sevenge.GameState;
import com.sevenge.SevenGE;
import com.sevenge.assets.TextureRegion;
import com.sevenge.audio.Music;
import com.sevenge.ecs.AnimationComponent;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.CameraComponent;
import com.sevenge.ecs.CameraSystem;
import com.sevenge.ecs.Component;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.input.GestureProcessor;
import com.sevenge.input.InputProcessor;
import com.sevenge.utils.Log;

public class SampleGameState extends GameState implements InputProcessor, GestureProcessor {

	private final String TAG = "InputTest";

	private Music music;
	private RendererSystem rendererSystem;
	private CameraSystem cameraSystem;
	private AnimationSystem animationSystem;
	private List<Entity> entities;
	private int changeme = 0;
	private Component[] optimizedEntites = new Component[10000];
	private int oec = 0;
	private CameraComponent cc;
	private ScriptingSystem scriptingSystem;

	private float angle;

	private float cameraX;

	private float cameraY;

	public SampleGameState (GameActivity gameActivity) {
		super(gameActivity);

		SevenGE.input.addInputProcessor(this);
		SevenGE.input.addGestureProcessor(this);

		SevenGE.assetManager.loadAssets("sample.pkg");

		rendererSystem = new RendererSystem();
		
		animationSystem = new AnimationSystem();
		scriptingSystem = new ScriptingSystem();

		Random rng = new Random();
		entities = new ArrayList<Entity>();
		Entity cam = new Entity();
		entities.add(cam);
		for (int i = 0; i < 200; i++) {
			Entity e = new Entity();
			SpriteComponent cs = new SpriteComponent();
			PositionComponent cp = new PositionComponent();
			cs.scale = 1.0f;
			float rnd = rng.nextFloat();
			if (rnd < 0.5f) {
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_big1");
				cs.scale = 1f;
			} else if (rnd < 0.7f)
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_small2");
			else if (rnd < 0.95f)
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_tiny2");
			else {
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("enemyRed1");
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
			cp.x = rng.nextFloat() * 1000f;
			cp.y = rng.nextFloat() * 1000f;
			Matrix.setIdentityM(cp.scaleMatrix, 0);
			Matrix.scaleM(cp.scaleMatrix, 0, cs.scale, cs.scale, 1.0f);

			Matrix.setIdentityM(cp.transform, 0);
			Matrix.translateM(cp.transform, 0, cs.textureRegion.width / 2, cs.textureRegion.height / 2, 0f);
			Matrix.rotateM(cp.transform, 0, cp.rotation, 0f, 0f, 1.0f);
			Matrix.translateM(cp.transform, 0, cp.x, cp.y, 0f);

			e.add(cp, 1);
			e.add(cs, 2);

			optimizedEntites[oec++] = cp;
			optimizedEntites[oec++] = cs;

			// entities.add(e);
		}

		// music = (Music)SevenGE.assetManager.getAsset("music1");
		// music.setLooping(true);
		// music.play();

		cameraSystem = new CameraSystem(cam);
		
	}

	@Override
	public void onSurfaceChange (int width, int height) {
		Entity e = entities.get(0);
		cc = new CameraComponent();
		cc.height = height;
		cc.width = width;
		cc.scale = 0.7f;
		PositionComponent cp = new PositionComponent();
		cp.x = 0;
		cp.y = 0;
		Matrix.setLookAtM(cc.viewMatrix, 0, cp.x, cp.y, 1f, cp.x, cp.y, 0f, 0f, 1.0f, 0.0f);
		Matrix.orthoM(cc.projectionMatrix, 0, -width / cc.scale / 2, width / cc.scale / 2, -height / cc.scale / 2, height
			/ cc.scale / 2, 0f, 1f);
		Matrix.multiplyMM(cc.viewProjectionMatrix, 0, cc.projectionMatrix, 0, cc.viewMatrix, 0);
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
		e.add(cp, 1);
		e.add(cc, 8);
		entities.add(e);
	}

	@Override
	public void update () {
		SevenGE.input.process();

//		angle = (float)((angle + 0.05f) % (Math.PI * 2));
//		cameraX = (float)(Math.cos(angle) * 500);
//		cameraY = (float)(Math.sin(angle) * 200);

		// cc.px = cc.x;
		// cc.py = cc.y;
		// cc.x = cameraX;
		// cc.y = cameraY;

		// animationSystem.process(entities);

		//cameraSystem.process(entities);
		if (changeme >= 30) {
			changeme = 0;
			scriptingSystem.process(entities);
		}
		changeme++;
	}

	@Override
	public void draw (float a, boolean updated) {
		rendererSystem.process(optimizedEntites, cc, oec, a, updated);
	}

	@Override
	public void dispose () {

	}

	@Override
	public void pause () {
		// music.pause();

	}

	@Override
	public void resume () {
		// music.play();

	}

	@Override
	public boolean onDoubleTap (MotionEvent me) {
		Log.d(TAG, "onDoubleTap");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent arg0) {
		Log.d(TAG, "onSingleTapConfirmed");
		return false;
	}

	@Override
	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.d(TAG, "onFling");
		return false;
	}

	@Override
	public void onLongPress (MotionEvent arg0) {
		Log.d(TAG, "onLongPress");

	}

	@Override
	public boolean onScroll (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.d(TAG, "onScroll");
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		Log.d(TAG, "touchDown" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		Log.d(TAG, "touchUp" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchMove (int x, int y, int pointer) {
		Log.d(TAG, "touchMove" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

}
