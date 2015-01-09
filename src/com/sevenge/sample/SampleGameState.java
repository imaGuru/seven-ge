
package com.sevenge.sample;

import java.util.Random;

import android.view.MotionEvent;

import com.sevenge.GameState;
import com.sevenge.SevenGE;
import com.sevenge.assets.AssetManager;
import com.sevenge.assets.AudioLoader;
import com.sevenge.assets.ShaderLoader;
import com.sevenge.assets.SpriteSheetFTLoader;
import com.sevenge.assets.TextureLoader;
import com.sevenge.assets.TextureShaderProgramLoader;
import com.sevenge.audio.Music;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.EntityManager;
import com.sevenge.ecs.PhysicsSystem;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.graphics.Camera;
import com.sevenge.graphics.SpriteBatcher;
import com.sevenge.input.GestureProcessor;
import com.sevenge.input.Input;
import com.sevenge.input.InputProcessor;

public class SampleGameState extends GameState implements InputProcessor, GestureProcessor {

	private static final String TAG = "SampleGameState";

	private EntityManager mEM;
	private Music music;

	private RendererSystem rendererSystem;
	private AnimationSystem animationSystem;
	private ScriptingSystem scriptingSystem;
	private PhysicsSystem physicsSystem;
	private SceneManager sceneManager;

	private int counter = 0;
	private Camera camera;
	private int mWidth;
	private int mHeight;
	private float lastScale = 1;
	private float firstSpan;
	private AssetManager assetManager;
	private Input input;

	SpriteBatcher mSpriteBatch = new SpriteBatcher(300);

	private float[] projectionMatrix = new float[16];

	@Override
	public void load () {
		input = SevenGE.getInput();
		input.addInputProcessor(this);
		input.addGestureProcessor(this);
		assetManager = SevenGE.getAssetManager();

		rendererSystem = new RendererSystem(200);
		animationSystem = new AnimationSystem(200);
		scriptingSystem = new ScriptingSystem();
		physicsSystem = new PhysicsSystem(200);
		sceneManager = new SceneManager();

		assetManager.addLoader("spriteSheet", new SpriteSheetFTLoader(assetManager));
		assetManager.addLoader("texture", new TextureLoader(assetManager));
		assetManager.addLoader("program", new TextureShaderProgramLoader(assetManager));
		assetManager.addLoader("shader", new ShaderLoader(assetManager));
		assetManager.addLoader("audio", new AudioLoader(assetManager));

		assetManager.loadAssets("package.pkg");

		mEM = new EntityManager(300, 10);
		mEM.registerSystem(rendererSystem);
		mEM.registerSystem(animationSystem);
		mEM.registerSystem(scriptingSystem);
		mEM.registerSystem(physicsSystem);

		mWidth = SevenGE.getWidth();
		mHeight = SevenGE.getHeight();

		camera = new Camera(mWidth, mHeight);
		camera.setPostion(0.0f, 0.0f);
		camera.setRotation(0.0f);
		camera.setZoom(1.0f);

		rendererSystem.setCamera(camera);

		Random rng = new Random();
		for (int i = 0; i < 150; i++) {

		}

		mEM.assignEntities();

		music = (Music)assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();

	}

	@Override
	public void update () {
		physicsSystem.process();
		input.process();
		animationSystem.process();
		if (counter == 33) {
			counter = 0;
			scriptingSystem.process();
		}
		counter++;
		mEM.assignEntities();
		sceneManager.update();
	}

	@Override
	public void draw (float interpolationAlpha) {

		rendererSystem.process(interpolationAlpha);

	}

	@Override
	public void dispose () {
		input.removeInputProcessor(this);
		input.removeGestureProcessor(this);
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
		SevenGE.getStateManager().setCurrentState(new SampleGameState());

	}

	@Override
	public boolean onScroll (MotionEvent me1, MotionEvent me2, float distX, float distY) {
		float me2x = me2.getX();
		float me2y = me2.getY();

		float[] coords = camera.unproject((int)(me2x + distX), (int)(me2y + distY), mWidth, mHeight, camera.getCameraMatrix());
		float x1 = coords[0];
		float y1 = coords[1];
		coords = camera.unproject((int)me2x, (int)me2y, mWidth, mHeight, camera.getCameraMatrix());
		float x2 = coords[0];
		float y2 = coords[1];

		com.sevenge.utils.Vector2 camPos = camera.getPosition();
		camPos.x -= x2 - x1;
		camPos.y -= y2 - y1;
		camera.setPostion(camPos.x, camPos.y);
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

	@Override
	public boolean onScale (float currentSpan) {
		float scale = lastScale * firstSpan / currentSpan;
		camera.setZoom(Math.min(5.0f, Math.max(0.1f, scale)));
		return true;
	}

	@Override
	public void onScaleEnd (float currentSpan) {
	}

	@Override
	public void onScaleBegin (float currentSpan) {
		lastScale = camera.getZoom();
		firstSpan = currentSpan;
	}

}
