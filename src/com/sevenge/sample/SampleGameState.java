
package com.sevenge.sample;

import java.util.Random;

import android.opengl.Matrix;
import android.view.MotionEvent;

import com.sevenge.GameActivity;
import com.sevenge.GameState;
import com.sevenge.IO;
import com.sevenge.SevenGE;
import com.sevenge.assets.TextureRegion;
import com.sevenge.audio.Music;
import com.sevenge.ecs.AnimationComponent;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.CameraComponent;
import com.sevenge.ecs.CameraSystem;
import com.sevenge.ecs.Component;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.EntityManager;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.graphics.FontUtils;
import com.sevenge.input.GestureProcessor;
import com.sevenge.input.InputProcessor;

public class SampleGameState extends GameState implements InputProcessor, GestureProcessor {

	private final String TAG = "SampleGameState";

	private EntityManager mEM;
	private Component[] components = new Component[10];

	private Music music;

	private RendererSystem rendererSystem;

	private AnimationSystem animationSystem;

	private CameraSystem cameraSystem;

	private ScriptingSystem scriptingSystem;

	private int counter = 0;

	public SampleGameState (GameActivity gameActivity) {
		super(gameActivity);

		SevenGE.input.addInputProcessor(this);
		SevenGE.input.addGestureProcessor(this);

		SevenGE.assetManager.loadAssets("sample.pkg");
		SevenGE.assetManager.registerAsset("font", FontUtils.load(IO.getAssetManager(), "Fonts/OpenSansBold.ttf", 20, 2, 0));

		rendererSystem = new RendererSystem(200);
		animationSystem = new AnimationSystem(200);
		cameraSystem = new CameraSystem(null);
		scriptingSystem = new ScriptingSystem();

		mEM = new EntityManager(300, 10);
		mEM.addSystem(rendererSystem);
		mEM.addSystem(animationSystem);
		mEM.addSystem(cameraSystem);
		mEM.addSystem(scriptingSystem);

		Random rng = new Random();
		for (int i = 0; i < 200; i++) {
			Entity entity = mEM.createEntity(components, 0);
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
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("enemyBlack1");
				AnimationComponent ca = new AnimationComponent();
				ca.frameList = new TextureRegion[] {(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack1"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack2"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack3"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack4"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack5")};
				ca.durations = new int[] {500, 1000, 2000, 234, 666};
				ca.isPlaying = true;
				entity.addComponent(ca, 3);
			}

			cp.rotation = rng.nextFloat() * 360.0f;
			cp.x = rng.nextFloat() * 1000f;
			cp.y = rng.nextFloat() * 1000f;
			Matrix.setIdentityM(cp.scaleMatrix, 0);
			Matrix.scaleM(cp.scaleMatrix, 0, cs.scale, cs.scale, 1.0f);

			Matrix.setIdentityM(cp.transform, 0);
			// Matrix.translateM(cp.transform, 0, cs.textureRegion.width / 2, cs.textureRegion.height / 2, 0f);
			// Matrix.rotateM(cp.transform, 0, 0, 0f, 0f, 1.0f);
			Matrix.translateM(cp.transform, 0, cp.x, cp.y, 0f);

			entity.addComponent(cp, 0);
			entity.addComponent(cs, 1);

		}
		mEM.assignEntities();

		music = (Music)SevenGE.assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();

	}

	@Override
	public void onSurfaceChange (int width, int height) {

		CameraComponent cc = new CameraComponent();
		cc.height = height;
		cc.width = width;
		cc.scale = 1.5f;

		PositionComponent cp = new PositionComponent();
		cp.x = 0;
		cp.y = 0;
		Matrix.setLookAtM(cc.viewMatrix, 0, cp.x, cp.y, 1f, cp.x, cp.y, 0f, 0f, 1.0f, 0.0f);
		Matrix.orthoM(cc.projectionMatrix, 0, -width / cc.scale / 2, width / cc.scale / 2, -height / cc.scale / 2, height
			/ cc.scale / 2, 0f, 1f);
		Matrix.multiplyMM(cc.viewProjectionMatrix, 0, cc.projectionMatrix, 0, cc.viewMatrix, 0);
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);

		Entity camera = mEM.createEntity(components, 0);
		camera.addComponent(cc, 2);
		camera.addComponent(cp, 0);
		cameraSystem.setCamera(camera);
		rendererSystem.setCamera(camera);

	}

	@Override
	public void update () {
		cameraSystem.process();
		SevenGE.input.process();
		animationSystem.process();
		if (counter == 33) {
			counter = 0;
			scriptingSystem.process();
		}
		counter++;
	}

	@Override
	public void draw (float interpolationAlpha) {
		rendererSystem.process(interpolationAlpha);
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

	@Override
	public boolean onDoubleTap (MotionEvent me) {
		// Log.d(TAG, "onDoubleTap");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent arg0) {
		// Log.d(TAG, "onSingleTapConfirmed");
		return false;
	}

	@Override
	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// Log.d(TAG, "onFling");
		return false;
	}

	@Override
	public void onLongPress (MotionEvent arg0) {
		// Log.d(TAG, "onLongPress");

	}

	@Override
	public boolean onScroll (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// Log.d(TAG, "onScroll");
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		// Log.d(TAG, "touchDown" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		// Log.d(TAG, "touchUp" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchMove (int x, int y, int pointer) {
		// Log.d(TAG, "touchMove" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

}
