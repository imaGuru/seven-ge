
package com.sevenge.sample;

import java.util.Random;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.sevenge.GameState;
import com.sevenge.SevenGE;
import com.sevenge.assets.AssetManager;
import com.sevenge.assets.Texture;
import com.sevenge.assets.TextureRegion;
import com.sevenge.audio.Music;
import com.sevenge.ecs.AnimationComponent;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.EntityManager;
import com.sevenge.ecs.PhysicsComponent;
import com.sevenge.ecs.PhysicsSystem;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.graphics.Camera;
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

	public SampleGameState () {
		input = SevenGE.getInput();
		input.addInputProcessor(this);
		input.addGestureProcessor(this);
		assetManager = SevenGE.getAssetManager();
		assetManager.loadAssets("sample.pkg");

		rendererSystem = new RendererSystem(200);
		animationSystem = new AnimationSystem(200);
		scriptingSystem = new ScriptingSystem();
		physicsSystem = new PhysicsSystem(200);
		sceneManager = new SceneManager();

		mEM = new EntityManager(300, 10);
		mEM.registerSystem(rendererSystem);
		mEM.registerSystem(animationSystem);
		mEM.registerSystem(scriptingSystem);
		mEM.registerSystem(physicsSystem);

		Random rng = new Random();
		for (int i = 0; i < 150; i++) {
			Entity entity = mEM.createEntity(10);
			SpriteComponent cs = new SpriteComponent();
			PositionComponent cp = new PositionComponent();
			cp.rotation = rng.nextFloat() * 360.0f;
			cp.x = rng.nextFloat() * 1400f;
			cp.y = rng.nextFloat() * 1400f;
			cs.scale = 1.0f;
			float rnd = rng.nextFloat();
			if (rnd < 0.5f) {
				cs.textureRegion = (TextureRegion)assetManager.getAsset("meteorGrey_big1");
				cs.scale = 1f;
				cp.layer = 1;
				cp.parallaxFactor = 0.7f;
			} else if (rnd < 0.7f) {
				cs.textureRegion = (TextureRegion)assetManager.getAsset("meteorGrey_small2");
				cp.layer = 2;
				cp.parallaxFactor = 0.8f;
			} else if (rnd < 0.75f) {
				cp.layer = 3;
				cp.parallaxFactor = 0.9f;
				cs.textureRegion = (TextureRegion)assetManager.getAsset("meteorGrey_tiny2");
			} else {
				cp.layer = 4;
				cp.parallaxFactor = 1f;
				cs.textureRegion = (TextureRegion)assetManager.getAsset("enemyBlack1");
				AnimationComponent ca = new AnimationComponent();
				PhysicsComponent physicsComponent = new PhysicsComponent();

				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyDef.BodyType.DynamicBody;
				bodyDef.position.set(PhysicsSystem.WORLD_TO_BOX * cp.x, PhysicsSystem.WORLD_TO_BOX * cp.y);
				Body body = physicsSystem.getWorld().createBody(bodyDef);
				CircleShape dynamicCircle = new CircleShape();
				dynamicCircle.setRadius(cs.textureRegion.height / 2 * PhysicsSystem.WORLD_TO_BOX);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = dynamicCircle;
				fixtureDef.density = 2.0f;
				fixtureDef.friction = 0.5f;
				fixtureDef.restitution = 0.5f;
				body.createFixture(fixtureDef);
				physicsComponent.setBody(body);

				ca.frameList = new TextureRegion[] {(TextureRegion)assetManager.getAsset("enemyBlack1"),
					(TextureRegion)assetManager.getAsset("enemyBlack2"), (TextureRegion)assetManager.getAsset("enemyBlack3"),
					(TextureRegion)assetManager.getAsset("enemyBlack4"), (TextureRegion)assetManager.getAsset("enemyBlack5")};
				ca.durations = new int[] {500, 1000, 2000, 234, 666};
				ca.isPlaying = true;
				entity.addComponent(ca, 3);
				entity.addComponent(physicsComponent, 4);
			}

			entity.addComponent(cp, 0);
			entity.addComponent(cs, 1);

		}
		Entity e = mEM.createEntity(10);
		PhysicsComponent physicsComponent = new PhysicsComponent();
		PositionComponent positionComponent = new PositionComponent();

		int groundWidth = 1100;
		int groundHeight = 1;
		int groundX = groundWidth / 2 + 140;
		int groundY = groundHeight / 2;

		positionComponent.x = groundX;
		positionComponent.y = groundY;

		groundX *= PhysicsSystem.WORLD_TO_BOX;
		groundY *= PhysicsSystem.WORLD_TO_BOX;
		groundWidth *= PhysicsSystem.WORLD_TO_BOX;
		groundHeight *= PhysicsSystem.WORLD_TO_BOX;

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(groundX, groundY));
		Body groundBody = physicsSystem.getWorld().createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(groundWidth, groundHeight);
		groundBody.createFixture(groundBox, 0.0f);

		physicsComponent.setBody(groundBody);

		e.addComponent(positionComponent, 0);
		e.addComponent(physicsComponent, 4);

		SpriteComponent cs;
		PositionComponent cp;

		e = mEM.createEntity(10);
		cs = new SpriteComponent();
		cp = new PositionComponent();
		cp.layer = -1;
		cp.parallaxFactor = 0.1f;
		cp.rotation = 0;
		cp.x = 0;
		cp.y = 512;
		cs.scale = 1.0f;
		cs.textureRegion = new TextureRegion(512, 512, 0, 0, (Texture)assetManager.getAsset("star3-0-0"));
		e.addComponent(cp, 0);
		e.addComponent(cs, 1);

		e = mEM.createEntity(10);
		cs = new SpriteComponent();
		cp = new PositionComponent();
		cp.layer = -1;
		cp.parallaxFactor = 0.1f;
		cp.rotation = 0;
		cp.x = 0;
		cp.y = 0;
		cs.scale = 1.0f;
		cs.textureRegion = new TextureRegion(512, 512, 0, 0, (Texture)assetManager.getAsset("star3-0-1"));
		e.addComponent(cp, 0);
		e.addComponent(cs, 1);

		e = mEM.createEntity(10);
		cs = new SpriteComponent();
		cp = new PositionComponent();
		cp.layer = -1;
		cp.parallaxFactor = 0.1f;
		cp.rotation = 0;
		cp.x = 512;
		cp.y = 512;
		cs.scale = 1.0f;
		cs.textureRegion = new TextureRegion(512, 512, 0, 0, (Texture)assetManager.getAsset("star3-1-0"));
		e.addComponent(cp, 0);
		e.addComponent(cs, 1);

		e = mEM.createEntity(10);
		cs = new SpriteComponent();
		cp = new PositionComponent();
		cp.layer = -1;
		cp.parallaxFactor = 0.1f;
		cp.rotation = 0;
		cp.x = 512;
		cp.y = 0;
		cs.scale = 1.0f;
		cs.textureRegion = new TextureRegion(512, 512, 0, 0, (Texture)assetManager.getAsset("star3-1-1"));
		e.addComponent(cp, 0);
		e.addComponent(cs, 1);

		mEM.assignEntities();

		music = (Music)assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();

	}

	@Override
	public void onSurfaceChange (int width, int height) {

		camera = new Camera(width, height);
		camera.setPostion(0.0f, 0.0f);
		camera.setRotation(0.0f);
		camera.setZoom(1.0f);

		mWidth = width;
		mHeight = height;
		rendererSystem.setCamera(camera);
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
	public boolean onScale (ScaleGestureDetector detector) {
		float scale = lastScale * firstSpan / detector.getCurrentSpan();
		camera.setZoom(scale);
		Log.d("WTF", "scale" + scale + " " + firstSpan + " " + detector.getCurrentSpan());
		return true;
	}

	@Override
	public void onScaleEnd (ScaleGestureDetector detector) {
		Log.d("WTF", "end" + lastScale);
	}

	@Override
	public void onScaleBegin (ScaleGestureDetector detector) {
		lastScale = camera.getZoom();
		firstSpan = detector.getCurrentSpan();
	}

}
